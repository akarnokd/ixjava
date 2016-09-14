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

final class IxWindowSkip<T> extends IxSource<T, Ix<T>> {

    final int size;

    final int skip;

    static final Object NULL = new Object();

    IxWindowSkip(Iterable<T> source, int size, int skip) {
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

        int index;

        final int skip;

        WindowIterable<T> current;

        WindowIterator(Iterator<T> it, int size, int skip) {
            super(it);
            this.size = size;
            this.skip = skip;
        }

        @Override
        protected boolean moveNext() {
            int i = index;
            for (;;) {
                if (!it.hasNext()) {
                    done = true;
                    return false;
                }

                T v = it.next();

                if (i++ == 0) {
                    index = i;
                    WindowIterable<T> c = new WindowIterable<T>(this, size);
                    current = c;
                    c.iterator.offer(v);
                    value = c;
                    hasValue = true;
                    return true;
                }

                WindowInnerIterator<T> ci = current.iterator;

                if (ci.offered < size) {
                    ci.offer(v);
                }

                if (i == skip) {
                    i = 0;
                }
            }
        }

        boolean moveInner() {
            if (!it.hasNext()) {
                return false;
            }

            T v = it.next();

            current.iterator.offer(v);

            index++;
            return true;
        }
    }

    static final class WindowIterable<T> extends Ix<T> {

        final WindowInnerIterator<T> iterator;

        boolean once;

        WindowIterable(WindowIterator<T> parent, int size) {
            this.iterator = new WindowInnerIterator<T>(parent, size);
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

        int remaining;

        int offered;

        WindowInnerIterator(WindowIterator<T> parent, int size) {
            this.parent = parent;
            this.queue = new ArrayDeque<Object>();
            this.remaining = size;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected boolean moveNext() {
            int r = remaining;
            if (r == 0) {
                done = true;
                return false;
            }
            Object o = queue.poll();

            if (o == null) {
                if (!parent.moveInner()) {
                    done = true;
                    return false;
                }
                o = queue.poll();
            }

            value = o == NULL ? null : (T)o;
            hasValue = true;
            remaining = r - 1;
            return true;
        }

        void offer(T t) {
            offered++;
            queue.offer(t != null ? t : NULL);
        }
    }

}
