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
 * Split into buffers when the predicate returns true and neither
 * buffer contains the item.
 *
 * @param <T> the value type
 */
final class IxBufferSplit<T> extends IxSource<T, List<T>> {

    final IxPredicate<? super T> predicate;

    IxBufferSplit(Iterable<T> source, IxPredicate<? super T> predicate) {
        super(source);
        this.predicate = predicate;
    }

    @Override
    public Iterator<List<T>> iterator() {
        return new BufferSplitIterator<T>(source.iterator(), predicate);
    }

    static final class BufferSplitIterator<T> implements Iterator<List<T>> {

        final Iterator<T> source;

        final IxPredicate<? super T> predicate;

        boolean done;

        List<T> buffer;

        BufferSplitIterator(Iterator<T> source, IxPredicate<? super T> predicate) {
            this.source = source;
            this.predicate = predicate;
        }

        @Override
        public boolean hasNext() {
            List<T> b = buffer;
            if (b == null) {
                if (done) {
                    return false;
                }

                b = new ArrayList<T>();

                Iterator<T> src = source;
                while (src.hasNext()) {
                    T v = src.next();

                    if (predicate.test(v)) {
                        buffer = b;
                        return true;
                    }

                    b.add(v);
                }

                if (b.isEmpty()) {
                    done = true;
                    return false;
                }

                buffer = b;
            }
            return true;
        }

        @Override
        public List<T> next() {
            if (hasNext()) {
                List<T> b = buffer;
                buffer = null;
                return b;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
