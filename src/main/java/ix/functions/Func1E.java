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

package ix.functions;

/**
 * The function interface which takes one parameter and returns something.
 * When <code>Func1</code> is used in a method parameter declaration, you
 * may consider using wildcards: 
 * <p><code>&lt;T, U&gt; U someMethod(Func1&lt;? super T, ? extends U&gt; f);</code></p>
 * 
 * @param <Param1> the first parameter
 * @param <Return> the return type
 * @param <E> the exception
 */
public interface Func1E<Param1, Return, E extends Exception> {
	/**
	 * The method that gets invoked with a parameter.
	 * @param param1 the parameter value
	 * @return the return object
	 * @throws E on error
	 */
	Return call(Param1 param1) throws E;
}
