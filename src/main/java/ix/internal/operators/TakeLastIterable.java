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

import rx.Notification;

public final class TakeLastIterable<T> implements Iterable<T> {
	private final Iterable<? extends T> source;
	private final int num;

	public TakeLastIterable(Iterable<? extends T> source, int num) {
		this.source = source;
		this.num = num;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The source iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        /** The temporary buffer. */
	        final CircularBuffer<Notification<? extends T>> buffer = new CircularBuffer<Notification<? extends T>>(num);
	        @Override
	        public boolean hasNext() {
	            try {
	                while (it.hasNext()) {
	                    buffer.add(Interactive.some(it.next()));
	                }
	            } catch (Throwable t) {
	                buffer.add(Interactive.<T>err(t));
	            } finally {
	                Interactive.unsubscribe(it);
	            }
	            return !buffer.isEmpty();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                return Interactive.value(buffer.take());
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