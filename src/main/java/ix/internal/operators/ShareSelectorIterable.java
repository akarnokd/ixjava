package ix.internal.operators;

import java.util.Iterator;

import rx.functions.Func1;

public final class ShareSelectorIterable<T, U> implements Iterable<U> {
	private final Iterable<T> source;
	private final Func1<? super Iterable<T>, ? extends Iterable<U>> selector;

	public ShareSelectorIterable(Iterable<T> source,
			Func1<? super Iterable<T>, ? extends Iterable<U>> selector) {
		this.source = source;
		this.selector = selector;
	}

	@Override
	public Iterator<U> iterator() {
	    return selector.call(Interactive.share(source)).iterator();
	}
}