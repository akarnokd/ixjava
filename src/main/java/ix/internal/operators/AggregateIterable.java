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

import rx.Notification;
import rx.functions.Func2;

/**
 * Aggregates the source values via a sum function and
 * applies a divide function to return the final result.
 *
 * @param <T> the source sequence element type
 * @param <U> the the accumulated type
 * @param <V> the result type
 */
public final class AggregateIterable<T, U, V> implements Iterable<V> {
	/** The source sequence. */
	private final Iterable<? extends T> source;
	/** The summing function. */
	private final Func2<? super U, ? super T, ? extends U> sum;
	/** The divider function. */
	private final Func2<? super U, ? super Integer, ? extends V> divide;

	/**
	 * Constructor, initializes the fields.
	 * @param source the source sequence
	 * @param sum the sum function
	 * @param divide the division function
	 */
	public AggregateIterable(Iterable<? extends T> source,
			Func2<? super U, ? super T, ? extends U> sum,
			Func2<? super U, ? super Integer, ? extends V> divide) {
		this.source = source;
		this.sum = sum;
		this.divide = divide;
	}

	@Override
	public Iterator<V> iterator() {
	    return new Iterator<V>() {
	        /** The source iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        /** The single result container. */
	        final SingleContainer<Notification<? extends V>> result = new SingleContainer<Notification<? extends V>>();
	        /** We have finished the aggregation. */
	        boolean done;
	        @Override
	        public boolean hasNext() {
	            if (!done) {
	                done = true;
	                if (result.isEmpty()) {
	                    try {
	                        U intermediate = null;
	                        int count = 0;
	                        try {
	                            while (it.hasNext()) {
	                                intermediate = sum.call(intermediate, it.next());
	                                count++;
	                            }
	                        } finally {
	                            Interactive.unsubscribe(it);
	                        }
	                        if (count > 0) {
	                            result.add(Interactive.some(divide.call(intermediate, count)));
	                        }
	                    } catch (Throwable t) {
	                        result.add(Interactive.<V>err(t));
	                    }
	                }
	            }
	            return !result.isEmpty();
	        }
	        
	        @Override
	        public V next() {
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