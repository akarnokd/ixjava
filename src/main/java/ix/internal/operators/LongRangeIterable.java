package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class LongRangeIterable implements Iterable<Long> {
	private final long start;
	private final long count;

	public LongRangeIterable(long start, long count) {
		this.start = start;
		this.count = count;
	}

	@Override
	public Iterator<Long> iterator() {
	    return new Iterator<Long>() {
	        long current = start;
	        @Override
	        public boolean hasNext() {
	            return current < start + count;
	        }
	        @Override
	        public Long next() {
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