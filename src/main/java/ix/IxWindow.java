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

final class IxWindow<T> extends IxSource<T, Ix<T>> {

    final int size;

    static final Object NULL = new Object();

    IxWindow(Iterable<T> source, int size) {
        super(source);
        this.size = size;
    }

    @Override
    public Iterator<Ix<T>> iterator() {
        return new WindowIterator<T>(source.iterator(), size);
    }

    static final class WindowIterator<T> extends IxSourceIterator<T, Ix<T>> {

        final int size;

        int index;

        WindowIterable<T> current;

        WindowIterator(Iterator<T> it, int size) {
            super(it);
            this.size = size;
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

                WindowIterable<T> c;
                if (i++ == 0) {
                    if (i == size) {
                        index = 0;
                    } else {
                        index = i;
                    }
                    c = new WindowIterable<T>(this, size);
                    current = c;
                    c.iterator.queue.offer(v != null ? v : NULL);
                    value = c;
                    hasValue = true;
                    return true;
                }

                current.iterator.queue.offer(v != null ? v : NULL);

                if (i == size) {
                    i = 0;
                }
            }
        }

        boolean moveInner() {
            if (!it.hasNext()) {
                return false;
            }

            T v = it.next();

            current.iterator.queue.offer(v != null ? v : NULL);

            int i = index + 1;
            if (i == size) {
                current = null;
                index = 0;
            } else {
                index = i;
            }
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

        WindowInnerIterator(WindowIterator<T> parent, int remaining) {
            this.parent = parent;
            this.queue = new ArrayDeque<Object>();
            this.remaining = remaining;
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
    }

}
