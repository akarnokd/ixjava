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
import rx.functions.Func1;

/**
 * Returns an iterable which returns a single true if
 * the predicate matches all elements in the source sequence
 * or false otherwise.
 *
 * @param <T> the source value type
 */
public final class AllIterable<T> implements Iterable<Boolean> {
	/** The source. */
	private final Iterable<? extends T> source;
	/** The predicate. */
	private final Func1<? super T, Boolean> predicate;
	/**
	 * Constructor, initializes the fields.
	 * @param source the source sequence
	 * @param predicate the predicate function
	 */
	public AllIterable(Iterable<? extends T> source,
			Func1<? super T, Boolean> predicate) {
		this.source = source;
		this.predicate = predicate;
	}

	@Override
	public Iterator<Boolean> iterator() {
	    return new Iterator<Boolean>() {
	        /** The source iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        /** The peek ahead container. */
	        final SingleContainer<Notification<Boolean>> peek = new SingleContainer<Notification<Boolean>>();
	        /** Completed. */
	        boolean done;
	        @Override
	        public boolean hasNext() {
	            if (peek.isEmpty() && !done) {
	                try {
	                    if (it.hasNext()) {
	                        while (it.hasNext()) {
	                            T value = it.next();
	                            if (!predicate.call(value)) {
	                                peek.add(Interactive.some(false));
	                                return true;
	                            }
	                        }
	                        peek.add(Interactive.some(true));
	                    }
	                    done = true;
	                } catch (Throwable t) {
	                    peek.add(Interactive.<Boolean>err(t));
	                    done = true;
	                } finally {
	                    Interactive.unsubscribe(it);
	                }
	            }
	            return !peek.isEmpty();
	        }
	        
	        @Override
	        public Boolean next() {
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