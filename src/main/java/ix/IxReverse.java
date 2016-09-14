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

final class IxReverse<T> extends IxSource<T, T> {

    IxReverse(Iterable<T> source) {
        super(source);
    }

    @Override
    public Iterator<T> iterator() {
        return new ReverseIterator<T>(source.iterator());
    }

    static final class ReverseIterator<T> extends IxSourceIterator<T, T> {

        List<T> list;

        int index;

        ReverseIterator(Iterator<T> it) {
            super(it);
        }

        @Override
        protected boolean moveNext() {
            List<T> list = this.list;

            if (list == null) {
                list = new ArrayList<T>();
                this.list = list;
                while (it.hasNext()) {
                    list.add(it.next());
                }
                index = list.size();
            }

            int i = index;
            if (i == 0) {
                done = true;
                return false;
            }
            value = list.get(i - 1);
            hasValue = true;
            index = i - 1;
            return true;
        }
    }
}
