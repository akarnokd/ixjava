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
 * Combines subsequent values into a single value that is then returned.
 * @param <T> the source value type
 * @since 0.92.3
 */
public final class ReduceIterable<T> implements Iterable<T> {

    final Iterable<? extends T> source;
    
    final Func2<T, T, T> reducer;
    
    public ReduceIterable(Iterable<? extends T> source, Func2<T, T, T> reducer) {
        this.source = source;
        this.reducer = reducer;
    }
    
    @Override
    public Iterator<T> iterator() {
        Iterator<? extends T> it = source.iterator();
        
        return new ReduceIterator<T>(it, reducer);
    }
    
    static final class ReduceIterator<T> implements Iterator<T> {
        final Func2<T, T, T> reducer;
        
        Iterator<? extends T> it;
        
        boolean once;
        
        boolean hasValue;
        
        T result;
        
        public ReduceIterator(Iterator<? extends T> it, Func2<T, T, T> reducer) {
            this.it = it;
            this.reducer = reducer;
        }

        @Override
        public boolean hasNext() {
            if (!once) {
                once = true;
                
                final Iterator<? extends T> o = it;
                final Func2<T, T, T> f = reducer;
                
                if (o.hasNext()) {
                    T r = o.next();
                    
                    while (o.hasNext()) {
                        r = f.call(r, o.next());
                    }
                    
                    result = r;
                    hasValue = true;
                }
            }
            return hasValue;
        }
        
        @Override
        public T next() {
            if (hasNext()) {
                T c = result;
                result = null;
                hasValue = false;
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
