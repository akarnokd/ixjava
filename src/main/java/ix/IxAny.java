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

final class IxAny<T> extends IxSource<T, Boolean> {

    final IxPredicate<? super T> predicate;

    IxAny(Iterable<T> source, IxPredicate<? super T> predicate) {
        super(source);
        this.predicate = predicate;
    }

    @Override
    public Iterator<Boolean> iterator() {
        return new AnyIterator<T>(source.iterator(), predicate);
    }

    static final class AnyIterator<T> extends IxSourceIterator<T, Boolean> {

        final IxPredicate<? super T> predicate;

        AnyIterator(Iterator<T> it, IxPredicate<? super T> predicate) {
            super(it);
            this.predicate = predicate;
        }

        @Override
        protected boolean moveNext() {
            Iterator<T> it = this.it;
            IxPredicate<? super T> p = predicate;

            while (it.hasNext()) {
                if (p.test(it.next())) {
                    hasValue = true;
                    value = true;
                    done = true;
                    return true;
                }
            }

            hasValue = true;
            value = false;
            done = true;
            return true;
        }

    }
}
