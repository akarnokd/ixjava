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


import java.util.Iterator;
import java.util.LinkedList;

/**
 * The default implementation for a grouped iterable
 * which holds the values in a <code>LinkedList</code>.
 * @param <K> the key type
 * @param <V> the value type
 * @author akarnokd, 2011.02.03.
 */
public class DefaultGroupedIterable<K, V> implements GroupedIterable<K, V> {
	/** The key. */
	private final K key;
	/** The group content. */
	private final LinkedList<V> values = new LinkedList<V>();
	/**
	 * Constructor.
	 * @param key the group key
	 */
	public DefaultGroupedIterable(K key) {
		this.key = key;
	}
	@Override
	public K key() {
		return key;
	}
	@Override
	public Iterator<V> iterator() {
		return values.iterator();
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
