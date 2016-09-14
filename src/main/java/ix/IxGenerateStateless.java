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

final class IxGenerateStateless<T> extends Ix<T> {

    final IxConsumer<IxEmitter<T>> generator;

    IxGenerateStateless(IxConsumer<IxEmitter<T>> generator) {
        this.generator = generator;
    }

    @Override
    public Iterator<T> iterator() {
        return new GenerateIterator<T>(generator);
    }

    static final class GenerateIterator<T> extends IxBaseIterator<T>
    implements IxEmitter<T> {

        final IxConsumer<IxEmitter<T>> generator;

        GenerateIterator(IxConsumer<IxEmitter<T>> generator) {
            this.generator = generator;
        }

        @Override
        protected boolean moveNext() {

            generator.accept(this);

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
        public void onComplete() {
            done = true;
        }
    }
}
