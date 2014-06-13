package ix.internal.operators;

import java.util.Iterator;

public final class RepeatIterable<T> implements Iterable<T> {
	private final T value;

	public RepeatIterable(T value) {
		this.value = value;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        @Override
	        public boolean hasNext() {
	            return true;
	        }
	        @Override
	        public T next() {
	            return value;
	        }
	        @Override
	        public void remove() {
	            throw new UnsupportedOperationException();
	        }
	    };
	}
}