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

final class IxMap<T, R> extends IxSource<T, R> {

    final IxFunction<? super T, ? extends R> mapper;

    IxMap(Iterable<T> source, IxFunction<? super T, ? extends R> mapper) {
        super(source);
        this.mapper = mapper;
    }

    @Override
    public Iterator<R> iterator() {
        return new MapIterator<T, R>(source.iterator(), mapper);
    }

    static final class MapIterator<T, R> implements Iterator<R> {

        final Iterator<T> it;

        final IxFunction<? super T, ? extends R> mapper;

        MapIterator(Iterator<T> it, IxFunction<? super T, ? extends R> mapper) {
            this.it = it;
            this.mapper = mapper;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public R next() {
            return mapper.apply(it.next());
        }

        @Override
        public void remove() {
            it.remove();
        }
    }
}
