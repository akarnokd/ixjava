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

/**
 * Iterable sequence which repeates the same value indefinitely.
 *
 * @param <T> the value type
 */
public final class RepeatIterable<T> implements Iterable<T> {
	/** The value to repeat. */
	private final T value;
	/**
	 * Constructor, sets the value.
	 * @param value the value to repeat
	 */
	public RepeatIterable(T value) {
		this.value = value;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        @Override
	        public boolean hasNext() {
	            return true;
	        }
	        @Override
	        public T next() {
	            return value;
	        }
	        @Override
	        public void remove() {
	            throw new UnsupportedOperationException();
	        }
	    };
	}
}