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

final class IxOrderBy<T, K> extends IxSource<T, T> {

    final IxFunction<? super T, K> keySelector;

    final Comparator<? super K> comparator;

    final int flag;

    IxOrderBy(Iterable<T> source, IxFunction<? super T, K> keySelector, Comparator<? super K> comparator, int flag) {
        super(source);
        this.keySelector = keySelector;
        this.comparator = comparator;
        this.flag = flag;
    }

    @Override
    public Iterator<T> iterator() {
        return new OrderByIterator<T, K>(source.iterator(), keySelector, comparator, flag);
    }

    static final class OrderByIterator<T, K> extends IxSourceIterator<T, T> implements Comparator<T> {

        final IxFunction<? super T, K> keySelector;

        final Comparator<? super K> comparator;

        final int flag;

        List<T> values;

        int index;

        OrderByIterator(Iterator<T> it, IxFunction<? super T, K> keySelector, Comparator<? super K> comparator, int flag) {
            super(it);
            this.keySelector = keySelector;
            this.comparator = comparator;
            this.flag = flag;
        }

        @Override
        protected boolean moveNext() {

            List<T> list = values;

            if (list == null) {
                list = new ArrayList<T>();

                Iterator<T> it = this.it;

                while (it.hasNext()) {
                    list.add(it.next());
                }

                if (list.isEmpty()) {
                    done = true;
                    return false;
                }

                Collections.sort(list, this);

                values = list;
            }

            int i = index;
            if (i != list.size()) {
                index = i + 1;
                value = list.get(i);
                list.set(i, null);
                hasValue = true;
                return true;
            }
            done = true;
            return false;
        }

        @Override
        public int compare(T o1, T o2) {
            K k1 = keySelector.apply(o1);
            K k2 = keySelector.apply(o2);
            return comparator.compare(k1, k2) * flag;
        }
    }

}
