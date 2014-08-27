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

/**
 * Iterable sequence that takes the first elements of the source sequence.
 *
 * @param <T> the value type
 */
public final class TakeIterable<T> implements Iterable<T> {
	/** The number of items to take. */
	private final int num;
	/** The source sequence. */
	private final Iterable<? extends T> source;
	/**
	 * Constructor, initializes the fields.
	 * @param num the number of items to take
	 * @param source the source sequence
	 */
	public TakeIterable(int num, Iterable<? extends T> source) {
		this.num = num;
		this.source = source;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The counter. */
	        int count;
	        /** The source iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        @Override
	        public boolean hasNext() {
	            return count < num && it.hasNext();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                count++;
	                return it.next();
	            }
	            throw new NoSuchElementException();
	        }
	        
	        @Override
	        public void remove() {
	            it.remove();
	        }
	    };
	}
}