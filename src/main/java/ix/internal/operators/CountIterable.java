package ix.internal.operators;

import ix.internal.util.SingleContainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.Notification;

public final class CountIterable<T> implements Iterable<Integer> {
	private final Iterable<T> source;

	public CountIterable(Iterable<T> source) {
		this.source = source;
	}

	@Override
	public Iterator<Integer> iterator() {
	    final Iterator<T> it = source.iterator();
	    return new Iterator<Integer>() {
	        /** The peek ahead container. */
	        final SingleContainer<Notification<Integer>> peek = new SingleContainer<Notification<Integer>>();
	        /** Computation already done. */
	        boolean done;
	        @Override
	        public boolean hasNext() {
	            if (!done) {
	                if (peek.isEmpty()) {
	                    int count = 0;
	                    try {
	                        while (it.hasNext()) {
	                            it.next();
	                            count++;
	                        }
	                        peek.add(Interactive.some(count));
	                    } catch (Throwable t) {
	                        peek.add(Interactive.<Integer>err(t));
	                    } finally {
	                        done = true;
	                        Interactive.unsubscribe(it);
	                    }
	                }
	            }
	            return !peek.isEmpty();
	        }
	        
	        @Override
	        public Integer next() {
	            if (hasNext()) {
	                return Interactive.value(peek.take());
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