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

final class IxBuffer<T> extends IxSource<T, List<T>> {

    final int size;

    IxBuffer(Iterable<T> source, int size) {
        super(source);
        this.size = size;
    }

    @Override
    public Iterator<List<T>> iterator() {
        return new BufferIterator<T>(source.iterator(), size);
    }

    static final class BufferIterator<T> extends IxSourceIterator<T, List<T>> {
        final int size;

        BufferIterator(Iterator<T> it, int size) {
            super(it);
            this.size = size;
        }

        @Override
        protected boolean moveNext() {
            int s = size;

            Iterator<T> it = this.it;

            List<T> list = new ArrayList<T>();

            while (s != 0 && it.hasNext()) {
                list.add(it.next());
                s--;
            }

            if (list.isEmpty()) {
                done = true;
                return false;
            }
            value = list;
            hasValue = true;
            if (s != 0) {
                done = true;
            }
            return true;
        }


    }
}
