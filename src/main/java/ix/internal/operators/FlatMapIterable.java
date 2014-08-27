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

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.functions.Func1;

public final class FlatMapIterable<U, T> implements Iterable<U> {
	private final Func1<? super T, ? extends Iterable<? extends U>> selector;
	/** The source sequence. */
	private final Iterable<? extends T> source;

	public FlatMapIterable(
			Func1<? super T, ? extends Iterable<? extends U>> selector,
			Iterable<? extends T> source) {
		this.selector = selector;
		this.source = source;
	}

	@Override
	public Iterator<U> iterator() {
	    final Iterator<? extends T> it = source.iterator();
	    return new Iterator<U>() {
	        /** The current selected iterator. */
	        Iterator<? extends U> sel;
	        @Override
	        public boolean hasNext() {
	            if (sel == null || !sel.hasNext()) {
	                while (!Thread.currentThread().isInterrupted()) {
	                    if (it.hasNext()) {
	                        sel = selector.call(it.next()).iterator();
	                        if (sel.hasNext()) {
	                            return true;
	                        }
	                    } else {
	                        break;
	                    }
	                }
	                return false;
	            }
	            return true;
	        }
	        
	        @Override
	        public U next() {
	            if (hasNext()) {
	                return sel.next();
	            }
	            throw new NoSuchElementException();
	        }
	        
	        @Override
	        public void remove() {
	            if (sel == null) {
	                throw new IllegalStateException();
	            }
	            sel.remove();
	        }
	        
	    };
	}
}