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

/**
 * A function callback with one input value and one output value.
 * @param <T> the input value type
 * @param <R> the output value type
 */
public interface IxFunction<T, R> {
    /**
     * Applies a function to the input value and returns an output value.
     * @param t the input value
     * @return the output value
     */
    R apply(T t);
}
