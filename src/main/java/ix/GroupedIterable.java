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
 * The extension interface to an iterable which
 * holds a group key for its contents.
 * @param <K> the group key type
 * @param <V> the value type
 */
public interface GroupedIterable<K, V> extends Iterable<V> {
	/** @return the key of this iterable. */
	K key();
}
