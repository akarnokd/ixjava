package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.functions.Func1;

public final class GenerateIterable<T> implements Iterable<T> {
	private final T seed;
	private final Func1<? super T, ? extends T> next;
	private final Func1<? super T, Boolean> predicate;

	public GenerateIterable(T seed, Func1<? super T, ? extends T> next,
			Func1<? super T, Boolean> predicate) {
		this.seed = seed;
		this.next = next;
		this.predicate = predicate;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        T value = seed;
	        @Override
	        public boolean hasNext() {
	            return predicate.call(value);
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                T current = value;
	                value = next.call(value);
	                return current;
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