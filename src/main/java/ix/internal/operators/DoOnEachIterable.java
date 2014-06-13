package ix.internal.operators;

import ix.internal.util.SingleContainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.Notification;
import rx.functions.Action0;
import rx.functions.Action1;

public final class DoOnEachIterable<T> implements Iterable<T> {
	private final Action1<? super Throwable> error;
	private final Action0 finish;
	private final Iterable<? extends T> source;

	public DoOnEachIterable(Action1<? super Throwable> error,
			Action0 finish, Iterable<? extends T> source) {
		this.error = error;
		this.finish = finish;
		this.source = source;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        /** The peek ahead container. */
	        final SingleContainer<Notification<? extends T>> peek = new SingleContainer<Notification<? extends T>>();
	        /** Finish or error once. */
	        boolean once = true;
	        @Override
	        public boolean hasNext() {
	            if (peek.isEmpty()) {
	                try {
	                    if (it.hasNext()) {
	                        peek.add(Interactive.some(it.next()));
	                    } else {
	                        if (once) {
	                            once = false;
	                            finish.call();
	                        }
	                    }
	                } catch (Throwable t) {
	                    peek.add(Interactive.<T>err(t));
	                }
	            }
	            return !peek.isEmpty();
	        }
	        
	        @Override
	        public T next() {
	            if (it.hasNext()) {
	                Notification<? extends T> o = peek.take();
	                if (o.isOnError() && once) {
	                    once = false;
	                    error.call(o.getThrowable());
	                }
	                return Interactive.value(o);
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