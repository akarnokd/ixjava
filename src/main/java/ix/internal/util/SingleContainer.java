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

package ix.internal.util;

/**
 * Helper class that stores a single element.
 * The caller may add a new element only when the container is empty and
 * the caller may take the contained element if there is one.
 * Use the <code>isEmpty()</code> to check for the status
 * The add and take methods might throw <code>IllegalStateException</code>, 
 * which should indicate a library bug.
 * Typically used by the Interactive methods to help them conform with the Iterable contract,
 * e.g., <code>hasNext()</code> is idempotent, but <code>next()</code> might 
 * be called without <code>hasNext()</code> to be called at all.
 * The container is not thread-safe.
 * @param <T> the contained element type
 */
public class SingleContainer<T> {
	/** The currently stored value. */
	T value;
	/** The state. */
	boolean empty = true;
	/**
	 * Add a new value. Might throw an <code>IllegalStateException</code> when the container already has a value.
	 * @param value the new value
	 */
	public void add(T value) {
		if (empty) {
			this.value = value;
			empty = false;
		} else {
			throw new IllegalStateException("occupied");
		}
	}
	/**
	 * @return the content or throws an <code>IllegalStateException</code> when the container is empty.
	 */
	public T take() {
		if (!empty) {
			T v = value;
			value = null;
			empty = true;
			return v;
		}
		throw new IllegalStateException("empty");
	}
	/**
	 * @return true if there is nothing contained
	 */
	public boolean isEmpty() {
		return empty;
	}
}
