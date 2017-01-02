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

/**
 * Suppress or throw when the remove() is called.
 *
 * @param <T> the value type
 */
final class IxReadOnly<T> extends IxSource<T, T> {

    final boolean silent;

    IxReadOnly(Iterable<T> source, boolean silent) {
        super(source);
        this.silent = silent;
    }

    @Override
    public Iterator<T> iterator() {
        if (silent) {
            return new ReadOnlySilentIterator<T>(source.iterator());
        }
        return new ReadOnlyThrowingIterator<T>(source.iterator());
    }

    static final class ReadOnlySilentIterator<T> implements Iterator<T> {

        final Iterator<T> source;

        ReadOnlySilentIterator(Iterator<T> source) {
            this.source = source;
        }

        @Override
        public boolean hasNext() {
            return source.hasNext();
        }

        @Override
        public T next() {
            return source.next();
        }

        @Override
        public void remove() {
            // deliberately ignored
        }
    }

    static final class ReadOnlyThrowingIterator<T> implements Iterator<T> {

        final Iterator<T> source;

        ReadOnlyThrowingIterator(Iterator<T> source) {
            this.source = source;
        }

        @Override
        public boolean hasNext() {
            return source.hasNext();
        }

        @Override
        public T next() {
            return source.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
