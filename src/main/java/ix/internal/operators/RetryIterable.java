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

import ix.internal.util.SingleContainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.Notification;

public final class RetryIterable<T> implements Iterable<T> {
	private final int count;
	private final Iterable<? extends T> source;

	public RetryIterable(int count, Iterable<? extends T> source) {
		this.count = count;
		this.source = source;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The retry count. */
	        int retries = count;
	        /** The peek store. */
	        final SingleContainer<Notification<? extends T>> peek = new SingleContainer<Notification<? extends T>>();
	        /** The current iterator. */
	        Iterator<? extends T> it = source.iterator();
	        @Override
	        public boolean hasNext() {
	            if (peek.isEmpty()) {
	                while (it.hasNext()) {
	                    try {
	                        peek.add(Interactive.some(it.next()));
	                        break;
	                    } catch (Throwable t) {
	                        if (retries-- > 0) {
	                            it = source.iterator();
	                        } else {
	                            peek.add(Interactive.<T>err(t));
	                            break;
	                        }
	                    }
	                }
	            }
	            return !peek.isEmpty();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                return Interactive.value(peek.take());
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