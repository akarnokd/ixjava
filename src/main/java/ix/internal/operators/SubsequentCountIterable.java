package ix.internal.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public final class SubsequentCountIterable<T> implements
		Iterable<Iterable<T>> {
	private final Iterable<? extends T> source;
	private final int count;

	public SubsequentCountIterable(Iterable<? extends T> source, int count) {
		this.source = source;
		this.count = count;
	}

	@Override
	public Iterator<Iterable<T>> iterator() {
	    // get the first count-1 elements
	    final LinkedList<T> ll = new LinkedList<T>();
	    final Iterator<? extends T> it = source.iterator();
	    int cnt = 0;
	    try {
	        while (it.hasNext() && cnt < count - 1) {
	            ll.add(it.next());
	            cnt++;
	        }
	    } finally {
	        Interactive.unsubscribe(it);
	    }
	    if (cnt < count - 1) {
	        return Interactive.<Iterable<T>>empty().iterator();
	    }
	    return new Iterator<Iterable<T>>() {
	        @Override
	        public boolean hasNext() {
	            return it.hasNext();
	        }
	        @Override
	        public Iterable<T> next() {
	            if (hasNext()) {
	                ll.add(it.next());
	                ll.removeFirst();
	                return new ArrayList<T>(ll);
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