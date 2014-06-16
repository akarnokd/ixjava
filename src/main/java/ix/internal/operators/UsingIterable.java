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

import ix.CloseableIterator;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.functions.Func0;
import rx.functions.Func1;

public final class UsingIterable<T, U extends Closeable> implements Iterable<T> {
	private final Func0<U> resource;
	private final Func1<? super U, Iterable<? extends T>> usage;

	public UsingIterable(Func0<U> resource,
			Func1<? super U, Iterable<? extends T>> usage) {
		this.resource = resource;
		this.usage = usage;
	}

	@Override
	public Iterator<T> iterator() {
	    final U c = resource.call();
	    return new CloseableIterator<T>() {
	        /** The iterator. */
	        final Iterator<? extends T> it = usage.call(c).iterator();
	        /** Run once the it has no more elements. */
	        final AtomicBoolean once = new AtomicBoolean();
	        @Override
	        public boolean hasNext() {
	            if (it.hasNext()) {
	                return true;
	            }
	            unsubscribe();
	            return false;
	        }
	        
	        @Override
	        public boolean isUnsubscribed() {
	        	return once.get();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                return it.next();
	            }
	            throw new NoSuchElementException();
	        }
	        @Override
	        public void remove() {
	            it.remove();
	        }
	        @Override
	        public void unsubscribe() {
	            if (once.compareAndSet(false, true)) {
	                try {
	                    c.close();
	                } catch (IOException ex) {
	                    // ignored
	                }
	            }
	        }
	    };
	}
}