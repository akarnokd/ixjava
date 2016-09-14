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

final class IxScanSeed<T, R> extends IxSource<T, R> {

    final IxSupplier<R> seed;

    final IxFunction2<R, T, R> scanner;

    IxScanSeed(Iterable<T> source, IxSupplier<R> seed, IxFunction2<R, T, R> scanner) {
        super(source);
        this.seed = seed;
        this.scanner = scanner;
    }

    @Override
    public Iterator<R> iterator() {
        return new ScanSeedIterator<T, R>(source.iterator(), seed.get(), scanner);
    }

    static final class ScanSeedIterator<T, R> extends IxSourceIterator<T, R> {
        final IxFunction2<R, T, R> scanner;

        R accumulator;

        boolean once;

        ScanSeedIterator(Iterator<T> it, R accumulator, IxFunction2<R, T, R> scanner) {
            super(it);
            this.accumulator = accumulator;
            this.scanner = scanner;
        }

        @Override
        protected boolean moveNext() {
            if (!once) {
                once = true;
                value = accumulator;
                hasValue = true;
                return true;
            }

            if (it.hasNext()) {

                R v = scanner.apply(accumulator, it.next());

                accumulator = v;
                value = v;
                hasValue = true;
                return true;
            }

            accumulator = null;
            done = true;
            return false;
        }
    }

}
