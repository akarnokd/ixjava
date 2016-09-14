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

final class IxAverageDouble extends IxSource<Number, Double> {

    IxAverageDouble(Iterable<Number> source) {
        super(source);
    }

    @Override
    public Iterator<Double> iterator() {
        return new AverageFloatIterator(source.iterator());
    }

    static final class AverageFloatIterator extends IxSourceIterator<Number, Double> {

        AverageFloatIterator(Iterator<Number> it) {
            super(it);
        }

        @Override
        protected boolean moveNext() {
            Iterator<Number> it = this.it;

            double accumulator = 0f;
            long count = 0;

            if (!it.hasNext()) {
                done = true;
                return false;
            }

            do {
                accumulator += it.next().doubleValue();
                count++;
            } while (it.hasNext());

            value = accumulator / count;
            hasValue = true;
            done = true;
            return true;
        }
    }
}
