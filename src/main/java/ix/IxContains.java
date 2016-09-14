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

final class IxContains<T> extends IxSource<T, Boolean> {

    final Object o;

    IxContains(Iterable<T> source, Object o) {
        super(source);
        this.o = o;
    }

    @Override
    public Iterator<Boolean> iterator() {
        return new ContainsIterator<T>(source.iterator(), o);
    }

    static final class ContainsIterator<T> extends IxSourceIterator<T, Boolean> {

        final Object o;

        ContainsIterator(Iterator<T> it, Object o) {
            super(it);
            this.o = o;
        }

        @Override
        protected boolean moveNext() {
            Iterator<T> it = this.it;
            Object o = this.o;

            while (it.hasNext()) {
                T v = it.next();
                if (o == v || (o != null && o.equals(v))) {
                    value = true;
                    hasValue = true;
                    done = true;
                    return true;
                }
            }

            value = false;
            hasValue = true;
            done = true;
            return true;
        }
    }

}
