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

final class IxSequenceEqual<T> extends IxSource<T, Boolean> {

    final Iterable<? extends T> other;

    final IxPredicate2<? super T, ? super T> comparer;

    IxSequenceEqual(Iterable<T> source, Iterable<? extends T> other,
            IxPredicate2<? super T, ? super T> comparer) {
        super(source);
        this.other = other;
        this.comparer = comparer;
    }

    @Override
    public Iterator<Boolean> iterator() {
        return new SequenceEqualIterator<T>(source.iterator(), other.iterator(), comparer);
    }

    static final class SequenceEqualIterator<T> extends IxSourceIterator<T, Boolean> {

        final Iterator<? extends T> other;

        final IxPredicate2<? super T, ? super T> comparer;

        SequenceEqualIterator(Iterator<T> it, Iterator<? extends T> other,
                IxPredicate2<? super T, ? super T> comparer) {
            super(it);
            this.other = other;
            this.comparer = comparer;
        }

        @Override
        protected boolean moveNext() {

            Iterator<T> it2 = it;

            while (it2.hasNext()) {

                T v = it2.next();

                if (other.hasNext()) {

                    T u = other.next();

                    if (!comparer.test(v, u)) {

                        value = false;
                        hasValue = true;
                        done = true;
                        return true;
                    }
                } else {
                    value = false;
                    hasValue = true;
                    done = true;
                    return true;
                }
            }
            if (other.hasNext()) {
                value = false;
                hasValue = true;
                done = true;
                return true;
            }

            value = true;
            hasValue = true;
            done = true;
            return true;
        }


    }

}
