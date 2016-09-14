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

final class IxToMap<T, K, V> extends IxSource<T, Map<K, V>> {

    final IxFunction<? super T, ? extends K> keySelector;

    final IxFunction<? super T, ? extends V> valueSelector;

    IxToMap(Iterable<T> source, IxFunction<? super T, ? extends K> keySelector, IxFunction<? super T, ? extends V> valueSelector) {
        super(source);
        this.keySelector = keySelector;
        this.valueSelector = valueSelector;
    }

    @Override
    public Iterator<Map<K, V>> iterator() {
        return new ToMapIterator<T, K, V>(source.iterator(), keySelector, valueSelector);
    }

    static final class ToMapIterator<T, K, V> extends IxSourceIterator<T, Map<K, V>> {

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

            Map<K, V> result = new HashMap<K, V>();

            while (it.hasNext()) {
                T t = it.next();

                K k = keySelector.apply(t);

                V v = valueSelector.apply(t);

                result.put(k, v);
            }

            value = result;
            hasValue = true;
            done = true;
            return true;
        }
    }
}
