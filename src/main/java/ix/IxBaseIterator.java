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
 * the state between hasNext() and the next() calls.
 * @param <T> the source value type
 * @param <R> the result value type
 */
abstract class IxBaseIterator<T, R> implements Iterator<R> {

    protected final Iterator<T> it;
    
    protected boolean hasValue;
    
    protected boolean done;
    
    protected R value;

    public IxBaseIterator(Iterator<T> it) {
        this.it = it;
    }
    
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
    public R next() {
        if (!hasValue && !hasNext()) {
            return Ix.noelements();
        }
        R v = value;
        hasValue = false;
        value = null;
        return v;
    }
    
    @Override
    public void remove() {
        Ix.unsupported();
    }
}
