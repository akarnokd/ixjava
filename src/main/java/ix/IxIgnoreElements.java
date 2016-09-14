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

final class IxIgnoreElements<T> extends IxSource<T, T> {

    IxIgnoreElements(Iterable<T> source) {
        super(source);
    }

    @Override
    public Iterator<T> iterator() {
        return new IgnoreElementsIterator<T>(source.iterator());
    }

    static final class IgnoreElementsIterator<T> extends IxSourceIterator<T, T> {

        IgnoreElementsIterator(Iterator<T> it) {
            super(it);
        }

        @Override
        protected boolean moveNext() {
            Iterator<T> it = this.it;

            while (it.hasNext()) {
                it.next();
            }

            done = true;
            return false;
        }

    }
}
