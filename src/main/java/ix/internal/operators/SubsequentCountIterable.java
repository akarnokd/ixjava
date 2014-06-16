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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public final class SubsequentCountIterable<T> implements
		Iterable<Iterable<T>> {
	private final Iterable<? extends T> source;
	private final int count;

	public SubsequentCountIterable(Iterable<? extends T> source, int count) {
		this.source = source;
		this.count = count;
	}

	@Override
	public Iterator<Iterable<T>> iterator() {
	    // get the first count-1 elements
	    final LinkedList<T> ll = new LinkedList<T>();
	    final Iterator<? extends T> it = source.iterator();
	    int cnt = 0;
	    try {
	        while (it.hasNext() && cnt < count - 1) {
	            ll.add(it.next());
	            cnt++;
	        }
	    } finally {
	        Interactive.unsubscribe(it);
	    }
	    if (cnt < count - 1) {
	        return Interactive.<Iterable<T>>empty().iterator();
	    }
	    return new Iterator<Iterable<T>>() {
	        @Override
	        public boolean hasNext() {
	            return it.hasNext();
	        }
	        @Override
	        public Iterable<T> next() {
	            if (hasNext()) {
	                ll.add(it.next());
	                ll.removeFirst();
	                return new ArrayList<T>(ll);
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