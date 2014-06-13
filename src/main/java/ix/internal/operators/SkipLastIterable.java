package ix.internal.operators;

import ix.internal.util.CircularBuffer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.Notification;

public final class SkipLastIterable<T> implements Iterable<T> {
	private final Iterable<? extends T> source;
	private final int num;

	public SkipLastIterable(Iterable<? extends T> source, int num) {
		this.source = source;
		this.num = num;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The source iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        /** The temporary buffer. */
	        final CircularBuffer<Notification<? extends T>> buffer = new CircularBuffer<Notification<? extends T>>(num);
	        @Override
	        public boolean hasNext() {
	            try {
	                while (buffer.size() < num && it.hasNext()) {
	                    buffer.add(Interactive.some(it.next()));
	                }
	            } catch (Throwable t) {
	                buffer.add(Interactive.<T>err(t));
	            } finally {
	                Interactive.unsubscribe(it);
	            }
	            return buffer.size() == num && it.hasNext();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                return Interactive.value(buffer.take());
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