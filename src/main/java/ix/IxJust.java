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

final class IxJust<T> extends Ix<T> implements IxScalarCallable<T> {

    final T value;

    IxJust(T value) {
        this.value = value;
    }

    @Override
    public T call() {
        return value;
    }

    @Override
    public Iterator<T> iterator() {
        return new JustIterator<T>(value);
    }

    static final class JustIterator<T> implements Iterator<T> {

        final T value;

        boolean empty;

        JustIterator(T value) {
            this.value = value;
        }

        @Override
        public boolean hasNext() {
            return !empty;
        }

        @Override
        public T next() {
            if (!empty) {
                empty = true;
                return value;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
