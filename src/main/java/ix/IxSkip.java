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

final class IxSkip<T> extends IxSource<T, T> {

    final int n;

    IxSkip(Iterable<T> source, int n) {
        super(source);
        this.n = n;
    }

    @Override
    public Iterator<T> iterator() {
        return new SkipIterator<T>(source.iterator(), n);
    }

    static final class SkipIterator<T> extends IxSourceIterator<T, T> {

        int n;

        SkipIterator(Iterator<T> it, int n) {
            super(it);
            this.n = n;
        }
        @Override
        protected boolean moveNext() {
            Iterator<T> it = this.it;
            int n = this.n;

            if (n != 0) {
                while (n != 0) {
                    if (it.hasNext()) {
                        it.next();
                    } else {
                        done = true;
                        return false;
                    }
                    n--;
                }
                this.n = 0;
            }
            if (it.hasNext()) {
                value = it.next();
                hasValue = true;
                return true;
            }
            done = true;
            return false;
        }


    }
}
