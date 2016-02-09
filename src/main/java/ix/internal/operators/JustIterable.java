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
package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterable sequence with a specific single value.
 *
 * @param <T> the value type
 */
public final class JustIterable<T> implements Iterable<T> {
	/** The value to return. */
	private final T value;
	/**
	 * Constructor with the single value.
	 * @param value the value
	 */
	public JustIterable(T value) {
		this.value = value;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** Return the only element? */
	        boolean first = true;
	        @Override
	        public boolean hasNext() {
	            return first;
	        }
	        
	        @Override
	        public T next() {
	            if (first) {
	                first = false;
	                return value;
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