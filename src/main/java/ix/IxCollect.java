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

    IxCollect(Ix<T> source, IxSupplier<C> initialFactory, IxConsumer2<C, T> collector) {
        super(source);
        this.initialFactory = initialFactory;
        this.collector = collector;
    }

    @Override
    public Iterator<C> iterator() {
        return new CollectorIterator<T, C>(source.iterator(), collector, initialFactory.get());
    }

    @Override
    public IxEnumerator<C> enumerator() {
        return new CollectorEnumerator<T, C>(source.enumerator(), collector, initialFactory.get());
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
    
    static final class CollectorEnumerator<T, C> implements IxEnumerator<C> {
        
        final IxEnumerator<T> source;
        
        final IxConsumer2<C, T> collector;
        
        C value;
        
        boolean once;
        
        CollectorEnumerator(IxEnumerator<T> source, IxConsumer2<C, T> collector, C value) {
            this.source = source;
            this.collector = collector;
            this.value = value;
        }
        
        @Override
        public boolean moveNext() {
            if (!once) {
                once = true;
                
                C c = value;
                
                IxEnumerator<T> src = source;
                IxConsumer2<C, T> coll = collector;
                
                while (src.moveNext()) {
                    coll.accept(c, src.current());
                }
                
                return true;
            }
            value = null;
            return false;
        }
        
        @Override
        public C current() {
            return value;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
