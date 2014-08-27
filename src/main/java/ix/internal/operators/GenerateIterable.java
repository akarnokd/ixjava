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
package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.functions.Func1;

/**
 * Iterable sequence similar to a for-loop which generates sequence of values by
 * using callback functions.
 *
 * @param <T> the value type
 */
public final class GenerateIterable<T> implements Iterable<T> {
	/** The initial value. */
	private final T seed;
	/** Function to generate the next value. */
	private final Func1<? super T, ? extends T> next;
	/** Function to test for termination condition. */
	private final Func1<? super T, Boolean> predicate;

	/**
	 * Constructor, sets the fields.
	 * @param seed the initial value
	 * @param next the function to generate the next value
	 * @param predicate function to test for termination
	 */
	public GenerateIterable(T seed, Func1<? super T, ? extends T> next,
			Func1<? super T, Boolean> predicate) {
		this.seed = seed;
		this.next = next;
		this.predicate = predicate;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        T value = seed;
	        @Override
	        public boolean hasNext() {
	            return predicate.call(value);
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                T current = value;
	                value = next.call(value);
	                return current;
	            }
	            throw new NoSuchElementException();
	        }
	        
	        @Override
	        public void remove() {
	            throw new UnsupportedOperationException();
	        }
	        
	    };
	}
}