package ix.internal.operators;

import ix.internal.util.SingleContainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.Notification;
import rx.functions.Func2;

public final class AggregateIterable<V, T, U> implements Iterable<V> {
	private final Iterable<? extends T> source;
	private final Func2<? super U, ? super T, ? extends U> sum;
	private final Func2<? super U, ? super Integer, ? extends V> divide;

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