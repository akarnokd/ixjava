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

public final class MaterializeIterable<T> implements
		Iterable<Notification<T>> {
	private final Iterable<? extends T> source;

	public MaterializeIterable(Iterable<? extends T> source) {
		this.source = source;
	}

	@Override
	public Iterator<Notification<T>> iterator() {
	    final Iterator<? extends T> it = source.iterator();
	    return new Iterator<Notification<T>>() {
	        /** The peeked value or exception. */
	        final SingleContainer<Notification<T>> peek = new SingleContainer<Notification<T>>();
	        /** The source iterator threw an exception. */
	        boolean broken;
	        @Override
	        public boolean hasNext() {
	            if (!broken) {
	                try {
	                    if (peek.isEmpty()) {
	                        if (it.hasNext()) {
	                            T t = it.next();
	                            peek.add(Interactive.some(t));
	                        } else {
	                            peek.add(Interactive.<T>none());
	                            broken = true;
	                        }
	                    }
	                } catch (Throwable t) {
	                    broken = true;
	                    peek.add(Interactive.<T>err(t));
	                }
	            }
	            return !peek.isEmpty();
	        }
	        
	        @Override
	        public Notification<T> next() {
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