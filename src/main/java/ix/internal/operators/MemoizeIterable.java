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

import ix.internal.util.CircularBuffer;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Memoizes the source sequence with a given buffer size.
 *
 * @param <T> the element type
 */
public final class MemoizeIterable<T> implements Iterable<T> {
	/** The buffer size. */
	private final int bufferSize;
	/** The source iterator. */
	Iterator<? extends T> it;
	/** The ring buffer of the memory. */
	final CircularBuffer<T> buffer;
	/**
	 * Constructor, sets the buffer size and takes an iterator from source.
	 * @param source the source iterable
	 * @param bufferSize the buffer size
	 */
	public MemoizeIterable(Iterable<? extends T> source, int bufferSize) {
		this.bufferSize = bufferSize;
		it = source.iterator();
		buffer = new CircularBuffer<T>(bufferSize);
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        int myHead;
	        
	        @Override
	        public boolean hasNext() {
	            return buffer.tail() > Math.max(myHead, buffer.head()) || it.hasNext();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                if (buffer.tail() == myHead) {
	                    T value = it.next();
	                    if (bufferSize > 0) {
	                        buffer.add(value);
	                    }
	                    myHead++;
	                    return value;
	                } else {
	                    myHead = Math.max(myHead, buffer.head());
	                    T value = buffer.get(myHead);
	                    myHead++;
	                    return value;
	                }
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