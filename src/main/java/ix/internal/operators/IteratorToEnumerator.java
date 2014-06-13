package ix.internal.operators;

import ix.Enumerator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class IteratorToEnumerator<T> implements Enumerator<T> {
	private final Iterator<? extends T> it;
	/** The current value. */
	T value;
	/** The current value is set. */
	boolean hasValue;

	public IteratorToEnumerator(Iterator<? extends T> it) {
		this.it = it;
	}

	@Override
	public T current() {
	    if (hasValue) {
	        return value;
	    }
	    throw new NoSuchElementException();
	}

	@Override
	public boolean next() {
	    if (it.hasNext()) {
	        value = it.next();
	        hasValue = true;
	        return false;
	    }
	    hasValue = false;
	    return false;
	}
}