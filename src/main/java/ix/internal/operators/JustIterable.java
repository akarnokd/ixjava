package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class JustIterable<T> implements Iterable<T> {
	private final T value;

	public JustIterable(T value) {
		this.value = value;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** Return the only element? */
	        boolean first = true;
	        @Override
	        public boolean hasNext() {
	            return first;
	        }
	        
	        @Override
	        public T next() {
	            if (first) {
	                first = false;
	                return value;
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