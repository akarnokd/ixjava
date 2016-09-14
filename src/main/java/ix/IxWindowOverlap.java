/*
 * Copyright 2011-2016 David Karnok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ix;

import java.util.*;

final class IxWindowOverlap<T> extends IxSource<T, Ix<T>> {

    final int size;

    final int skip;

    static final Object NULL = new Object();

    IxWindowOverlap(Iterable<T> source, int size, int skip) {
        super(source);
        this.size = size;
        this.skip = skip;
    }

    @Override
    public Iterator<Ix<T>> iterator() {
        return new WindowIterator<T>(source.iterator(), size, skip);
    }

    static final class WindowIterator<T> extends IxSourceIterator<T, Ix<T>> {

        final int size;

        final int skip;

        final Queue<WindowIterable<T>> queue;

        final Queue<WindowIterable<T>> windows;

        int index;

        int headSize;

        WindowIterator(Iterator<T> it, int size, int skip) {
            super(it);
            this.size = size;
            this.skip = skip;
            this.queue = new ArrayDeque<WindowIterable<T>>();
            this.windows = new ArrayDeque<WindowIterable<T>>();
        }

        @Override
        protected boolean moveNext() {
            WindowIterable<T> w = queue.poll();
            if (w == null) {
                if (!moveMain()) {
                    done = true;
                    return false;
                }
                w = queue.poll();
            }

            value = w;
            hasValue = true;
            return true;
        }

        boolean moveMain() {
            int i = index;
            for (;;) {
                if (!it.hasNext()) {
                    return false;
                }

                T v = it.next();

                for (WindowIterable<T> c : windows) {
                    c.iterator.offer(v);
                }

                int j = headSize + 1;
                if (j == size) {
                    windows.poll();
                    headSize = j - skip;
                } else {
                    headSize = j;
                }

                if (i++ == 0) {
                    if (i == skip) {
                        index = 0;
                    } else {
                        index = i;
                    }
                    WindowIterable<T> c = new WindowIterable<T>(this, size);
                    c.iterator.offer(v);
                    queue.offer(c);
                    windows.offer(c);

                    return true;
                }

                if (i == skip) {
                    i = 0;
                }
            }
        }

        boolean moveInner() {
            int i = index;
            if (!it.hasNext()) {
                return false;
            }

            T v = it.next();

            for (WindowIterable<T> c : windows) {
                c.iterator.offer(v);
            }

            int j = headSize + 1;
            if (j == size) {
                windows.poll();
                headSize = j - skip;
            } else {
                headSize = j;
            }

            if (i++ == 0) {
                WindowIterable<T> c = new WindowIterable<T>(this, size);
                c.iterator.offer(v);
                queue.offer(c);
                windows.offer(c);
            }

            if (i == skip) {
                index = 0;
            }
            return true;
        }
    }

    static final class WindowIterable<T> extends Ix<T> {

        final WindowInnerIterator<T> iterator;

        boolean once;

        WindowIterable(WindowIterator<T> parent, int size) {
            this.iterator = new WindowInnerIterator<T>(parent);
        }

        @Override
        public Iterator<T> iterator() {
            if (!once) {
                once = true;
                return iterator;
            }
            throw new IllegalStateException("This Window Ix iterable can be consumed only once.");
        }
    }

    static final class WindowInnerIterator<T> extends IxBaseIterator<T> {

        final WindowIterator<T> parent;

        final ArrayDeque<Object> queue;

        WindowInnerIterator(WindowIterator<T> parent) {
            this.parent = parent;
            this.queue = new ArrayDeque<Object>();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected boolean moveNext() {
            Object o = queue.poll();

            if (o == null) {
                if (!parent.moveInner()) {
                    done = true;
                    return false;
                }
                o = queue.poll();
                if (o == null) {
                    done = true;
                    return false;
                }
            }

            value = o == NULL ? null : (T)o;
            hasValue = true;
            return true;
        }

        void offer(T v) {
            queue.offer(v != null ? v : NULL);
        }
    }

}
