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
import java.util.Map.Entry;

final class IxExcept<T> extends IxSource<T, T> {

    final Iterable<? extends T> other;

    IxExcept(Iterable<T> source, Iterable<? extends T> other) {
        super(source);
        this.other = other;
    }

    @Override
    public Iterator<T> iterator() {
        return new ExceptIterator<T>(source.iterator(), other.iterator());
    }

    static final class ExceptIterator<T> extends IxSourceIterator<T, T> {
        final Iterator<? extends T> other;

        final LinkedHashMap<T, Boolean> set;

        Iterator<Map.Entry<T, Boolean>> setIterator;

        boolean once;

        boolean second;

        ExceptIterator(Iterator<T> it, Iterator<? extends T> other) {
            super(it);
            this.other = other;
            this.set = new LinkedHashMap<T, Boolean>();
        }

        @Override
        protected boolean moveNext() {
            LinkedHashMap<T, Boolean> secondSet = set;
            if (!once) {
                once = true;
                while (other.hasNext()) {
                    secondSet.put(other.next(), true);
                }
            }

            for (;;) {
                if (second) {
                    Iterator<Entry<T, Boolean>> sIt = setIterator;
                    while (sIt.hasNext()) {
                        Entry<T, Boolean> e = sIt.next();

                        if (e.getValue()) {
                            value = e.getKey();
                            hasValue = true;
                            return true;
                        }
                    }
                    done = true;
                    return false;
                } else {
                    Iterator<T> fIt = it;
                    while (fIt.hasNext()) {
                        T v = fIt.next();

                        Boolean b = secondSet.get(v);
                        if (b == null) {
                            value = v;
                            hasValue = true;
                            return true;
                        }
                        if (b) {
                            secondSet.put(v, false);
                        }
                    }
                    second = true;
                    setIterator = secondSet.entrySet().iterator();
                }
            }
        }
    }
}
