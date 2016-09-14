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

final class IxFromArray<T> extends Ix<T> {

    final int start;
    final int end;
    final T[] array;

    IxFromArray(int start, int end, T[] array) {
        this.start = start;
        this.end = end;
        this.array = array;
    }

    @Override
    public Iterator<T> iterator() {
        return new FromArray<T>(start, end, array);
    }

    static final class FromArray<T> implements Iterator<T> {
        final T[] array;

        final int end;

        int index;

        FromArray(int start, int end, T[] array) {
            this.index = start;
            this.end = end;
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return index != end;
        }

        @Override
        public T next() {
            int i = index;
            if (i != end) {
                index = i + 1;
                return array[i];
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
