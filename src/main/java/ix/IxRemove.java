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

final class IxRemove<T> extends IxSource<T, T> {

    final IxPredicate<? super T> predicate;

    IxRemove(Iterable<T> source, IxPredicate<? super T> predicate) {
        super(source);
        this.predicate = predicate;
    }

    @Override
    public Iterator<T> iterator() {
        return new RemoveIterator<T>(source.iterator(), predicate);
    }

    static final class RemoveIterator<T> extends IxSourceIterator<T, T> {
        final IxPredicate<? super T> predicate;

        RemoveIterator(Iterator<T> it, IxPredicate<? super T> predicate) {
            super(it);
            this.predicate = predicate;
        }

        @Override
        protected boolean moveNext() {

            for (;;) {
                if (!it.hasNext()) {
                    done = true;
                    return false;
                }

                T v = it.next();

                if (predicate.test(v)) {
                    it.remove();
                } else {
                    value = v;
                    hasValue = true;
                    return true;
                }
            }
        }

        @Override
        public void remove() {
            it.remove();
        }
    }

}
