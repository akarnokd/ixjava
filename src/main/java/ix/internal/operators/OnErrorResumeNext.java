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

public final class OnErrorResumeNext<T> implements Iterable<T> {
	/** The source sequences. */
	private final Iterable<? extends Iterable<? extends T>> sources;

	public OnErrorResumeNext(
			Iterable<? extends Iterable<? extends T>> sources) {
		this.sources = sources;
	}

	@Override
	public Iterator<T> iterator() {
	    final Iterator<? extends Iterable<? extends T>> iter0 = sources.iterator();
	    if (iter0.hasNext()) {
	        return new Iterator<T>() {
	            /** The current iterator. */
	            Iterator<? extends T> it = iter0.next().iterator();
	            /** The memorized iterator for the remove call. */
	            Iterator<? extends T> itForRemove = null;
	            /** The peek ahead container. */
	            final SingleContainer<Notification<? extends T>> peek = new SingleContainer<Notification<? extends T>>();
	            @Override
	            public boolean hasNext() {
	                if (peek.isEmpty()) {
	                    while (!Thread.currentThread().isInterrupted()) {
	                        try {
	                            if (it.hasNext()) {
	                                peek.add(Interactive.some(it.next()));
	                                break;
	                            } else {
	                                if (iter0.hasNext()) {
	                                    it = iter0.next().iterator();
	                                } else {
	                                    break;
	                                }
	                            }
	                        } catch (Throwable t) {
	                            if (iter0.hasNext()) {
	                                it = iter0.next().iterator();
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
	                    itForRemove = it;
	                    return Interactive.value(peek.take());
	                }
	                throw new NoSuchElementException();
	            }
	            
	            @Override
	            public void remove() {
	                if (itForRemove == null) {
	                    throw new IllegalStateException();
	                }
	                itForRemove.remove();
	                itForRemove = null;
	            }
	        };
	    }
	    return Interactive.<T>empty().iterator();
	}
}