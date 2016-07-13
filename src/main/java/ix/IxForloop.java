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

final class IxForloop<T, R> extends Ix<R> {

    final T seed;
    
    final Pred<? super T> condition;
    
    final Func1<? super T, ? extends R> selector;
    
    final Func1<? super T, ? extends T> next;

    public IxForloop(T seed, Pred<? super T> condition, Func1<? super T, ? extends R> selector,
            Func1<? super T, ? extends T> next) {
        this.seed = seed;
        this.condition = condition;
        this.selector = selector;
        this.next = next;
    }
    
    @Override
    public Iterator<R> iterator() {
        return new ForloopIterator<T, R>(seed, condition, selector, next);
    }
    
    static final class ForloopIterator<T, R> extends IxBaseIterator<R> {
        
        T index;

        final Pred<? super T> condition;
        
        final Func1<? super T, ? extends R> selector;
        
        final Func1<? super T, ? extends T> next;

        boolean exceptFirst;
        
        public ForloopIterator(T index, Pred<? super T> condition, Func1<? super T, ? extends R> selector,
                Func1<? super T, ? extends T> next) {
            this.index = index;
            this.condition = condition;
            this.selector = selector;
            this.next = next;
        }

        @Override
        protected boolean moveNext() {
            T i = index;
            
            if (exceptFirst) {
                i = next.call(i);
                index = i;
            } else {
                exceptFirst = true;
            }
            
            if (!condition.test(i)) {
                done = true;
                return false;
            }
            
            value = selector.call(i);
            hasValue = true;
            
            return true;
        }
    }
}
