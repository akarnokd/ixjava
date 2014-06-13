package ix.internal.operators;

import ix.internal.util.SingleContainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.Notification;

public final class RetryIterable<T> implements Iterable<T> {
	private final int count;
	private final Iterable<? extends T> source;

	public RetryIterable(int count, Iterable<? extends T> source) {
		this.count = count;
		this.source = source;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The retry count. */
	        int retries = count;
	        /** The peek store. */
	        final SingleContainer<Notification<? extends T>> peek = new SingleContainer<Notification<? extends T>>();
	        /** The current iterator. */
	        Iterator<? extends T> it = source.iterator();
	        @Override
	        public boolean hasNext() {
	            if (peek.isEmpty()) {
	                while (it.hasNext()) {
	                    try {
	                        peek.add(Interactive.some(it.next()));
	                        break;
	                    } catch (Throwable t) {
	                        if (retries-- > 0) {
	                            it = source.iterator();
	                        } else {
	                            peek.add(Interactive.<T>err(t));
	                            break;
	                        }
	                    }
	                }
	            }
	            return !peek.isEmpty();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                return Interactive.value(peek.take());
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