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
 * Allows signalling values or completion in an imperative manner.
 *
 * @param <T> the value type emitted
 */
public interface IxEmitter<T> {

    /**
     * The next value to signal.
     * @param value the value to signal
     */
    void onNext(T value);

    /**
     * Indicate no more values will be generated.
     */
    void onComplete();
}
