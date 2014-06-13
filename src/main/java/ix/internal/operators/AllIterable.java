package ix.internal.operators;

import ix.internal.util.SingleContainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.Notification;
import rx.functions.Func1;

public final class AllIterable<T> implements Iterable<Boolean> {
	private final Iterable<? extends T> source;
	private final Func1<? super T, Boolean> predicate;

	public AllIterable(Iterable<? extends T> source,
			Func1<? super T, Boolean> predicate) {
		this.source = source;
		this.predicate = predicate;
	}

	@Override
	public Iterator<Boolean> iterator() {
	    return new Iterator<Boolean>() {
	        /** The source iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        /** The peek ahead container. */
	        final SingleContainer<Notification<Boolean>> peek = new SingleContainer<Notification<Boolean>>();
	        /** Completed. */
	        boolean done;
	        @Override
	        public boolean hasNext() {
	            if (peek.isEmpty() && !done) {
	                try {
	                    if (it.hasNext()) {
	                        while (it.hasNext()) {
	                            T value = it.next();
	                            if (!predicate.call(value)) {
	                                peek.add(Interactive.some(false));
	                                return true;
	                            }
	                        }
	                        peek.add(Interactive.some(true));
	                    }
	                    done = true;
	                } catch (Throwable t) {
	                    peek.add(Interactive.<Boolean>err(t));
	                    done = true;
	                } finally {
	                    Interactive.unsubscribe(it);
	                }
	            }
	            return !peek.isEmpty();
	        }
	        
	        @Override
	        public Boolean next() {
	            if (hasNext()) {
	                return Interactive.value(peek.take());
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