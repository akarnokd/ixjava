package ix.internal.util;

/**
 * A linked buffer, which can be only filled and queried.
 * @param <T> the element type
 */
public final class LinkedBuffer<T> {
    /** The node. */
    public static class N<T> {
        /** The element value. */
        public T value;
        /** The next node. */
        public LinkedBuffer.N<T> next;
    }
    /** The head pointer. */
    public final LinkedBuffer.N<T> head = new LinkedBuffer.N<T>();
    /** The tail pointer. */
    public LinkedBuffer.N<T> tail = head;
    /** The size. */
    public int size;
    /**
     * Add a new value.
     * @param value the new value
     */
    public void add(T value) {
        LinkedBuffer.N<T> n = new LinkedBuffer.N<T>();
        n.value = value;
        tail.next = n;
        tail = n;
        size++;
    }
}