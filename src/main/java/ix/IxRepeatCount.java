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

final class IxRepeatCount<T> extends Ix<T> {

    final T value;

    final long count;

    IxRepeatCount(T value, long count) {
        this.value = value;
        this.count = count;
    }

    @Override
    public Iterator<T> iterator() {
        return new RepeatCountIterator<T>(value, count);
    }

    static final class RepeatCountIterator<T> implements Iterator<T> {

        final T value;

        long count;

        RepeatCountIterator(T value, long count) {
            this.value = value;
            this.count = count;
        }

        @Override
        public boolean hasNext() {
            return count != 0L;
        }

        @Override
        public T next() {
            long c = count;
            if (c != 0L) {
                count = c - 1;
                return value;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
