package ix;

import java.util.Iterator;

import rx.functions.*;

final class IxDoOn<T> extends IxSource<T, T> {

    final Action1<? super T> onNext;
    
    final Action0 onCompleted;
    
    public IxDoOn(Iterable<T> source, Action1<? super T> onNext, Action0 onCompleted) {
        super(source);
        this.onNext = onNext;
        this.onCompleted = onCompleted;
    }

    @Override
    public Iterator<T> iterator() {
        return new DoOnIterator<T>(source.iterator(), onNext, onCompleted);
    }

    static final class DoOnIterator<T> extends IxSourceIterator<T, T> {
        final Action1<? super T> onNext;
        
        final Action0 onCompleted;

        public DoOnIterator(Iterator<T> it, Action1<? super T> onNext, Action0 onCompleted) {
            super(it);
            this.onNext = onNext;
            this.onCompleted = onCompleted;
        }

        @Override
        protected boolean moveNext() {
            if (it.hasNext()) {
                T v = it.next();
                value = v;
                hasValue = true;
                onNext.call(v);
                return true;
            }
            onCompleted.call();
            done = true;
            return false;
        }
    }
}
