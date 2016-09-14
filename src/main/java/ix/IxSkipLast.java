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

final class IxSkipLast<T> extends IxSource<T, T> {

    final int n;

    IxSkipLast(Iterable<T> source, int n) {
        super(source);
        this.n = n;
    }

    @Override
    public Iterator<T> iterator() {
        return new SkipLastIterator<T>(source.iterator(), n);
    }

    static final class SkipLastIterator<T> extends IxSourceQueuedIterator<T, T, T> {

        final int n;

        int size;

        SkipLastIterator(Iterator<T> it, int n) {
            super(it);
            this.n = n;
        }

        @Override
        protected boolean moveNext() {
            int s = size;
            int n = this.n;

            Iterator<T> it = this.it;
            if (!it.hasNext()) {
                done = true;
                return false;
            }

            if (s != n) {
                while (s != n) {
                    offer(toObject(it.next()));
                    if (!it.hasNext()) {
                        done = true;
                        return false;
                    }
                    s++;
                }
                size = s;
            }

            value = fromObject(poll());
            offer(toObject(it.next()));

            hasValue = true;

            return true;
        }

    }
}
