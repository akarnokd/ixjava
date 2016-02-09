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

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.functions.Action0;

public final class DoOnCompletedIterable<T> implements Iterable<T> {
	private final Action0 action;
	/** The source sequence. */
	private final Iterable<? extends T> source;

	public DoOnCompletedIterable(Action0 action,
			Iterable<? extends T> source) {
		this.action = action;
		this.source = source;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        /** After the last. */
	        boolean last;
	        @Override
	        public boolean hasNext() {
	            if (!it.hasNext()) {
	                if (!last) {
	                    last = true;
	                    action.call();
	                }
	                return false;
	            }
	            return true;
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
	        
	    };
	}
}