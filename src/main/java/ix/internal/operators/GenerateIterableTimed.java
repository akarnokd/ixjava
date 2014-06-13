package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import rx.functions.Func1;

public final class GenerateIterableTimed<T> implements Iterable<T> {
	private final Func1<? super T, Boolean> predicate;
	private final Func1<? super T, ? extends T> next;
	private final T seed;
	private final long initialDelay;
	private final long betweenDelay;
	private final TimeUnit unit;

	public GenerateIterableTimed(Func1<? super T, Boolean> predicate,
			Func1<? super T, ? extends T> next, T seed, long initialDelay,
			long betweenDelay, TimeUnit unit) {
		this.predicate = predicate;
		this.next = next;
		this.seed = seed;
		this.initialDelay = initialDelay;
		this.betweenDelay = betweenDelay;
		this.unit = unit;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        T value = seed;
	        /** Keeps track of whether there should be an initial delay? */
	        boolean shouldInitialWait = true;
	        /** Keeps track of whether there should be an initial delay? */
	        boolean shouldBetweenWait;
	        @Override
	        public boolean hasNext() {
	            if (shouldInitialWait) {
	                shouldInitialWait = false;
	                try {
	                    unit.sleep(initialDelay);
	                } catch (InterruptedException e) {
	                    return false; // FIXME not soure about this
	                }
	            } else {
	                if (shouldBetweenWait) {
	                    shouldBetweenWait = false;
	                    try {
	                        unit.sleep(betweenDelay);
	                    } catch (InterruptedException e) {
	                        return false; // FIXME not soure about this
	                    }
	                }
	            }
	            return predicate.call(value);
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                shouldBetweenWait = true;
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