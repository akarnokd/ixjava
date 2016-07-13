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

import rx.functions.Func1;

final class IxDistinctUntilChanged<T, K> extends IxSource<T, T> {

    final Func1<? super T, K> keySelector;
    
    final Pred2<? super K, ? super K> comparer;
    
    public IxDistinctUntilChanged(Iterable<T> source, Func1<? super T, K> keySelector,
            Pred2<? super K, ? super K> comparer) {
        super(source);
        this.keySelector = keySelector;
        this.comparer = comparer;
    }

    @Override
    public Iterator<T> iterator() {
        return new DistinctUntilChangedIterator<T, K>(source.iterator(), keySelector, comparer);
    }

    static final class DistinctUntilChangedIterator<T, K> extends IxSourceIterator<T, T> {
        
        final Func1<? super T, K> keySelector;
        
        final Pred2<? super K, ? super K> comparer;

        K last;
        
        boolean once;
        
        public DistinctUntilChangedIterator(Iterator<T> it, Func1<? super T, K> keySelector,
                Pred2<? super K, ? super K> comparer) {
            super(it);
            this.keySelector = keySelector;
            this.comparer = comparer;
        }

        @Override
        protected boolean moveNext() {
            Iterator<T> it = this.it;
            
            K prev = last;
            K curr = null;
            
            while (it.hasNext()) {
                T v = it.next();
                curr = keySelector.call(v);

                if (!once) {
                    once = true;
                    
                    last = curr;
                    
                    value = v;
                    hasValue = true;
                    return true;
                }
                
                if (!comparer.test(prev, curr)) {
                    last = curr;
                    
                    value = v;
                    hasValue = true;
                    return true;
                }
                
                prev = curr;
            }
            
            
            done = true;
            return false;
        }

        
    }
}
