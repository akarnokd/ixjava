package ix.internal.operators;

import java.util.Iterator;

import rx.functions.Func2;

public final class MapIndexedIterable<U, T> implements Iterable<U> {
	private final Iterable<? extends T> source;
	private final Func2<? super Integer, ? super T, ? extends U> selector;

	public MapIndexedIterable(Iterable<? extends T> source,
			Func2<? super Integer, ? super T, ? extends U> selector) {
		this.source = source;
		this.selector = selector;
	}

	@Override
	public Iterator<U> iterator() {
	    final Iterator<? extends T> it = source.iterator();
	    return new Iterator<U>() {
	        /** The current counter. */
	        int count;
	        @Override
	        public boolean hasNext() {
	            return it.hasNext();
	        }
	        
	        @Override
	        public U next() {
	            return selector.call(count++, it.next());
	        }
	        
	        @Override
	        public void remove() {
	            it.remove();
	        }
	        
	    };
	}
}