package ix.internal.operators;

import ix.internal.util.SingleContainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.exceptions.Exceptions;
import rx.functions.Func1;

public final class OnErrorResumeNextIterable<T> implements
		Iterable<T> {
	private final Iterable<? extends T> source;
	private final Func1<? super Throwable, ? extends Iterable<? extends T>> handler;

	public OnErrorResumeNextIterable(
			Iterable<? extends T> source,
			Func1<? super Throwable, ? extends Iterable<? extends T>> handler) {
		this.source = source;
		this.handler = handler;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The current iterator. */
	        Iterator<? extends T> it = source.iterator();
	        /** The last iterator used by next(). */
	        Iterator<? extends T> itForRemove;
	        /** The peek ahead container. */
	        final SingleContainer<T> peek = new SingleContainer<T>();
	        /** Indicate that we switched to the handler. */
	        boolean usingHandler;
	        @Override
	        public boolean hasNext() {
	            if (peek.isEmpty()) {
	                while (!Thread.currentThread().isInterrupted()) {
	                    try {
	                        if (it.hasNext()) {
	                            itForRemove = it;
	                            peek.add(it.next());
	                        }
	                        break;
	                    } catch (Throwable t) {
	                        if (!usingHandler) {
	                            Interactive.unsubscribe(it);
	                            it = handler.call(t).iterator();
	                            usingHandler = true;
	                        } else {
	                            Exceptions.propagate(t);
	                        }
	                    }
	                }
	            }
	            return !peek.isEmpty();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                return peek.take();
	            }
	            throw new NoSuchElementException();
	        }
	        
	        @Override
	        public void remove() {
	            if (itForRemove == null) {
	                throw new IllegalStateException();
	            }
	            itForRemove.remove();
	            itForRemove = null;
	        }
	        
	    };
	}
}