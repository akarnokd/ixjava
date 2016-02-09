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
package ix.internal.operators;

import ix.internal.util.SingleContainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterable sequence that returns a single true if the source sequence contains any
 * element.
 * 
 * @param <T> the element type
 */
public final class AnyIterable<T> implements Iterable<Boolean> {
	/** The source sequence. */
	private final Iterable<T> source;

	public AnyIterable(Iterable<T> source) {
		this.source = source;
	}

	@Override
	public Iterator<Boolean> iterator() {
	    return new Iterator<Boolean>() {
	        /** The source's iterator. */
	        Iterator<T> it = source.iterator();
	        final SingleContainer<Boolean> peek = new SingleContainer<Boolean>();
	        /** Query once. */
	        boolean once = true;
	        @Override
	        public boolean hasNext() {
	            if (once) {
	                once = false;
	                if (peek.isEmpty()) {
	                    peek.add(it.hasNext());
	                }
	            }
	            return !peek.isEmpty();
	        }
	        
	        @Override
	        public Boolean next() {
	            if (hasNext()) {
	                return peek.take();
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