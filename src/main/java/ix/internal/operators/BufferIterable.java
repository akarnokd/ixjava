package ix.internal.operators;

import ix.internal.util.SingleContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import rx.Notification;

public final class BufferIterable<T> implements Iterable<List<T>> {
	private final Iterable<? extends T> source;
	private final int bufferSize;

	public BufferIterable(Iterable<? extends T> source, int bufferSize) {
		this.source = source;
		this.bufferSize = bufferSize;
	}

	@Override
	public Iterator<List<T>> iterator() {
	    return new Iterator<List<T>>() {
	        /** The source iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        /** The current buffer. */
	        final SingleContainer<Notification<List<T>>> peek = new SingleContainer<Notification<List<T>>>();
	        /** Did the source finish? */
	        boolean done;
	        @Override
	        public boolean hasNext() {
	            if (peek.isEmpty() && !done) {
	                try {
	                    if (it.hasNext()) {
	                        try {
	                            List<T> buffer = new ArrayList<T>();
	                            while (it.hasNext() && buffer.size() < bufferSize) {
	                                buffer.add(it.next());
	                            }
	                            if (buffer.size() > 0) {
	                                peek.add(Interactive.some(buffer));
	                            }
	                        } catch (Throwable t) {
	                            done = true;
	                            peek.add(Interactive.<List<T>>err(t));
	                        }
	                    } else {
	                        done = true;
	                    }
	                } finally {
	                    Interactive.unsubscribe(it);
	                }
	            }
	            return !peek.isEmpty();
	        }
	        
	        @Override
	        public List<T> next() {
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