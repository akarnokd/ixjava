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

final class IxCharacters extends Ix<Integer> {

    final CharSequence source;

    final int start;

    final int end;

    IxCharacters(CharSequence source, int start, int end) {
        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new CharactersIterator(source, start, end);
    }

    static final class CharactersIterator implements Iterator<Integer> {

        final CharSequence source;

        final int end;

        int index;

        CharactersIterator(CharSequence source, int start, int end) {
            this.source = source;
            this.index = start;
            this.end = end;
        }

        @Override
        public boolean hasNext() {
            return index != end;
        }

        @Override
        public Integer next() {
            int i = index;
            if (i != end) {
                index = i + 1;
                return (int)source.charAt(i);
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
