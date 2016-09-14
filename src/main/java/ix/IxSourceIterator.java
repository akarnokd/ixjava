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
 * A base iterator that references an upstream iterator and manages
 * the state between hasNext() and the next() calls; plus defines
 * the remove() to throw UnsupportedOperationException.
 * @param <T> the source value type
 * @param <R> the result value type
 */
public abstract class IxSourceIterator<T, R> extends IxBaseIterator<R> {

    /** The upstream's iterator. */
    protected final Iterator<T> it;

    public IxSourceIterator(Iterator<T> it) {
        this.it = it;
    }
}
