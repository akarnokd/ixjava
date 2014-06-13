package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class RangeIterable implements Iterable<Integer> {
	private final int start;
	private final int count;

	public RangeIterable(int start, int count) {
		this.start = start;
		this.count = count;
	}

	@Override
	public Iterator<Integer> iterator() {
	    return new Iterator<Integer>() {
	        int current = start;
	        @Override
	        public boolean hasNext() {
	            return current < start + count;
	        }
	        @Override
	        public Integer next() {
	            if (hasNext()) {
	                return current++;
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