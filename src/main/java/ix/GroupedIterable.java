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

import java.util.LinkedList;
import java.util.List;

/**
 * Contains a sequence values with an associated key used by the groupBy operator.
 * @param <K> the group key type
 * @param <V> the value type
 */
public final class GroupedIterable<K, V> extends Iterables<V> {
	/** The group key. */
	protected final K key;
	/** The values in the group. */
	protected final List<V> values;
	/**
	 * Constructs a new grouped iterable with the given key.
	 * @param key the group key
	 */
	public GroupedIterable(K key) {
		super(new LinkedList<V>());
		this.key = key;
		this.values = (List<V>)this.it;
	}
	public K getKey() {
		return key;
	}
	/**
	 * Adds one element to the values.
	 * @param value the value
	 */
	public void add(V value) {
		values.add(value);
	}
	/**
	 * Add the values of the target iterable.
	 * @param values the values to add
	 */
	public void add(Iterable<V> values) {
		for (V v : values) {
			this.values.add(v);
		}
	}
}
