package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ErrorIterable<T> implements Iterable<T> {
	private final Throwable t;

	public ErrorIterable(Throwable t) {
		this.t = t;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** First call? */
	        boolean first = true;
	        @Override
	        public boolean hasNext() {
	            return first;
	        }
	        
	        @Override
	        public T next() {
	            if (first) {
	                first = false;
	                if (t instanceof RuntimeException) {
	                    throw (RuntimeException)t;
	                }
	                throw new RuntimeException(t);
	            }
	            throw new NoSuchElementException();
	        }
	        
	        @Override
	        public void remove() {
	            throw new IllegalStateException();
	        }
	        
	    };
	}
}