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
package ix;

/**
 * The base interface for an Enumerable-Enumerator
 * pair of iteration method where the
 * enumerator has <code>next()</code> to
 * advance the iteration and <code>current()</code>
 * to return the current element. An example
 * from normal Java for such
 * kind of iteration is the <code>java.sql.ResultSet</code>.
 * @param <T> the element type
 */
public interface Enumerator<T> {
	/**
	 * Tries to move to the next element.
	 * Only this method may throw an exception.
	 * @return false if there are no more elements
	 */
	boolean next();
	/**
	 * Returns the current element. If the
	 * enumeration was not started via the <code>next()</code>
	 * method, or the enumeration has already finished and <code>next()</code>
	 * returned false, a <code>NoSuchElementException</code> is thrown.
	 * Other than these, this method should not throw any exception.
	 * @return the current element.
	 */
	T current();
}
