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

final class IxGenerate<T, S> extends Ix<T> {

    final IxSupplier<S> stateFactory;

    final IxFunction2<S, IxEmitter<T>, S> generator;

    final IxConsumer<? super S> stateDisposer;

    IxGenerate(IxSupplier<S> stateFactory, IxFunction2<S, IxEmitter<T>, S> generator, IxConsumer<? super S> stateDisposer) {
        this.stateFactory = stateFactory;
        this.generator = generator;
        this.stateDisposer = stateDisposer;
    }

    @Override
    public Iterator<T> iterator() {
        return new GenerateIterator<T, S>(stateFactory.get(), generator, stateDisposer);
    }

    static final class GenerateIterator<T, S> extends IxBaseIterator<T>
    implements IxEmitter<T> {

        final IxFunction2<S, IxEmitter<T>, S> generator;

        final IxConsumer<? super S> stateDisposer;

        S state;

        GenerateIterator(S state, IxFunction2<S, IxEmitter<T>, S> generator, IxConsumer<? super S> stateDisposer) {
            this.state = state;
            this.generator = generator;
            this.stateDisposer = stateDisposer;
        }

        @Override
        protected boolean moveNext() {

            state = generator.apply(state, this);

            boolean hv = hasValue;
            boolean d = done;
            if (!hv && !d) {
                stateDisposer.accept(state);
                throw new IllegalStateException("The generator didn't call any of the onXXX methods!");
            }
            if (d) {
                stateDisposer.accept(state);
            }
            return hv;
        }

        @Override
        public void onNext(T t) {
            value = t;
            hasValue = true;
        }

        @Override
        public void onComplete() {
            done = true;
        }
    }
}
