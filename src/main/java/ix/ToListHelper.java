package ix;

import java.util.*;

import rx.functions.*;

enum ToListHelper implements Func0<List<Object>>, Action2<List<Object>, Object>, Func1<List<Object>, Object[]> {
    INSTANCE;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> Func0<List<T>> initialFactory() {
        return (Func0)INSTANCE;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> Action2<List<T>, T> collector() {
        return (Action2)INSTANCE;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> Func1<List<T>, Object[]> toArray() {
        return (Func1)INSTANCE;
    }
    @Override
    public void call(List<Object> t1, Object t2) {
        t1.add(t2);
    }

    @Override
    public List<Object> call() {
        return new ArrayList<Object>();
    }

    @Override
    public Object[] call(List<Object> t) {
        return t.toArray();
    }
    
}
