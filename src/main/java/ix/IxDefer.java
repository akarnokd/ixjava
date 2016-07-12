package ix;

import java.util.Iterator;

import rx.functions.Func0;

final class IxDefer<T> extends Ix<T> {

    final Func0<? extends Iterable<? extends T>> factory;
    
    public IxDefer(Func0<? extends Iterable<? extends T>> factory) {
        this.factory = factory;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Iterator<T> iterator() {
        return (Iterator<T>)factory.call().iterator();
    }
}
