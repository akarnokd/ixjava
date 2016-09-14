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

final class IxSplit extends Ix<String> {

    final String string;

    final String by;

    IxSplit(String string, String by) {
        this.string = string;
        this.by = by;
    }

    @Override
    public Iterator<String> iterator() {
        return new SplitIterator(string, by);
    }

    static final class SplitIterator extends IxBaseIterator<String> {
        final String string;

        final String by;

        int index;

        SplitIterator(String string, String by) {
            this.string = string;
            this.by = by;
        }

        @Override
        protected boolean moveNext() {
            int i = index;
            int j = string.indexOf(by, i);

            if (j < 0) {
                value = string.substring(i);
                hasValue = true;
                done = true;
                return true;
            }

            hasValue = true;
            index = j + by.length();
            value = string.substring(i, j);
            return true;
        }
    }
}
