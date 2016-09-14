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

/**
 * A base iterator that manages
 * the state between hasNext() and the next() calls; plus defines
 * the remove() to throw UnsupportedOperationException.
 * @param <R> the result value type
 */
public abstract class IxBaseIterator<R> implements Iterator<R> {

    /** Indicates a value is available for consumption. */
    protected boolean hasValue;

    /** Indicates there are no more data available. */
    protected boolean done;

    /** The current value if hasValue is true. */
    protected R value;

    /**
     * Move the stream forward by a single element.
     * @return what the hasNext should return
     */
    protected abstract boolean moveNext();

    @Override
    public final boolean hasNext() {
        boolean b = hasValue;
        if (!b) {
            if (!done) {
                return moveNext();
            }
        }
        return b;
    }

    @Override
    public final R next() {
        if (!hasValue && !hasNext()) {
            throw new NoSuchElementException();
        }
        R v = value;
        hasValue = false;
        value = null;
        return v;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
