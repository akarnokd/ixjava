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

final class IxMinMax<T> extends IxSource<T, T> {

    final Comparator<? super T> comparator;

    final int flag;

    IxMinMax(Iterable<T> source, Comparator<? super T> comparator, int flag) {
        super(source);
        this.comparator = comparator;
        this.flag = flag;
    }

    @Override
    public Iterator<T> iterator() {
        return new MinMaxIterator<T>(source.iterator(), comparator, flag);
    }

    static final class MinMaxIterator<T> extends IxSourceIterator<T, T> {

        final Comparator<? super T> comparator;

        final int flag;

        MinMaxIterator(Iterator<T> it, Comparator<? super T> comparator, int flag) {
            super(it);
            this.comparator = comparator;
            this.flag = flag;
        }

        @Override
        protected boolean moveNext() {
            T v;

            Iterator<T> it = this.it;

            if (!it.hasNext()) {
                done = true;
                return false;
            }

            v = it.next();

            Comparator<? super T> f = comparator;
            int g = flag;

            while (it.hasNext()) {
                T w = it.next();
                if (f.compare(v, w) * g > 0) {
                    v = w;
                }
            }

            value = v;
            hasValue = true;
            done = true;
            return true;
        }

    }

}
