package ix;

import rx.functions.Func1;

enum IdentityHelper implements Func1<Object, Object> {
    INSTANCE
    ;
    
    @SuppressWarnings("unchecked")
    public static <T> Func1<T, T> instance() {
        return (Func1<T, T>)INSTANCE;
    }
    
    @Override
    public Object call(Object t) {
        return t;
    }
}