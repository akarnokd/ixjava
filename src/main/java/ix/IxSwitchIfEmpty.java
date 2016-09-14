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

final class IxSwitchIfEmpty<T> extends IxSource<T, T> {

    final Iterable<? extends T> other;

    IxSwitchIfEmpty(Iterable<T> source, Iterable<? extends T> other) {
        super(source);
        this.other = other;
    }

    @Override
    public Iterator<T> iterator() {
        return new SwitchIfEmptyIterator<T>(source.iterator(), other);
    }

    static final class SwitchIfEmptyIterator<T> extends IxSourceIterator<T, T> {

        final Iterable<? extends T> other;

        Iterator<? extends T> otherIterator;

        boolean nonEmpty;

        SwitchIfEmptyIterator(Iterator<T> it, Iterable<? extends T> other) {
            super(it);
            this.other = other;
        }

        @Override
        protected boolean moveNext() {
            Iterator<? extends T> ot = otherIterator;
            if (ot != null) {
                if (ot.hasNext()) {
                    value = ot.next();
                    hasValue = true;
                    return true;
                }
                done = true;
                return false;
            }

            if (nonEmpty) {
                if (it.hasNext()) {
                    value = it.next();
                    hasValue = true;
                    return true;
                }
                done = true;
                return false;
            }

            if (it.hasNext()) {
                nonEmpty = true;
                value = it.next();
                hasValue = true;
                return true;
            }

            ot = other.iterator();
            otherIterator = ot;
            if (ot.hasNext()) {
                value = ot.next();
                hasValue = true;
                return true;
            }
            done = true;
            return false;
        }

        @Override
        public void remove() {
            Iterator<? extends T> ot = otherIterator;
            if (ot != null) {
                ot.remove();
            } else {
                it.remove();
            }
        }
    }
}
