package ix.internal.operators;

import ix.internal.util.LinkedBuffer;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class MemoizeAllIterable<T> implements Iterable<T> {
	private final Iterator<? extends T> it;
	private final LinkedBuffer<T> buffer;

	public MemoizeAllIterable(Iterator<? extends T> it,
			LinkedBuffer<T> buffer) {
		this.it = it;
		this.buffer = buffer;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The element count. */
	        int count = 0;
	        /** The current node pointer. */
	        LinkedBuffer.N<T> pointer = buffer.head;
	        @Override
	        public boolean hasNext() {
	            return count < buffer.size || it.hasNext();
	        }
	        
	        @Override
	        public T next() {
	            if (hasNext()) {
	                if (count < buffer.size) {
	                    T value = pointer.next.value;
	                    pointer = pointer.next;
	                    count++;
	                    return value;
	                } else {
	                    T value = it.next();
	                    buffer.add(value);
	                    count++;
	                    pointer = pointer.next;
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