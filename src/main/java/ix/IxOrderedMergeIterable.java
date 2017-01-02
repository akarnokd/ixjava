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

import ix.IxOrderedMergeArray.OrderedMergeIterator;

/**
 * Merges an array of Iterable items by picking the smallest from them
 * with the help of a Comparator.
 *
 * @param <T> the value type
 */
final class IxOrderedMergeIterable<T> extends Ix<T> {

    final Iterable<? extends Iterable<? extends T>> sources;

    final Comparator<? super T> comparator;

    IxOrderedMergeIterable(Iterable<? extends Iterable<? extends T>> sources, Comparator<? super T> comparator) {
        this.sources = sources;
        this.comparator = comparator;
    }

    @Override
    public Iterator<T> iterator() {
        @SuppressWarnings("unchecked")
        Iterator<? extends T>[] srcs = new Iterator[8];
        int n = 0;

        for (Iterable<? extends T> iter : sources) {
            if (n == srcs.length) {
                srcs = Arrays.copyOf(srcs, n + (n >> 2));
            }
            srcs[n++] = iter.iterator();
        }

        return new OrderedMergeIterator<T>(srcs, n, comparator);
    }
}
