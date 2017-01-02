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

/**
 * Merges an array of Iterable items by picking the smallest from them
 * with the help of a Comparator.
 *
 * @param <T> the value type
 */
final class IxOrderedMergeArray<T> extends Ix<T> {

    final Iterable<? extends T>[] sources;

    final Comparator<? super T> comparator;

    IxOrderedMergeArray(Iterable<? extends T>[] sources, Comparator<? super T> comparator) {
        this.sources = sources;
        this.comparator = comparator;
    }

    @Override
    public Iterator<T> iterator() {
        Iterable<? extends T>[] all = sources;
        int n = all.length;
        @SuppressWarnings("unchecked")
        Iterator<? extends T>[] srcs = new Iterator[n];
        for (int i = 0; i < n; i++) {
            srcs[i] = all[i].iterator();
        }
        return new OrderedMergeIterator<T>(srcs, n, comparator);
    }

    static final class OrderedMergeIterator<T> implements Iterator<T> {

        final Iterator<? extends T>[] sources;

        final Comparator<? super T> comparator;

        final int n;

        final Object[] latest;

        static final Object EMPTY = new Object();

        static final Object DONE = new Object();

        boolean done;

        int index;

        OrderedMergeIterator(Iterator<? extends T>[] sources, int n, Comparator<? super T> comparator) {
            this.sources = sources;
            this.n = n;
            this.latest = new Object[n];
            Arrays.fill(latest, EMPTY);
            this.comparator = comparator;
            this.index = -1;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean hasNext() {
            int i = index;
            if (i < 0) {
                if (done) {
                    return false;
                }
                int count = n;
                Object[] vs = latest;
                int d = 0;
                int f = -1;
                T min = null;
                for (int j = 0; j < count; j++) {
                    Object o = vs[j];
                    if (o == DONE) {
                        d++;
                        continue;
                    } else
                    if (o == EMPTY) {
                        Iterator<? extends T> src = sources[j];
                        if (src.hasNext()) {
                            o = src.next();
                            vs[j] = o;
                        } else {
                            d++;
                            vs[j] = DONE;
                            continue;
                        }
                    }

                    if (f < 0 || comparator.compare(min, (T)o) > 0) {
                        f = j;
                        min = (T)o;
                    }
                }

                if (d == count) {
                    done = true;
                    return false;
                }

                index = f;
            }
            return true;
        }

        @Override
        public T next() {
            if (hasNext()) {
                int i = index;
                index = -1;
                @SuppressWarnings("unchecked")
                T v = (T)latest[i];
                latest[i] = EMPTY;
                return v;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
