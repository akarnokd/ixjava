package ix;

import java.util.Iterator;

final class IxFilter<T> extends IxSource<T, T> {

    final Pred<? super T> predicate;
    
    public IxFilter(Iterable<T> source, Pred<? super T> predicate) {
        super(source);
        this.predicate = predicate;
    }

    @Override
    public Iterator<T> iterator() {
        return new FilterIterator<T>(source.iterator(), predicate);
    }

    static final class FilterIterator<T> extends IxBaseIterator<T, T> {

        final Pred<? super T> predicate;
        
        public FilterIterator(Iterator<T> it, Pred<? super T> predicate) {
            super(it);
            this.predicate = predicate;
        }

        @Override
        protected boolean moveNext() {
            for (;;) {
                if (it.hasNext()) {
                    T v = it.next();
                    if (predicate.test(v)) {
                        value = v;
                        hasValue = true;
                        return true;
                    }
                } else {
                    done = true;
                    return false;
                }
            }
        }
        
        @Override
        public void remove() {
            it.remove();
        }
    }
}
