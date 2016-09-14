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

final class IxDistinct<T, K> extends IxSource<T, T> {

    final IxFunction<? super T, K> keySelector;

    IxDistinct(Iterable<T> source, IxFunction<? super T, K> keySelector) {
        super(source);
        this.keySelector = keySelector;
    }

    @Override
    public Iterator<T> iterator() {
        return new DistinctIterator<T, K>(source.iterator(), keySelector);
    }

    static final class DistinctIterator<T, K> extends IxSourceIterator<T, T> {
        final IxFunction<? super T, K> keySelector;

        final Set<K> set;

        DistinctIterator(Iterator<T> it, IxFunction<? super T, K> keySelector) {
            super(it);
            this.keySelector = keySelector;
            this.set = new HashSet<K>();
        }

        @Override
        protected boolean moveNext() {
            Iterator<T> it = this.it;

            while (it.hasNext()) {
                T v = it.next();

                K k = keySelector.apply(v);

                if (set.add(k)) {
                    value = v;
                    hasValue = true;
                    return true;
                }
            }

            done = true;
            return false;
        }
    }
}
