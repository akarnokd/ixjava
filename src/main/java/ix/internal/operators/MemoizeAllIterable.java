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

import ix.internal.util.LinkedBuffer;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class MemoizeAllIterable<T> implements Iterable<T> {
	/** The source sequence. */
	private final Iterator<? extends T> it;
	private final LinkedBuffer<T> buffer;

	public MemoizeAllIterable(Iterator<? extends T> it,
			LinkedBuffer<T> buffer) {
		this.it = it;
		this.buffer = buffer;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The element count. */
	        int count = 0;
	        /** The current node pointer. */
	        LinkedBuffer.N<T> pointer = buffer.head;
	        @Override
	        public boolean hasNext() {
	            return count < buffer.size || it.hasNext();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                if (count < buffer.size) {
	                    T value = pointer.next.value;
	                    pointer = pointer.next;
	                    count++;
	                    return value;
	                } else {
	                    T value = it.next();
	                    buffer.add(value);
	                    count++;
	                    pointer = pointer.next;
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