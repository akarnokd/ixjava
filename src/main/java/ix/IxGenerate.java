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
import rx.functions.*;

final class IxGenerate<T, S> extends Ix<T> {

    final Func0<S> stateFactory;
    
    final Func2<S, Observer<T>, S> generator;
    
    final Action1<? super S> stateDisposer;
    
    public IxGenerate(Func0<S> stateFactory, Func2<S, Observer<T>, S> generator, Action1<? super S> stateDisposer) {
        this.stateFactory = stateFactory;
        this.generator = generator;
        this.stateDisposer = stateDisposer;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new GenerateIterator<T, S>(stateFactory.call(), generator, stateDisposer);
    }
    
    static final class GenerateIterator<T, S> extends IxBaseIterator<T>
    implements Observer<T> {

        final Func2<S, Observer<T>, S> generator;
        
        final Action1<? super S> stateDisposer;

        S state;
        
        public GenerateIterator(S state, Func2<S, Observer<T>, S> generator, Action1<? super S> stateDisposer) {
            this.state = state;
            this.generator = generator;
            this.stateDisposer = stateDisposer;
        }

        @Override
        protected boolean moveNext() {
            
            state = generator.call(state, this);
            
            boolean hv = hasValue;
            boolean d = done;
            if (!hv && !d) {
                stateDisposer.call(state);
                throw new IllegalStateException("The generator didn't call any of the onXXX methods!");
            }
            if (d) {
                stateDisposer.call(state);
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
            stateDisposer.call(state);
            
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
