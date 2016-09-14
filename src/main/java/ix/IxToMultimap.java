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

final class IxToMultimap<T, K, V> extends IxSource<T, Map<K, Collection<V>>> {

    final IxFunction<? super T, ? extends K> keySelector;

    final IxFunction<? super T, ? extends V> valueSelector;

    IxToMultimap(Iterable<T> source, IxFunction<? super T, ? extends K> keySelector, IxFunction<? super T, ? extends V> valueSelector) {
        super(source);
        this.keySelector = keySelector;
        this.valueSelector = valueSelector;
    }

    @Override
    public Iterator<Map<K, Collection<V>>> iterator() {
        return new ToMapIterator<T, K, V>(source.iterator(), keySelector, valueSelector);
    }

    static final class ToMapIterator<T, K, V> extends IxSourceIterator<T, Map<K, Collection<V>>> {

        final IxFunction<? super T, ? extends K> keySelector;

        final IxFunction<? super T, ? extends V> valueSelector;

        ToMapIterator(Iterator<T> it, IxFunction<? super T, ? extends K> keySelector, IxFunction<? super T, ? extends V> valueSelector) {
            super(it);
            this.keySelector = keySelector;
            this.valueSelector = valueSelector;
        }

        @Override
        protected boolean moveNext() {
            Iterator<T> it = this.it;

            IxFunction<? super T, ? extends K> keySelector = this.keySelector;

            IxFunction<? super T, ? extends V> valueSelector = this.valueSelector;

            Map<K, Collection<V>> result = new HashMap<K, Collection<V>>();

            while (it.hasNext()) {
                T t = it.next();

                K k = keySelector.apply(t);

                Collection<V> coll = result.get(k);
                if (coll == null) {
                    coll = new ArrayList<V>();
                    result.put(k, coll);
                }

                V v = valueSelector.apply(t);

                coll.add(v);
            }

            value = result;
            hasValue = true;
            done = true;
            return true;
        }
    }
}
