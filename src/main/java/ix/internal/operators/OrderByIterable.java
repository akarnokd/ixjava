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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import rx.functions.Func1;

/**
 * Iterable sequence which sorts the source (finite) sequence accoring to the given
 * key selector and key comparator functions.
 *
 * @param <T> the source type
 * @param <U> the key type
 */
public final class OrderByIterable<T, U> implements Iterable<T> {
	/** The source sequence. */
	private final Iterable<? extends T> source;
	/** The key comparator function. */
	private final Comparator<? super U> keyComparator;
	/** The key selector function. */
	private final Func1<? super T, ? extends U> keySelector;
	/**
	 * Constructor, initializes the fields.
	 * @param source the source sequence
	 * @param keyComparator the key comparator function
	 * @param keySelector the key selector function
	 */
	public OrderByIterable(Iterable<? extends T> source,
			Comparator<? super U> keyComparator,
			Func1<? super T, ? extends U> keySelector) {
		this.source = source;
		this.keyComparator = keyComparator;
		this.keySelector = keySelector;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The buffer. */
	        List<T> buffer;
	        /** The source iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        /** The buffer iterator. */
	        Iterator<T> bufIterator;
	        @Override
	        public boolean hasNext() {
	            if (buffer == null) {
	                buffer = new ArrayList<T>();
	                try {
	                    while (it.hasNext()) {
	                        buffer.add(it.next());
	                    }
	                } finally {
	                    Interactive.unsubscribe(it);
	                }
	                Collections.sort(buffer, new Comparator<T>() {
	                    @Override
	                    public int compare(T o1, T o2) {
	                        U key1 = keySelector.call(o1);
	                        U key2 = keySelector.call(o2);
	                        return keyComparator.compare(key1, key2);
	                    }
	                });
	                bufIterator = buffer.iterator();
	            }
	            return bufIterator.hasNext();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                return bufIterator.next();
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