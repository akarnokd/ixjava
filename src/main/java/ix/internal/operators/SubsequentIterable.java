package ix.internal.operators;

import ix.internal.util.Pair;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class SubsequentIterable<T> implements
		Iterable<Pair<T, T>> {
	private final Iterable<? extends T> source;

	public SubsequentIterable(Iterable<? extends T> source) {
		this.source = source;
	}

	@Override
	public Iterator<Pair<T, T>> iterator() {
	    final Iterator<? extends T> it = source.iterator();
	    if (!it.hasNext()) {
	        return Interactive.<Pair<T, T>>empty().iterator();
	    }
	    final T flast = it.next();
	    return new Iterator<Pair<T, T>>() {
	        /** The last source value. */
	        T last = flast;
	        @Override
	        public boolean hasNext() {
	            return it.hasNext();
	        }
	        @Override
	        public Pair<T, T> next() {
	            if (hasNext()) {
	                T curr = it.next();
	                Pair<T, T> ret = Pair.of(last, curr);
	                last = curr;
	                return ret;
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