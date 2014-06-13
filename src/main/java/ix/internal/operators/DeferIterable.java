package ix.internal.operators;

import java.util.Iterator;

import rx.functions.Func0;

public final class DeferIterable<T> implements Iterable<T> {
	private final Func0<? extends Iterable<T>> func;

	public DeferIterable(Func0<? extends Iterable<T>> func) {
		this.func = func;
	}

	@Override
	public Iterator<T> iterator() {
	    return func.call().iterator();
	}
}