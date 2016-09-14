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

final class IxJoin<T> extends IxSource<T, String> {

    final CharSequence separator;

    IxJoin(Iterable<T> source, CharSequence separator) {
        super(source);
        this.separator = separator;
    }

    @Override
    public Iterator<String> iterator() {
        return new JoinIterator<T>(source.iterator(), separator);
    }

    static final class JoinIterator<T> extends IxSourceIterator<T, String> {
        final CharSequence separator;

        JoinIterator(Iterator<T> it, CharSequence separator) {
            super(it);
            this.separator = separator;
        }

        @Override
        protected boolean moveNext() {
            CharSequence sep = separator;

            Iterator<T> it = this.it;

            StringBuilder b = new StringBuilder();

            if (it.hasNext()) {
                b.append(it.next());

                while (it.hasNext()) {
                    b.append(sep);
                    b.append(it.next());
                }
            }

            value = b.toString();
            hasValue = true;
            done = true;
            return true;
        }
    }

}
