package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.functions.Action0;

public final class DoOnCompletedIterable<T> implements Iterable<T> {
	private final Action0 action;
	private final Iterable<? extends T> source;

	public DoOnCompletedIterable(Action0 action,
			Iterable<? extends T> source) {
		this.action = action;
		this.source = source;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        /** After the last. */
	        boolean last;
	        @Override
	        public boolean hasNext() {
	            if (!it.hasNext()) {
	                if (!last) {
	                    last = true;
	                    action.call();
	                }
	                return false;
	            }
	            return true;
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
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