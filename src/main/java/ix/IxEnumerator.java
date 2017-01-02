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
 * Represents a moveNext/current style streaming API.
 * <p>The problem with Iterator is that often next() has
 * to call hasNext or otherwise check if the stream has
 * properly moved along.
 * @param <T> the value type
 */
public interface IxEnumerator<T> {

    /**
     * Move the stream to the next element and return true,
     * or return false if the stream has ended
     * @return true if there is an item available via {@link #current()}
     * false otherwise
     */
    boolean moveNext();
    
    /**
     * Retrieves the current item after a successful {@link #moveNext()}
     * <p>Calling this method before calling moveNext() first or after
     * moveNext() returned false is undefined behavior (it may return
     * null or crash).
     * @return the current item
     */
    T current();
    
    /**
     * Remove the current item.
     * @throws UnsupportedOperationException if the upstream is read-only
     * or otherwise doesn't support removal
     */
    void remove();
}
