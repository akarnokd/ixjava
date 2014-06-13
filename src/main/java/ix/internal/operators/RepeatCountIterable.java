package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class RepeatCountIterable<T> implements Iterable<T> {
	private final int count;
	private final T value;

	public RepeatCountIterable(int count, T value) {
		this.count = count;
		this.value = value;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        int index;
	        @Override
	        public boolean hasNext() {
	            return index < count;
	        }
	        @Override
	        public T next() {
	            if (hasNext()) {
	                index++;
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