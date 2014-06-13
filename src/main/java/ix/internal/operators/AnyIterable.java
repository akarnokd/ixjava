package ix.internal.operators;

import ix.internal.util.SingleContainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class AnyIterable<T> implements Iterable<Boolean> {
	private final Iterable<T> source;

	public AnyIterable(Iterable<T> source) {
		this.source = source;
	}

	@Override
	public Iterator<Boolean> iterator() {
	    return new Iterator<Boolean>() {
	        /** The source's iterator. */
	        Iterator<T> it = source.iterator();
	        final SingleContainer<Boolean> peek = new SingleContainer<Boolean>();
	        /** Query once. */
	        boolean once = true;
	        @Override
	        public boolean hasNext() {
	            if (once) {
	                once = false;
	                if (peek.isEmpty()) {
	                    peek.add(it.hasNext());
	                }
	            }
	            return !peek.isEmpty();
	        }
	        
	        @Override
	        public Boolean next() {
	            if (hasNext()) {
	                return peek.take();
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