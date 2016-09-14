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

final class IxBufferOverlap<T> extends IxSource<T, List<T>> {

    final int size;

    final int skip;

    IxBufferOverlap(Iterable<T> source, int size, int skip) {
        super(source);
        this.size = size;
        this.skip = skip;
    }

    @Override
    public Iterator<List<T>> iterator() {
        return new BufferIterator<T>(source.iterator(), size, skip);
    }

    static final class BufferIterator<T> extends IxSourceQueuedIterator<T, List<T>, List<T>>
    implements IxConsumer2<List<T>, T> {
        final int size;

        final int skip;

        int index;

        BufferIterator(Iterator<T> it, int size, int skip) {
            super(it);
            this.size = size;
            this.skip = skip;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected boolean moveNext() {
            Iterator<T> it = this.it;

            int s = size;
            int k = skip;

            int i = index;

            while (it.hasNext()) {
                if (i == 0) {
                    offer(new ArrayList<T>());
                }
                T v = it.next();
                foreach(this, v);
                if (++i == k) {
                    i = 0;
                }

                if (((List<T>)peek()).size() == s) {
                    break;
                }
            }
            index = i;

            List<T> list = fromObject(poll());
            if (list == null) {
                done = true;
                return false;
            }

            value = list;
            hasValue = true;
            return true;
        }

        @Override
        public void accept(List<T> t1, T t2) {
            t1.add(t2);
        }
    }
}
