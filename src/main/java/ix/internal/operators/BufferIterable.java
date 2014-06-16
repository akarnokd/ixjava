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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import rx.Notification;

public final class BufferIterable<T> implements Iterable<List<T>> {
	private final Iterable<? extends T> source;
	private final int bufferSize;

	public BufferIterable(Iterable<? extends T> source, int bufferSize) {
		this.source = source;
		this.bufferSize = bufferSize;
	}

	@Override
	public Iterator<List<T>> iterator() {
	    return new Iterator<List<T>>() {
	        /** The source iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        /** The current buffer. */
	        final SingleContainer<Notification<List<T>>> peek = new SingleContainer<Notification<List<T>>>();
	        /** Did the source finish? */
	        boolean done;
	        @Override
	        public boolean hasNext() {
	            if (peek.isEmpty() && !done) {
	                try {
	                    if (it.hasNext()) {
	                        try {
	                            List<T> buffer = new ArrayList<T>();
	                            while (it.hasNext() && buffer.size() < bufferSize) {
	                                buffer.add(it.next());
	                            }
	                            if (buffer.size() > 0) {
	                                peek.add(Interactive.some(buffer));
	                            }
	                        } catch (Throwable t) {
	                            done = true;
	                            peek.add(Interactive.<List<T>>err(t));
	                        }
	                    } else {
	                        done = true;
	                    }
	                } finally {
	                    Interactive.unsubscribe(it);
	                }
	            }
	            return !peek.isEmpty();
	        }
	        
	        @Override
	        public List<T> next() {
	            if (hasNext()) {
	                return Interactive.value(peek.take());
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