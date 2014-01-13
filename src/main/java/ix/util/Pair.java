/*
 * Copyright 2011-2013 David Karnok
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
 * A pair of two objects.
 * @author akarnokd, 2011.03.21.
 * @param <T> the first type
 * @param <U> the second type
 */
public final class Pair<T, U> {
	/** The first object. */
	public final T first;
	/** The second object. */
	public final U second;
	/**
	 * Construct a pair.
	 * @param first the first object
	 * @param second the second object
	 */
	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
	}
	/**
	 * Construct a pair.
	 * @param <T> the first type
	 * @param <U> the second type
	 * @param first the first object
	 * @param second the second object
	 * @return the pair
	 */
	public static <T, U> Pair<T, U> of(T first, U second) {
		return new Pair<T, U>(first, second);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair<?, ?>) {
			Pair<?, ?> that = (Pair<?, ?>)obj;
			return 
			(this.first == that.first || (this.first != null && this.first.equals(that.first)))
			&& (this.second == that.second || (this.second != null && this.second.equals(that.second)));
		}
		return false;
	}
	@Override
	public int hashCode() {
		return (17 + (first != null ? first.hashCode() : 0)) * 31 + (second != null ? second.hashCode() : 0);
	}
	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}
}
