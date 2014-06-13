package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class PartialIterable<T> implements Iterable<T> {
	private final int from;
	private final T[] ts;
	private final int to;

	public PartialIterable(int from, T[] ts, int to) {
		this.from = from;
		this.ts = ts;
		this.to = to;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The current location. */
	        int index = from;
	        /** The lenght. */
	        final int size = ts.length;
	        @Override
	        public boolean hasNext() {
	            return index < size && index < to;
	        }
	        @Override
	        public T next() {
	            if (hasNext()) {
	                return ts[index++];
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