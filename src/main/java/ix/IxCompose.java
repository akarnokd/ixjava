package ix;

import java.util.Iterator;

import rx.functions.Func1;

final class IxCompose<T, R> extends IxSource<T, R> {

    final Func1<? super Ix<T>, ? extends Iterable<? extends R>> transformer;
    
    public IxCompose(Iterable<T> source, Func1<? super Ix<T>, ? extends Iterable<? extends R>> transformer) {
        super(source);
        this.transformer = transformer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<R> iterator() {
        return (Iterator<R>)transformer.call(from(source)).iterator();
    }

}
