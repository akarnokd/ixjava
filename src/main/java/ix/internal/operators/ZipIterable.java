package ix.internal.operators;

import ix.internal.util.SingleContainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.Notification;
import rx.functions.Func2;

public final class ZipIterable<V, T, U> implements Iterable<V> {
	private final Iterable<? extends U> right;
	private final Func2<? super T, ? super U, ? extends V> combiner;
	private final Iterable<? extends T> left;

	public ZipIterable(Iterable<? extends U> right,
			Func2<? super T, ? super U, ? extends V> combiner,
			Iterable<? extends T> left) {
		this.right = right;
		this.combiner = combiner;
		this.left = left;
	}

	@Override
	public Iterator<V> iterator() {
	    return new Iterator<V>() {
	        /** The left iterator. */
	        final Iterator<? extends T> ts = left.iterator();
	        /** The right iterator. */
	        final Iterator<? extends U> us = right.iterator();
	        /** The peek-ahead container. */
	        final SingleContainer<Notification<? extends V>> peek = new SingleContainer<Notification<? extends V>>();
	        @Override
	        public boolean hasNext() {
	            if (peek.isEmpty()) {
	                try {
	                    if (ts.hasNext() && us.hasNext()) {
	                        peek.add(Interactive.some(combiner.call(ts.next(), us.next())));
	                    }
	                } catch (Throwable t) {
	                    peek.add(Interactive.<V>err(t));
	                }
	            }
	            return !peek.isEmpty();
	        }
	        @Override
	        public V next() {
	            if (hasNext()) {
	                return Interactive.value(peek.take());
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