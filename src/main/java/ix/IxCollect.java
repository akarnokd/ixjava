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

final class IxCollect<T, C> extends IxSource<T, C> {

    final IxSupplier<C> initialFactory;

    final IxConsumer2<C, T> collector;

    IxCollect(Iterable<T> source, IxSupplier<C> initialFactory, IxConsumer2<C, T> collector) {
        super(source);
        this.initialFactory = initialFactory;
        this.collector = collector;
    }

    @Override
    public Iterator<C> iterator() {
        return new CollectorIterator<T, C>(source.iterator(), collector, initialFactory.get());
    }

    static final class CollectorIterator<T, C> extends IxSourceIterator<T, C> {

        final IxConsumer2<C, T> collector;

        CollectorIterator(Iterator<T> it, IxConsumer2<C, T> collector, C value) {
            super(it);
            this.collector = collector;
            this.value = value;
        }

        @Override
        protected boolean moveNext() {
            Iterator<T> it = this.it;

            IxConsumer2<C, T> coll = collector;

            C c = value;

            while (it.hasNext()) {
                coll.accept(c, it.next());
            }

            hasValue = true;
            done = true;
            return true;
        }
    }
}
