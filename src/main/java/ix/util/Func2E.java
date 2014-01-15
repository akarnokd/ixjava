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
 * The function interface which takes two parameter and returns something.
 * When <code>Func1</code> is used in a method parameter declaration, you
 * may consider using wildcards: 
 * <p><code>&lt;T, U, V> V someMethod(Func2&lt;? super T, ? super U, ? extends V> f);</code></p>
 * @author akarnokd, 2011.01.27
 * @param <Return> the return type
 * @param <Param1> the first parameter
 * @param <Param2> the second parameter
 * @param <E> the exception
 */
public interface Func2E<Param1, Param2, Return, E extends Exception> {
	/**
	 * The method that gets invoked with two parameters.
	 * @param param1 the first parameter value
	 * @param param2 the second parameter value
	 * @return the return object
	 * @throws E on error
	 */
	Return invoke(Param1 param1, Param2 param2) throws E;
}
