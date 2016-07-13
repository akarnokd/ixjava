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

import java.util.*;

final class IxRepeatPredicate<T> extends Ix<T> {

    final T value;
    
    final Pred0 stopPredicate;
    
    public IxRepeatPredicate(T value, Pred0 stopPredicate) {
        this.value = value;
        this.stopPredicate = stopPredicate;
    }

    @Override
    public Iterator<T> iterator() {
        return new RepeatPredicateIterator<T>(value, stopPredicate);
    }

    static final class RepeatPredicateIterator<T> extends IxBaseIterator<T> {

        final T valueToRepeat;
        
        final Pred0 stopPredicate;
        
        public RepeatPredicateIterator(T value, Pred0 stopPredicate) {
            this.valueToRepeat = value;
            this.stopPredicate = stopPredicate;
        }
        
        @Override
        protected boolean moveNext() {
            if (!stopPredicate.getAsBoolean()) {
                value = valueToRepeat;
                hasValue = true;
                return true;
            }
            done = true;
            return false;
        }
        
    }
}
