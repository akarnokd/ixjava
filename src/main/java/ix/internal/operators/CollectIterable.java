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

package ix.internal.operators;

import java.util.*;

import rx.functions.*;

/**
 * Collects source values into a custom collection and emits that single collection.
 * @param <T> the source value type
 * @param <C> the target collection type
 * @since 0.92.3
 */
public final class CollectIterable<T, C> implements Iterable<C> {

    final Iterable<? extends T> source;
    
    final Func0<C> collectionSupplier;
    
    final Action2<C, ? super T> collector;
    
    public CollectIterable(Iterable<? extends T> source, Func0<C> collectionSupplier, Action2<C, ? super T> collector) {
        this.source = source;
        this.collectionSupplier = collectionSupplier;
        this.collector = collector;
    }
    
    @Override
    public Iterator<C> iterator() {
        C collection = collectionSupplier.call();
        
        Iterator<? extends T> it = source.iterator();
        
        return new CollectIterator<T, C>(collection, it, collector);
    }
    
    static final class CollectIterator<T, C> implements Iterator<C> {
        final Action2<C, ? super T> collector;
        
        C collection;
        
        Iterator<? extends T> it;
        
        boolean once;
        
        public CollectIterator(C collection, Iterator<? extends T> it, Action2<C, ? super T> collector) {
            this.collection = collection;
            this.it = it;
            this.collector = collector;
        }

        @Override
        public boolean hasNext() {
            if (!once) {
                once = true;
                final C c = collection;
                final Iterator<? extends T> o = it;
                final Action2<C, ? super T> a = collector;
                while (o.hasNext()) {
                    a.call(c, o.next());
                }
                return true;
            }
            return collection != null;
        }
        
        @Override
        public C next() {
            if (hasNext()) {
                C c = collection;
                collection = null;
                return c;
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
