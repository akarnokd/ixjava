package ix.internal.operators;

import java.util.Iterator;

public final class ShareIterable<T> implements Iterable<T> {
	private final Iterable<T> source;
	Iterator<T> it;

	public ShareIterable(Iterable<T> source) {
		this.source = source;
	}

	@Override
	public Iterator<T> iterator() {
	    if (it == null) {
	        it = source.iterator();
	    }
	    return it;
	}
}