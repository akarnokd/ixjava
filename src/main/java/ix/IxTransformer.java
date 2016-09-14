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

final class IxTransformer<T, R> extends IxSource<T, R> {

    final IxTransform<T, R> transform;

    IxTransformer(Iterable<T> source, IxTransform<T, R> transform) {
        super(source);
        this.transform = transform;
    }

    @Override
    public Iterator<R> iterator() {
        return new TransformerIterator<T, R>(source.iterator(), transform);
    }

    static final class TransformerIterator<T, R> extends IxSourceIterator<T, R>
    implements IxConsumer<R> {

        final IxTransform<T, R> transform;

        TransformerIterator(Iterator<T> it, IxTransform<T, R> transform) {
            super(it);
            this.transform = transform;
        }

        @Override
        protected boolean moveNext() {
            int m = transform.moveNext(it, this);
            if (m == IxTransform.LAST) {
                done = true;
                return true;
            }
            if (m == IxTransform.STOP) {
                value = null;
                done = true;
                return false;
            }
            if (!hasValue) {
                throw new IllegalStateException("No value set!");
            }
            return true;
        }

        @Override
        public void accept(R t) {
            if (hasValue) {
                throw new IllegalStateException("Value already set in this turn!");
            }
            value = t;
            hasValue = true;
        }
    }
}
