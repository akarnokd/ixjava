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

package ix.util;

/**
 * An action with two parameters and exception.
 * @author karnokd, 2012.01.24.
 * @param <T> the first parameter type
 * @param <V> the second parameter type
 * @param <E> the exception type
 * @since 0.96
 */
public interface Action2E<T, V, E extends Exception> {
	/**
	 * Invoke the action.
	 * @param t the first parameter
	 * @param u the second parameter
	 * @throws E the exception
	 */
	void invoke(T t, V u) throws E;
}
