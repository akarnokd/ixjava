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
import java.util.concurrent.Callable;

final class IxFlattenIterable<T, R> extends IxSource<T, R> {

    final IxFunction<? super T, ? extends Iterable<? extends R>> mapper;

    IxFlattenIterable(Iterable<T> source, IxFunction<? super T, ? extends Iterable<? extends R>> mapper) {
        super(source);
        this.mapper = mapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<R> iterator() {
        if (source instanceof Callable) {
            return (Iterator<R>)(mapper.apply(checkedCall((Callable<T>)source)).iterator());
        }
        return new FlattenIterator<T, R>(source.iterator(), mapper);
    }

    static final class FlattenIterator<T, R> extends IxSourceIterator<T, R> {

        final IxFunction<? super T, ? extends Iterable<? extends R>> mapper;

        Iterator<? extends R> current;

        FlattenIterator(Iterator<T> it, IxFunction<? super T, ? extends Iterable<? extends R>> mapper) {
            super(it);
            this.mapper = mapper;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected boolean moveNext() {
            Iterator<? extends R> c = current;

            while (c == null) {
                if (it.hasNext()) {
                    Iterable<? extends R> inner = mapper.apply(it.next());
                    if (inner instanceof Callable) {
                        value = checkedCall((Callable<R>)inner);
                        hasValue = true;
                        return true;
                    }

                    c = inner.iterator();

                    if (c.hasNext()) {
                        current = c;
                        break;
                    } else {
                        c = null;
                    }
                } else {
                    done = true;
                    return false;
                }
            }

            value = c.next();
            hasValue = true;

            if (!c.hasNext()) {
                current = null;
            }
            return true;
        }

    }

}
