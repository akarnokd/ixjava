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
 * A base iterator that extends a custom ArrayDeque, references an upstream iterator
 * and manages the state between hasNext() and the next() calls; plus defines
 * the remove() to throw UnsupportedOperationException.
 * @param <T> the source value type
 * @param <U> the queued element types
 * @param <R> the result value type
 */
public abstract class IxSourceQueuedIterator<T, U, R>
extends IxSourceIterator<T, R> {

    protected static final Object NULL = new Object();

    private Object[] array;

    private int producerIndex;

    private int consumerIndex;

    public IxSourceQueuedIterator(Iterator<T> it) {
        super(it);
    }

    /**
     * Cast the value into an object and turn
     * a null value into a sentinel value.
     * @param value the value to cast
     * @return the cast value, not null
     * @see IxSourceQueuedIterator#fromObject(Object)
     */
    protected final Object toObject(U value) {
        return value != null ? value : NULL;
    }

    /**
     * Cast the object back to a typed value.
     * @param value the value to cast back
     * @return the typed value, maybe null
     */
    @SuppressWarnings("unchecked")
    protected final U fromObject(Object value) {
        return value == NULL ? null : (U)value;
    }

    protected final boolean offer(Object value) {
        if (value == null) {
            throw new NullPointerException("The queue doesn't support null values.");
        }
        Object[] a = array;
        if (a == null) {
            a = new Object[8];
            array = a;
        }
        int mask = a.length - 1;
        int pi = producerIndex;

        int offset = pi & mask;
        if (a[offset] != null) {
            int newLen = mask * 2 + 2;
            Object[] b = new Object[newLen];
            System.arraycopy(a, offset, b, 0, mask + 1 - offset);
            System.arraycopy(a, 0, b, offset, offset);
            b[mask + 1] = value;

            array = b;
            consumerIndex = 0;
            producerIndex = mask + 2;
        } else {
            a[offset] = value;
            producerIndex = pi + 1;
        }
        return true;
    }

    protected final Object poll() {
        Object[] a = array;
        if (a != null) {
            int m = a.length - 1;
            int ci = consumerIndex;
            int offset = ci & m;

            Object v = a[offset];
            if (v != null) {
                a[offset] = null;
                consumerIndex = ci + 1;
            }
            return v;
        }
        return null;
    }

    protected final Object peek() {
        Object[] a = array;
        if (a != null) {
            int m = a.length - 1;
            int ci = consumerIndex;
            int offset = ci & m;

            return a[offset];
        }
        return null;
    }

    protected final boolean isEmpty() {
        return consumerIndex == producerIndex;
    }

    protected final void clear() {
        array = null;
        consumerIndex = 0;
        producerIndex = 0;
    }

    protected final <S> void foreach(IxConsumer2<? super U, S> action, S state) {
        Object[] a = array;
        if (a != null) {
            int m = a.length - 1;
            int pi = producerIndex;
            for (int ci = consumerIndex; ci != pi; ci++) {
                int offset = ci & m;
                Object o = a[offset];
                action.accept(fromObject(o), state);
            }
        }
    }
}
