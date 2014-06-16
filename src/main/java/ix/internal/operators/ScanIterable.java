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

public final class ScanIterable<U, T> implements Iterable<U> {
	private final Iterable<? extends T> source;
	private final Func2<? super U, ? super T, ? extends U> aggregator;
	private final U seed;

	public ScanIterable(Iterable<? extends T> source,
			Func2<? super U, ? super T, ? extends U> aggregator, U seed) {
		this.source = source;
		this.aggregator = aggregator;
		this.seed = seed;
	}

	@Override
	public Iterator<U> iterator() {
	    final Iterator<? extends T> it = source.iterator();
	    return new Iterator<U>() {
	        /** The current value. */
	        U current = seed;
	        
	        @Override
	        public boolean hasNext() {
	            return it.hasNext();
	        }
	        
	        @Override
	        public U next() {
	            current = aggregator.call(current, it.next());
	            return current;
	        }
	        
	        @Override
	        public void remove() {
	            it.remove();
	        }
	    };
	}
}