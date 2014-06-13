package ix.internal.operators;

import ix.internal.util.CircularBuffer;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class MemoizeIterable<T> implements Iterable<T> {
	private final Iterable<? extends T> source;
	private final int bufferSize;
	/** The source iterator. */
	Iterator<? extends T> it;
	/** The ring buffer of the memory. */
	final CircularBuffer<T> buffer;

	public MemoizeIterable(Iterable<? extends T> source, int bufferSize) {
		this.source = source;
		this.bufferSize = bufferSize;
		it = source.iterator();
		buffer = new CircularBuffer<T>(bufferSize);
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        int myHead;
	        
	        @Override
	        public boolean hasNext() {
	            return buffer.tail() > Math.max(myHead, buffer.head()) || it.hasNext();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                if (buffer.tail() == myHead) {
	                    T value = it.next();
	                    if (bufferSize > 0) {
	                        buffer.add(value);
	                    }
	                    myHead++;
	                    return value;
	                } else {
	                    myHead = Math.max(myHead, buffer.head());
	                    T value = buffer.get(myHead);
	                    myHead++;
	                    return value;
	                }
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