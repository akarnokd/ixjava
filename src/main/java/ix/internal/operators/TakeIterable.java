package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class TakeIterable<T> implements Iterable<T> {
	private final int num;
	private final Iterable<? extends T> source;

	public TakeIterable(int num, Iterable<? extends T> source) {
		this.num = num;
		this.source = source;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The counter. */
	        int count;
	        /** The source iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        @Override
	        public boolean hasNext() {
	            return count < num && it.hasNext();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                count++;
	                return it.next();
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