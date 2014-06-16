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

import rx.functions.Func0;
import rx.functions.Func2;

public final class FilterIndexedIterable<T> implements Iterable<T> {
	private final Iterable<? extends T> source;
	private final Func0<? extends Func2<? super Integer, ? super T, Boolean>> predicateFactory;

	public FilterIndexedIterable(
			Iterable<? extends T> source,
			Func0<? extends Func2<? super Integer, ? super T, Boolean>> predicateFactory) {
		this.source = source;
		this.predicateFactory = predicateFactory;
	}

	@Override
	public Iterator<T> iterator() {
	    final Func2<? super Integer, ? super T, Boolean> predicate = predicateFactory.call();
	    final Iterator<? extends T> it = source.iterator();
	    return new Iterator<T>() {
	        /** The current element count. */
	        int count;
	        /** The temporary store for peeked elements. */
	        final SingleContainer<T> peek = new SingleContainer<T>();
	        @Override
	        public boolean hasNext() {
	            if (peek.isEmpty()) {
	                while (it.hasNext()) {
	                    T value = it.next();
	                    if (predicate.call(count, value)) {
	                        peek.add(value);
	                        count++;
	                        return true;
	                    }
	                    count++;
	                }
	                return false;
	            }
	            return true;
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                return peek.take();
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