package ix.internal.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import rx.functions.Func1;

public final class OrderByIterable<T, U> implements Iterable<T> {
	private final Iterable<? extends T> source;
	private final Comparator<? super U> keyComparator;
	private final Func1<? super T, ? extends U> keySelector;

	public OrderByIterable(Iterable<? extends T> source,
			Comparator<? super U> keyComparator,
			Func1<? super T, ? extends U> keySelector) {
		this.source = source;
		this.keyComparator = keyComparator;
		this.keySelector = keySelector;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The buffer. */
	        List<T> buffer;
	        /** The source iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        /** The buffer iterator. */
	        Iterator<T> bufIterator;
	        @Override
	        public boolean hasNext() {
	            if (buffer == null) {
	                buffer = new ArrayList<T>();
	                try {
	                    while (it.hasNext()) {
	                        buffer.add(it.next());
	                    }
	                } finally {
	                    Interactive.unsubscribe(it);
	                }
	                Collections.sort(buffer, new Comparator<T>() {
	                    @Override
	                    public int compare(T o1, T o2) {
	                        U key1 = keySelector.call(o1);
	                        U key2 = keySelector.call(o2);
	                        return keyComparator.compare(key1, key2);
	                    }
	                });
	                bufIterator = buffer.iterator();
	            }
	            return bufIterator.hasNext();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                return bufIterator.next();
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