package ix;

import java.util.Iterator;

final class IxFromArray<T> extends Ix<T> {

    final int start;
    final int end;
    final T[] array;
    
    public IxFromArray(int start, int end, T[] array) {
        this.start = start;
        this.end = end;
        this.array = array;
    }

    @Override
    public Iterator<T> iterator() {
        return new FromArray<T>(start, end, array);
    }

    static final class FromArray<T> implements Iterator<T> {
        final T[] array;

        final int end;
        
        int index;

        public FromArray(int start, int end, T[] array) {
            this.index = start;
            this.end = end;
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return index != end;
        }

        @Override
        public T next() {
            int i = index;
            if (i != end) {
                index = i + 1;
                return array[i];
            }
            return noelements();
        }

        @Override
        public void remove() {
            unsupported();
        }
    }
}
