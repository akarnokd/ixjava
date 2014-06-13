package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.functions.Func1;

public final class FlatMapIterable<U, T> implements Iterable<U> {
	private final Func1<? super T, ? extends Iterable<? extends U>> selector;
	private final Iterable<? extends T> source;

	public FlatMapIterable(
			Func1<? super T, ? extends Iterable<? extends U>> selector,
			Iterable<? extends T> source) {
		this.selector = selector;
		this.source = source;
	}

	@Override
	public Iterator<U> iterator() {
	    final Iterator<? extends T> it = source.iterator();
	    return new Iterator<U>() {
	        /** The current selected iterator. */
	        Iterator<? extends U> sel;
	        @Override
	        public boolean hasNext() {
	            if (sel == null || !sel.hasNext()) {
	                while (!Thread.currentThread().isInterrupted()) {
	                    if (it.hasNext()) {
	                        sel = selector.call(it.next()).iterator();
	                        if (sel.hasNext()) {
	                            return true;
	                        }
	                    } else {
	                        break;
	                    }
	                }
	                return false;
	            }
	            return true;
	        }
	        
	        @Override
	        public U next() {
	            if (hasNext()) {
	                return sel.next();
	            }
	            throw new NoSuchElementException();
	        }
	        
	        @Override
	        public void remove() {
	            if (sel == null) {
	                throw new IllegalStateException();
	            }
	            sel.remove();
	        }
	        
	    };
	}
}