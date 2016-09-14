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

import java.util.Iterator;

final class IxAggregate<T> extends IxSource<T, T> {

    final IxFunction2<T, T, T> aggregator;

    IxAggregate(Iterable<T> source, IxFunction2<T, T, T> aggregator) {
        super(source);
        this.aggregator = aggregator;
    }

    @Override
    public Iterator<T> iterator() {
        return new AggregateIterator<T>(source.iterator(), aggregator);
    }

    static final class AggregateIterator<T> extends IxSourceIterator<T, T> {

        final IxFunction2<T, T, T> aggregator;

        AggregateIterator(Iterator<T> it, IxFunction2<T, T, T> aggregator) {
            super(it);
            this.aggregator = aggregator;
        }

        @Override
        protected boolean moveNext() {
            Iterator<T> it = this.it;
            IxFunction2<T, T, T> f = aggregator;

            if (it.hasNext()) {
                T acc = it.next();
                while (it.hasNext()) {
                    acc = f.apply(acc, it.next());
                }
                value = acc;
                hasValue = true;
                done = true;
                return true;
            }
            return false;
        }
    }

}
