/*
 * Copyright 2017 Dmytro Khmelenko
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

final class IxCountDouble<T> extends IxSource<T, Double> {

    IxCountDouble(Iterable<T> source) {
        super(source);
    }

    @Override
    public Iterator<Double> iterator() {
        return new CountDoubleIterator<T>(source.iterator());
    }

    static final class CountDoubleIterator<T> extends IxSourceIterator<T, Double> {

        CountDoubleIterator(Iterator<T> it) {
            super(it);
        }

        @Override
        protected boolean moveNext() {
            double c = 0;

            Iterator<T> it = this.it;

            while (it.hasNext()) {
                it.next();
                c++;
            }

            value = c;
            hasValue = true;
            done = true;
            return true;
        }
    }
}
