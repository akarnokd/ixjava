/*
 * Copyright 2011-2014 David Karnok
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
package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterable sequence which returns values from a range in an array of items.
 *
 * @param <T> the value type
 */
public final class PartialIterable<T> implements Iterable<T> {
	/** The source index, inclusive. */
	private final int from;
	/** The array of values. */
	private final T[] ts;
	/** The destination index, exclusive. */
	private final int to;
	/**
	 * Constructor, initalizes the fields.
	 * @param from the source index, inclusive
	 * @param ts the array of values
	 * @param to the destination index, exclusive
	 */
	public PartialIterable(int from, T[] ts, int to) {
		this.from = from;
		this.ts = ts;
		this.to = to;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The current location. */
	        int index = from;
	        /** The lenght. */
	        final int size = ts.length;
	        @Override
	        public boolean hasNext() {
	            return index < size && index < to;
	        }
	        @Override
	        public T next() {
	            if (hasNext()) {
	                return ts[index++];
	            }
	            throw new NoSuchElementException();
	        }
	        @Override
	        public void remove() {
	            throw new UnsupportedOperationException();
	        }
	    };
	}
}