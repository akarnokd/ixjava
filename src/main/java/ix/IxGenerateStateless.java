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

import rx.Observer;
import rx.functions.Action1;

final class IxGenerateStateless<T> extends Ix<T> {

    final Action1<Observer<T>> generator;
    
    public IxGenerateStateless(Action1<Observer<T>> generator) {
        this.generator = generator;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new GenerateIterator<T>(generator);
    }
    
    static final class GenerateIterator<T> extends IxBaseIterator<T>
    implements Observer<T> {

        final Action1<Observer<T>> generator;
        
        public GenerateIterator(Action1<Observer<T>> generator) {
            this.generator = generator;
        }

        @Override
        protected boolean moveNext() {
            
            generator.call(this);
            
            boolean hv = hasValue;
            if (!hv && !done) {
                throw new IllegalStateException("The generator didn't call any of the onXXX methods!");
            }
            return hv;
        }
        
        @Override
        public void onNext(T t) {
            value = t;
            hasValue = true;
        }
        
        @Override
        public void onError(Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            if (e instanceof Error) {
                throw (Error)e;
            }
            throw new RuntimeException(e);
        }
        
        @Override
        public void onCompleted() {
            done = true;
        }
    }
}
