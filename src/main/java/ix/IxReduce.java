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

final class IxReduce<T, C> extends IxSource<T, C> {

    final IxSupplier<C> initialFactory;

    final IxFunction2<C, T, C> reducer;

    IxReduce(Iterable<T> source, IxSupplier<C> initialFactory, IxFunction2<C, T, C> reducer) {
        super(source);
        this.initialFactory = initialFactory;
        this.reducer = reducer;
    }

    @Override
    public Iterator<C> iterator() {
        return new CollectorIterator<T, C>(source.iterator(), reducer, initialFactory.get());
    }

    static final class CollectorIterator<T, C> extends IxSourceIterator<T, C> {

        final IxFunction2<C, T, C> reducer;

        CollectorIterator(Iterator<T> it, IxFunction2<C, T, C> reducer, C value) {
            super(it);
            this.reducer = reducer;
            this.value = value;
        }

        @Override
        protected boolean moveNext() {
            Iterator<T> it = this.it;

            IxFunction2<C, T, C> f = reducer;

            C c = value;

            while (it.hasNext()) {
                c = f.apply(c, it.next());
            }

            value = c;
            hasValue = true;
            done = true;
            return true;
        }
    }
}
