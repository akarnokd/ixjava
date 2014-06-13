package ix.internal.operators;

import ix.Enumerator;
import ix.internal.util.SingleContainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.Notification;

public final class EnumerationToIterator<T> implements Iterator<T> {
	private final Enumerator<? extends T> en;
	/** The peek-ahead buffer. */
	final SingleContainer<Notification<? extends T>> peek = new SingleContainer<Notification<? extends T>>();
	/** Completion indicator. */
	boolean done;

	public EnumerationToIterator(Enumerator<? extends T> en) {
		this.en = en;
	}

	@Override
	public boolean hasNext() {
	    if (!done && peek.isEmpty()) {
	        try {
	            if (en.next()) {
	                peek.add(Interactive.some(en.current()));
	            } else {
	                done = true;
	            }
	        } catch (Throwable t) {
	            done = true;
	            peek.add(Interactive.<T>err(t));
	        }
	    }
	    return peek.isEmpty();
	}

	@Override
	public T next() {
	    if (hasNext()) {
	        return Interactive.value(peek.take());
	    }
	    throw new NoSuchElementException();
	}

	@Override
	public void remove() {
	    throw new UnsupportedOperationException();
	}
}