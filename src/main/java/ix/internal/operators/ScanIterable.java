package ix.internal.operators;

import java.util.Iterator;

import rx.functions.Func2;

public final class ScanIterable<U, T> implements Iterable<U> {
	private final Iterable<? extends T> source;
	private final Func2<? super U, ? super T, ? extends U> aggregator;
	private final U seed;

	public ScanIterable(Iterable<? extends T> source,
			Func2<? super U, ? super T, ? extends U> aggregator, U seed) {
		this.source = source;
		this.aggregator = aggregator;
		this.seed = seed;
	}

	@Override
	public Iterator<U> iterator() {
	    final Iterator<? extends T> it = source.iterator();
	    return new Iterator<U>() {
	        /** The current value. */
	        U current = seed;
	        
	        @Override
	        public boolean hasNext() {
	            return it.hasNext();
	        }
	        
	        @Override
	        public U next() {
	            current = aggregator.call(current, it.next());
	            return current;
	        }
	        
	        @Override
	        public void remove() {
	            it.remove();
	        }
	    };
	}
}