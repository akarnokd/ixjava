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
import java.util.concurrent.Callable;

final class IxFlattenArrayIterable<T> extends Ix<T> {

    final Iterable<T>[] sources;

    IxFlattenArrayIterable(Iterable<T>[] sources) {
        this.sources = sources;
    }

    @Override
    public Iterator<T> iterator() {
        return new FlattenIterator<T>(sources);
    }

    static final class FlattenIterator<T> extends IxBaseIterator<T> {

        final Iterable<T>[] sources;

        Iterator<? extends T> current;

        int index;

        FlattenIterator(Iterable<T>[] sources) {
            this.sources = sources;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected boolean moveNext() {
            Iterator<? extends T> c = current;

            while (c == null) {
                int i = index;
                if (i != sources.length) {
                    Iterable<? extends T> inner = sources[i];
                    index = i + 1;
                    if (inner instanceof Callable) {
                        value = checkedCall(((Callable<T>)inner));
                        hasValue = true;
                        return true;
                    }

                    c = inner.iterator();

                    if (c.hasNext()) {
                        current = c;
                        break;
                    } else {
                        c = null;
                    }
                } else {
                    done = true;
                    return false;
                }
            }

            value = c.next();
            hasValue = true;

            if (!c.hasNext()) {
                current = null;
            }
            return true;
        }

    }

}
