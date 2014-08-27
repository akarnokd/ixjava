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

public final class CountIterable<T> implements Iterable<Integer> {
	/** The source sequence. */
	private final Iterable<T> source;

	public CountIterable(Iterable<T> source) {
		this.source = source;
	}

	@Override
	public Iterator<Integer> iterator() {
	    final Iterator<T> it = source.iterator();
	    return new Iterator<Integer>() {
	        /** The peek ahead container. */
	        final SingleContainer<Notification<Integer>> peek = new SingleContainer<Notification<Integer>>();
	        /** Computation already done. */
	        boolean done;
	        @Override
	        public boolean hasNext() {
	            if (!done) {
	                if (peek.isEmpty()) {
	                    int count = 0;
	                    try {
	                        while (it.hasNext()) {
	                            it.next();
	                            count++;
	                        }
	                        peek.add(Interactive.some(count));
	                    } catch (Throwable t) {
	                        peek.add(Interactive.<Integer>err(t));
	                    } finally {
	                        done = true;
	                        Interactive.unsubscribe(it);
	                    }
	                }
	            }
	            return !peek.isEmpty();
	        }
	        
	        @Override
	        public Integer next() {
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