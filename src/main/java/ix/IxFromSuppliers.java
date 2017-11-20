package ix;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IxFromSuppliers<T> extends Ix<T> {

    final int start;
    final int end;
    final IxSupplier<T>[] array;

    IxFromSuppliers(int start, int end, IxSupplier<T>[] array) {
        this.start = start;
        this.end = end;
        this.array = array;
    }

    @Override
    public Iterator<T> iterator() {
        return new FromSuppliers<T>(start, end, array);
    }

    static final class FromSuppliers<T> implements Iterator<T> {
        final IxSupplier<T>[] array;

        final int end;

        int index;

        FromSuppliers(int start, int end, IxSupplier<T>[] array) {
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
                return array[i].get();
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
