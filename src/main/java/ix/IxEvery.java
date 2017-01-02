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
 * Emits every Nth item from upstream.
 *
 * @param <T> the value type
 */
final class IxEvery<T> extends IxSource<T, T> {

    final int nth;

    IxEvery(Iterable<T> source, int nth) {
        super(source);
        this.nth = nth;
    }

    @Override
    public Iterator<T> iterator() {
        return new EveryIterator<T>(source.iterator(), nth);
    }

    static final class EveryIterator<T> implements Iterator<T> {

        final Iterator<T> source;

        final int nth;

        boolean done;

        boolean hasValue;

        EveryIterator(Iterator<T> it, int nth) {
            this.source = it;
            this.nth = nth;
        }

        @Override
        public boolean hasNext() {
            if (done) {
                return false;
            }
            if (!hasValue) {
                int i = nth - 1;

                Iterator<T> src = source;

                while (i != 0 && src.hasNext()) {
                    src.next();
                    i--;
                }

                if (src.hasNext()) {
                    hasValue = true;
                    return true;
                }

                done = true;
                return false;
            }
            return true;
        }

        @Override
        public T next() {
            if (hasNext()) {
                hasValue = false;
                return source.next();
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            source.remove();
        }
    }
}
