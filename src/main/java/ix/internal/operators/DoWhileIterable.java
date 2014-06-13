package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.functions.Func0;

public final class DoWhileIterable<T> implements Iterable<T> {
	private final Iterable<? extends T> source;
	private final Func0<Boolean> gate;

	public DoWhileIterable(Iterable<? extends T> source, Func0<Boolean> gate) {
		this.source = source;
		this.gate = gate;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** is this the first pass? */
	        Iterator<? extends T> it = source.iterator();
	        @Override
	        public boolean hasNext() {
	            while (true) {
	                if (it.hasNext()) {
	                    return true;
	                }
	                if (gate.call()) {
	                    it = source.iterator();
	                } else {
	                    break;
	                }
	            }
	            return false;
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