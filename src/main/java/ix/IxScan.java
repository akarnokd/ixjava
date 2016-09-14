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

final class IxScan<T> extends IxSource<T, T> {

    final IxFunction2<T, T, T> scanner;

    IxScan(Iterable<T> source, IxFunction2<T, T, T> scanner) {
        super(source);
        this.scanner = scanner;
    }

    @Override
    public Iterator<T> iterator() {
        return new ScanIterator<T>(source.iterator(), scanner);
    }

    static final class ScanIterator<T> extends IxSourceIterator<T, T> {

        final IxFunction2<T, T, T> scanner;

        T last;

        boolean once;

        ScanIterator(Iterator<T> it, IxFunction2<T, T, T> scanner) {
            super(it);
            this.scanner = scanner;
        }

        @Override
        protected boolean moveNext() {
            if (!once) {
                if (it.hasNext()) {
                    once = true;
                    T v = it.next();

                    last = v;
                    value = v;
                    hasValue = true;
                    return true;
                }
                done = true;
                return false;
            }

            if (it.hasNext()) {
                T v = it.next();

                v = scanner.apply(last, v);
                last = v;
                value = v;
                hasValue = true;
                return true;
            }

            last = null;
            done = true;
            return false;
        }
    }
}
