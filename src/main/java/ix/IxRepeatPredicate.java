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

final class IxRepeatPredicate<T> extends Ix<T> {

    final T value;

    final long count;

    final IxBooleanSupplier stopPredicate;

    IxRepeatPredicate(T value, long count, IxBooleanSupplier stopPredicate) {
        this.value = value;
        this.stopPredicate = stopPredicate;
        this.count = count;
    }

    @Override
    public Iterator<T> iterator() {
        return new RepeatPredicateIterator<T>(value, count, stopPredicate);
    }

    static final class RepeatPredicateIterator<T> extends IxBaseIterator<T> {

        final T valueToRepeat;

        long count;

        final IxBooleanSupplier stopPredicate;

        RepeatPredicateIterator(T value, long count, IxBooleanSupplier stopPredicate) {
            this.valueToRepeat = value;
            this.stopPredicate = stopPredicate;
            this.count = count;
        }

        @Override
        protected boolean moveNext() {
            long c = count--;
            if (c != 0L && !stopPredicate.getAsBoolean()) {
                value = valueToRepeat;
                hasValue = true;
                return true;
            }
            done = true;
            return false;
        }

    }
}
