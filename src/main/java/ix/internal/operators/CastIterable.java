package ix.internal.operators;

import java.util.Iterator;

public final class CastIterable<T> implements Iterable<T> {
	private final Iterable<?> source;
	private final Class<T> token;

	public CastIterable(Iterable<?> source, Class<T> token) {
		this.source = source;
		this.token = token;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The source iterator. */
	        final Iterator<?> it = source.iterator();
	        @Override
	        public boolean hasNext() {
	            return it.hasNext();
	        }
	        
	        @Override
	        public T next() {
	            return token.cast(it.next());
	        }
	        
	        @Override
	        public void remove() {
	            it.remove();
	        }
	        
	    };
	}
}