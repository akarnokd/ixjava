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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import rx.Notification;
import rx.functions.Func1;

/**
 * Iterable sequence that returns the minimum or maximum of the source sequence
 * according to a key selector and key comparator.
 *
 * @param <T> the source value type
 * @param <U> the key value type
 */
public final class MinMaxIterable<T, U> implements Iterable<List<T>> {
	/** Returns a key for a value. */
	private final Func1<? super T, ? extends U> keySelector;
	/** The source sequence. */
	private final Iterable<? extends T> source;
	/** Find the maximum? */
	private final boolean max;
	/** Compares two keys. */
	private final Comparator<? super U> keyComparator;

	/**
	 * Constructor, initializes the fields.
	 * @param keySelector the key selector function
	 * @param source the source sequenec
	 * @param max find the maximum?
	 * @param keyComparator the key comparator function
	 */
	public MinMaxIterable(Func1<? super T, ? extends U> keySelector,
			Iterable<? extends T> source, boolean max,
			Comparator<? super U> keyComparator) {
		this.keySelector = keySelector;
		this.source = source;
		this.max = max;
		this.keyComparator = keyComparator;
	}

	@Override
	public Iterator<List<T>> iterator() {
	    return new Iterator<List<T>>() {
	        /** The source iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        /** The single result container. */
	        final SingleContainer<Notification<? extends List<T>>> result = new SingleContainer<Notification<? extends List<T>>>();
	        /** We have finished the aggregation. */
	        boolean done;
	        @Override
	        public boolean hasNext() {
	            if (!done) {
	                done = true;
	                if (result.isEmpty()) {
	                    try {
	                        List<T> intermediate = null;
	                        U lastKey = null;
	                        try {
	                            while (it.hasNext()) {
	                                T value = it.next();
	                                U key = keySelector.call(value);
	                                if (intermediate == null) {
	                                    intermediate = new ArrayList<T>();
	                                    lastKey = key;
	                                    intermediate.add(value);
	                                } else {
	                                    int c = keyComparator.compare(lastKey, key);
	                                    if ((c < 0 && max) || (c > 0 && !max)) {
	                                        intermediate = new ArrayList<T>();
	                                        lastKey = key;
	                                        c = 0;
	                                    }
	                                    if (c == 0) {
	                                        intermediate.add(value);
	                                    }
	                                }
	                            }
	                        } finally {
	                            Interactive.unsubscribe(it);
	                        }
	                        if (intermediate != null) {
	                            result.add(Interactive.some(intermediate));
	                        }
	                    } catch (Throwable t) {
	                        result.add(Interactive.<List<T>>err(t));
	                    }
	                }
	            }
	            return !result.isEmpty();
	        }
	        
	        @Override
	        public List<T> next() {
	            if (hasNext()) {
	                return Interactive.value(result.take());
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