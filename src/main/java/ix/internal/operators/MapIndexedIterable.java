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

import rx.functions.Func2;

public final class MapIndexedIterable<U, T> implements Iterable<U> {
	/** The source sequence. */
	private final Iterable<? extends T> source;
	private final Func2<? super Integer, ? super T, ? extends U> selector;

	public MapIndexedIterable(Iterable<? extends T> source,
			Func2<? super Integer, ? super T, ? extends U> selector) {
		this.source = source;
		this.selector = selector;
	}

	@Override
	public Iterator<U> iterator() {
	    final Iterator<? extends T> it = source.iterator();
	    return new Iterator<U>() {
	        /** The current counter. */
	        int count;
	        @Override
	        public boolean hasNext() {
	            return it.hasNext();
	        }
	        
	        @Override
	        public U next() {
	            return selector.call(count++, it.next());
	        }
	        
	        @Override
	        public void remove() {
	            it.remove();
	        }
	        
	    };
	}
}