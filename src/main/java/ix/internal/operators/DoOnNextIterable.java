package ix.internal.operators;

import java.util.Iterator;

import rx.functions.Action1;

public final class DoOnNextIterable<T> implements Iterable<T> {
	private final Iterable<? extends T> source;
	private final Action1<? super T> action;

	public DoOnNextIterable(Iterable<? extends T> source,
			Action1<? super T> action) {
		this.source = source;
		this.action = action;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The source iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        @Override
	        public boolean hasNext() {
	            return it.hasNext();
	        }
	        @Override
	        public T next() {
	            T value = it.next();
	            action.call(value);
	            return value;
	        }
	        @Override
	        public void remove() {
	            it.remove();
	        }
	    };
	}
}