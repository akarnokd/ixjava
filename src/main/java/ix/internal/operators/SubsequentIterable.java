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

import ix.Pair;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class SubsequentIterable<T> implements
		Iterable<Pair<T, T>> {
	/** The source sequence. */
	private final Iterable<? extends T> source;

	public SubsequentIterable(Iterable<? extends T> source) {
		this.source = source;
	}

	@Override
	public Iterator<Pair<T, T>> iterator() {
	    final Iterator<? extends T> it = source.iterator();
	    if (!it.hasNext()) {
	        return Interactive.<Pair<T, T>>empty().iterator();
	    }
	    final T flast = it.next();
	    return new Iterator<Pair<T, T>>() {
	        /** The last source value. */
	        T last = flast;
	        @Override
	        public boolean hasNext() {
	            return it.hasNext();
	        }
	        @Override
	        public Pair<T, T> next() {
	            if (hasNext()) {
	                T curr = it.next();
	                Pair<T, T> ret = Pair.of(last, curr);
	                last = curr;
	                return ret;
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