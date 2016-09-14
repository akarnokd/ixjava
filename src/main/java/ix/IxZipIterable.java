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

import ix.IxZipArray.ZipArrayIterator;

final class IxZipIterable<T, R> extends Ix<R> {

    final Iterable<? extends Iterable<? extends T>> sources;

    final IxFunction<? super Object[], R> zipper;

    IxZipIterable(Iterable<? extends Iterable<? extends T>> sources, IxFunction<? super Object[], R> zipper) {
        this.sources = sources;
        this.zipper = zipper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<R> iterator() {

        Iterable<? extends T>[] src = new Iterable[8];
        int n = 0;

        for (Iterable<? extends T> it : sources) {
            if (n == src.length) {
                src = Arrays.copyOf(src, n + (n >> 1));
            }
            src[n++] = it;
        }

        Iterator<T>[] iterators = new Iterator[n];
        for (int i = 0; i < n; i++) {
            iterators[i] = (Iterator<T>)src[i].iterator();
        }

        return new ZipArrayIterator<T, R>(iterators, zipper);
    }
}
