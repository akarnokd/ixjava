package ix.internal.operators;

import java.util.Iterator;
import java.util.Map;

import rx.functions.Func0;

public final class SwitchCaseIterable<U, T> implements Iterable<U> {
	private final Func0<T> selector;
	private final Map<T, Iterable<U>> options;

	public SwitchCaseIterable(Func0<T> selector, Map<T, Iterable<U>> options) {
		this.selector = selector;
		this.options = options;
	}

	@Override
	public Iterator<U> iterator() {
	    Iterable<U> it = options.get(selector.call());
	    return it != null ? it.iterator() : Interactive.<U>empty().iterator();
	}
}