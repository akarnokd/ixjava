package ix;

import rx.functions.Func1;

enum NumberToLongHelper implements Func1<Number, Long> {
    INSTANCE;
    
    @Override
    public Long call(Number t1) {
        return t1.longValue();
    }
}