/*
 * Copyright 2011-2016 David Karnok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ix;

import java.util.*;
import java.util.concurrent.Callable;

import rx.exceptions.Exceptions;
import rx.functions.*;

/**
 * Base class and entry point for fluent iterables.
 * @param <T> the value type
 */
public abstract class Ix<T> implements Iterable<T> {

    public static <T> Ix<T> just(T value) {
        return new IxJust<T>(value);
    }
    
    public static <T> Ix<T> empty() {
        return IxEmpty.instance();
    }
    
    /**
     * Checks if the value is null and if so, throws
     * a NullPointerException with the given message.
     * @param <U> the value type
     * @param value the value to check for null
     * @param message the message to report in the exception
     * @return the value
     */
    protected static <U> U nullCheck(U value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
        return value;
    }
    
    /**
     * Convenience method to throw UnsupportedOperationException();
     */
    protected static void unsupported() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Convenience method to throw NoSuchElementException();
     * @param <U> the value type
     * @return never returns as it throws
     */
    protected static <U> U noelements() {
        throw new NoSuchElementException();
    }
    
    public static <T> Ix<T> from(Iterable<T> source) {
        if (source instanceof Ix) {
            return (Ix<T>)source;
        }
        return new IxWrapper<T>(nullCheck(source, "source"));
    }
    
    public static Ix<Integer> range(int start, int count) {
        if (count == 0) {
            return empty();
        }
        if (count == 1) {
            return just(start);
        }
        return new IxRange(start, count);
    }
    
    public static Ix<Integer> characters(CharSequence cs) {
        return characters(cs, 0, cs.length());
    }
    
    public static Ix<Integer> characters(CharSequence cs, int start, int end) {
        int len = cs.length();
        if (start < 0 || start + end > len) {
            throw new IndexOutOfBoundsException("start=" + start + ", end=" + end + ", length=" + len);
        }
        return new IxCharacters(cs, start, end);
    }
    
    //---------------------------------------------------------------------------------------
    // Instance operators
    //---------------------------------------------------------------------------------------
    
    public final <R> Ix<R> map(Func1<? super T, ? extends R> mapper) {
        return new IxMap<T, R>(this, mapper);
    }
    
    public final T first() {
        if (this instanceof Callable) {
            @SuppressWarnings("unchecked")
            Callable<T> c = (Callable<T>) this;
            
            try {
                return c.call();
            } catch (Exception ex) {
                Exceptions.propagate(ex);
            }
        }
        return iterator().next();
    }

    public final T first(T defaultValue) {
        if (this instanceof Callable) {
            @SuppressWarnings("unchecked")
            Callable<T> c = (Callable<T>) this;
            
            try {
                return c.call();
            } catch (Exception ex) {
                Exceptions.propagate(ex);
            }
        }
        Iterator<T> it = iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return defaultValue;
    }

    public final T last() {
        if (this instanceof Callable) {
            @SuppressWarnings("unchecked")
            Callable<T> c = (Callable<T>) this;
            
            try {
                return c.call();
            } catch (Exception ex) {
                Exceptions.propagate(ex);
            }
        }
        Iterator<T> it = iterator();
        if (!it.hasNext()) {
            return noelements();
        }
        
        for (;;) {
            T t = it.next();
            if (!it.hasNext()) {
                return t;
            }
        }
    }
    
    public final T last(T defaultValue) {
        if (this instanceof Callable) {
            @SuppressWarnings("unchecked")
            Callable<T> c = (Callable<T>) this;
            
            try {
                return c.call();
            } catch (Exception ex) {
                Exceptions.propagate(ex);
            }
        }
        Iterator<T> it = iterator();
        if (!it.hasNext()) {
            return defaultValue;
        }
        
        for (;;) {
            T t = it.next();
            if (!it.hasNext()) {
                return t;
            }
        }
    }
    
    public final Ix<T> filter(Pred<T> predicate) {
        return new IxFilter<T>(this, predicate);
    }
    
    public final <C> Ix<C> collect(Func0<C> initialFactory, Action2<C, T> collector) {
        return new IxCollect<T, C>(this, initialFactory, collector);
    }
    
    public final <C> Ix<C> reduce(Func0<C> initialFactory, Func2<C, T, C> reducer) {
        return new IxReduce<T, C>(this, initialFactory, reducer);
    }
    
    public final Ix<T> hide() {
        return new IxWrapper<T>(this);
    }
    
    public final Ix<List<T>> toList() {
        return collect(ToListHelper.<T>initialFactory(), ToListHelper.<T>collector());
    }
    
    public final Ix<Object[]> toArray() {
        return collect(ToListHelper.<T>initialFactory(), ToListHelper.<T>collector())
                .map(ToListHelper.<T>toArray());
    }
}
