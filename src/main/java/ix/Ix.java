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

import rx.Observer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.*;

/**
 * Base class and entry point for fluent iterables.
 * 
 * @param <T> the value type
 * @since 1.0
 */
public abstract class Ix<T> implements Iterable<T> {

    public static <T> Ix<T> just(T value) {
        return new IxJust<T>(value);
    }
    
    public static <T> Ix<T> empty() {
        return IxEmpty.instance();
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
        return new IxCharacters(cs, 0, cs.length());
    }
    
    public static Ix<Integer> characters(CharSequence cs, int start, int end) {
        int len = cs.length();
        if (start < 0 || end < 0 || start > len || end > len) {
            throw new IndexOutOfBoundsException("start=" + start + ", end=" + end + ", length=" + len);
        }
        return new IxCharacters(cs, start, end);
    }
    
    public static <T> Ix<T> fromArray(T... values) {
        int n = values.length;
        if (n == 0) {
            return empty();
        }
        if (n == 1) {
            return just(values[0]);
        }
        return new IxFromArray<T>(0, values.length, values);
    }

    public static <T> Ix<T> fromArrayRange(int start, int end, T... values) {
        if (start < 0 || end < 0 || start > values.length || end > values.length) {
            throw new IndexOutOfBoundsException("start=" + start + ", end=" + end + ", length=" + values.length);
        }
        return new IxFromArray<T>(start, end, values);
    }

    @SuppressWarnings("unchecked")
    public static <T> Ix<T> concatArray(Iterable<? extends T>... sources) {
        int n = sources.length;
        if (n == 0) {
            return empty();
        }
        if (n == 1) {
            return from((Iterable<T>)sources[0]);
        }
        return new IxFlattenArrayIterable<T>((Iterable<T>[])sources);
    }

    public static <T> Ix<T> mergeArray(Iterable<? extends T>... sources) {
        return concatArray(sources); // concat and merge are the same in the Iterable world
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Ix<T> concat(Iterable<? extends Iterable<? extends T>> sources) {
        return new IxFlattenIterable<Iterable<? extends T>, T>(
                (Iterable)sources, 
                IdentityHelper.<Iterable<? extends T>>instance());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Ix<T> merge(Iterable<? extends Iterable<? extends T>> sources) {
        return new IxFlattenIterable<Iterable<? extends T>, T>(
                (Iterable)sources, 
                IdentityHelper.<Iterable<? extends T>>instance());
    }

    public static <T> Ix<T> defer(Func0<? extends Iterable<? extends T>> factory) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public static <T> Ix<T> generate(Action1<Observer<T>> nextSupplier) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public static <T, S> Ix<T> generate(Func0<S> stateSupplier, Func2<S, Observer<T>, S> nextSupplier) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public static <T, S> Ix<T> generate(Func0<S> stateSupplier, Func2<S, Observer<T>, S> nextSupplier, Action1<? super S> stateDisposer) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public static <T, R> Ix<R> zip(Iterable<? extends T>[] sources, FuncN<R> zipper) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public static <T, R> Ix<R> zip(Iterable<? extends Iterable<? extends T>> sources, FuncN<R> zipper) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public static <T1, T2, R> Ix<R> zip(
            Iterable<T1> it1, Iterable<T2> it2, 
            Func2<? super T1, ? super T2, ? extends R> zipper) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public static <T1, T2, T3, R> Ix<R> zip(
            Iterable<T1> it1, Iterable<T2> it2, 
            Iterable<T3> it3,
            Func3<? super T1, ? super T2, ? super T3, ? extends R> zipper) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public static <T1, T2, T3, T4, R> Ix<R> zip(
            Iterable<T1> it1, Iterable<T2> it2, 
            Iterable<T3> it3, Iterable<T3> it4,
            Func4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> zipper) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public static <T> Ix<T> repeat(T value) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public static <T> Ix<T> repeat(T value, long count) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public static <T> Ix<T> repeat(T value, Pred0 stopPredicate) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public static <T> Ix<T> forloop(T seed, Pred<? super T> condition, Func1<? super T, ? extends T> next) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    //---------------------------------------------------------------------------------------
    // Instance operators
    //---------------------------------------------------------------------------------------

    public final <R> R as(Func1<? super Ix<T>, R> transformer) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final <R> Ix<R> compose(Func1<? super Ix<T>, ? extends Iterable<? extends R>> transformer) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<Boolean> any(Pred<? super T> predicate) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    
    public final Ix<Boolean> all(Pred<? super T> predicate) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<Boolean> hasElements() {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<T> ignoreElements() {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<T> max(Comparator<? super T> comparator) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<T> max() {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<T> min(Comparator<? super T> comparator) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<T> min() {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<Boolean> contains(Object o) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<Integer> count() {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<Long> countLong() {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<T> distinct() {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<T> distinct(Pred2<? super T, ? super T> comparer) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final <K> Ix<T> distinct(Func1<? super T, K> keySelector) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<T> distinctUntilChanged() {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<T> distinctUntilChanged(Pred2<? super T, ? super T> comparer) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final <K> Ix<T> distinctUntilChanged(Func1<? super T, K> keySelector) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<T> doOnNext(Action1<? super T> action) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<T> doOnCompleted(Action1<? super T> action) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<T> startWith(T... value) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<T> endWith(T... value) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<String> join() {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<String> join(CharSequence separator) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final <R> Ix<R> map(Func1<? super T, ? extends R> mapper) {
        return new IxMap<T, R>(this, mapper);
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
    
    public final Ix<T> reduce(Func2<T, T, T> reducer) {
        return new IxAggregate<T>(this, reducer);
    }
    
    @SuppressWarnings("unchecked")
    public final Ix<Integer> maxInt() {
        return new IxMaxInt((Ix<Integer>)this);
    }

    @SuppressWarnings("unchecked")
    public final Ix<Integer> minInt() {
        return new IxMinInt((Ix<Integer>)this);
    }

    @SuppressWarnings("unchecked")
    public final Ix<Integer> sumInt() {
        return new IxSumInt((Ix<Integer>)this);
    }

    @SuppressWarnings("unchecked")
    public final Ix<Long> maxLong() {
        return new IxMaxLong((Ix<Long>)this);
    }

    @SuppressWarnings("unchecked")
    public final Ix<Long> minLong() {
        return new IxMinLong((Ix<Long>)this);
    }

    @SuppressWarnings("unchecked")
    public final Ix<Long> sumLong() {
        return new IxSumLong((Ix<Long>)this);
    }
    
    @SuppressWarnings("unchecked")
    public final Ix<Long> toLong() {
        return ((Ix<Number>)this).map(NumberToLongHelper.INSTANCE);
    }

    public final Ix<T> skip(int n) {
        if (n == 0) {
            return this;
        }
        return new IxSkip<T>(this, n);
    }

    public final Ix<T> take(int n) {
        return new IxTake<T>(this, n);
    }

    public final Ix<T> skipLast(int n) {
        if (n == 0) {
            return this;
        }
        return new IxSkipLast<T>(this, n);
    }
    
    public final Ix<T> takeLast(int n) {
        return new IxTakeLast<T>(this, n);
    }
    
    public final <R> Ix<R> flatMap(Func1<? super T, ? extends Iterable<? extends R>> mapper) {
        return new IxFlattenIterable<T, R>(this, mapper);
    }
    
    public final <R> Ix<R> concatMap(Func1<? super T, ? extends Iterable<? extends R>> mapper) {
        return new IxFlattenIterable<T, R>(this, mapper);
    }

    public final Ix<T> skipWhile(Pred<? super T> predicate) {
        return new IxSkipWhile<T>(this, predicate);
    }
    
    public final Ix<T> takeWhile(Pred<? super T> predicate) {
        return new IxTakeWhile<T>(this, predicate);
    }
    
    public final Ix<T> takeUntil(Pred<? super T> stopPredicate) {
        return new IxTakeUntil<T>(this, stopPredicate);
    }
    
    public final Ix<List<T>> buffer(int size) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<List<T>> buffer(int size, int skip) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final <K> Ix<GroupedIx<K, T>> groupBy(Func1<? super T, ? extends K> keySelector) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final <K, V> Ix<GroupedIx<K, V>> groupBy(Func1<? super T, ? extends K> keySelector,
            Func1<? super T, ? extends V> valueSelector) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<T> repeat() {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<T> repeat(long times) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<T> repeat(Pred0 predicate) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<T> repeat(long times, Pred0 predicate) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<T> publish() {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final <R> Ix<R> publish(Func1<? super Ix<T>, ? extends Iterator<? extends R>> transform) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<T> replay() {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<T> replay(int size) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final <R> Ix<R> replay(Func1<? super Ix<T>, ? extends Iterator<? extends R>> transform) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final <R> Ix<R> replay(int size, Func1<? super Ix<T>, ? extends Iterator<? extends R>> transform) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final <K> Ix<Map<K, T>> toMap(Func1<? super T, ? extends K> keySelector) {
        Func1<T, T> f = IdentityHelper.instance();
        return this.toMap(keySelector, f);
    }

    public final <K, V> Ix<Map<K, V>> toMap(Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final <K> Ix<Map<K, Collection<T>>> toMultimap(Func1<? super T, ? extends K> keySelector) {
        Func1<T, T> f = IdentityHelper.instance();
        return this.toMultimap(keySelector, f);
    }

    public final <K, V> Ix<Map<K, Collection<V>>> toMultimap(Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<Ix<T>> window(int size) {
        return window(size, size);
    }

    public final Ix<Ix<T>> window(int size, int skip) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<T> concatWith(Iterator<? extends T> other) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final Ix<T> mergeWith(Iterator<? extends T> other) {
        return concatWith(other);
    }
    
    public final <U, R> Ix<R> zipWith(Iterator<U> other, Func2<? super T, ? super U, ? extends R> zipper) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<T> orderBy() {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<T> orderBy(Comparator<? super T> comparator) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final <K extends Comparable<? super K>> Ix<T> orderBy(Func1<? super T, K> keySelector) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<T> scan(Func2<T, T, T> scanner) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final <R> Ix<R> scan(Func0<R> initialFactory, Func2<R, T, R> scanner) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final Ix<T> remove(Pred<? super T> predicate) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    public final <R> Ix<R> transform(IxTransform<T, R> transformer) {
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    public final <R> Ix<R> lift(Func1<? super Iterator<T>, ? extends Iterator<R>> lifter) {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    //---------------------------------------------------------------------------------------
    // Leaving the Iterable world
    //---------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public final T first() {
        if (this instanceof Callable) {
            return checkedCall((Callable<T>) this);
        }
        return iterator().next();
    }

    @SuppressWarnings("unchecked")
    public final T first(T defaultValue) {
        if (this instanceof Callable) {
            return checkedCall((Callable<T>) this);
        }
        Iterator<T> it = iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public final T last() {
        if (this instanceof Callable) {
            return checkedCall((Callable<T>) this);
        }
        Iterator<T> it = iterator();
        if (!it.hasNext()) {
            throw new NoSuchElementException();
        }
        
        for (;;) {
            T t = it.next();
            if (!it.hasNext()) {
                return t;
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public final T last(T defaultValue) {
        if (this instanceof Callable) {
            return checkedCall((Callable<T>) this);
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
    
    public final void removeAll() {
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }
    
    public final void foreach(Action1<? super T> action) {
        for (T t : this) {
            action.call(t);
        }
    }
    
    public final void foreachWhile(Pred<? super T> action) {
        for (T t : this) {
            if (!action.test(t)) {
                break;
            }
        }
    }

    public final <U extends Collection<? super T>> U into(U collection) {
        for (T v : this) {
            collection.add(v);
        }
        return collection;
    }

    public final void print() {
        print(", ", 80);
    }

    public final void print(CharSequence separator, int charsPerLine) {
        boolean first = true;
        int len = 0;
        
        for (T v : this) {
            
            String s = String.valueOf(v);

            if (first) {
                System.out.print(s);
                len += s.length();
                first = false;
            } else {
                System.out.print(separator);
                len += separator.length();
                if (len > charsPerLine) {
                    System.out.println();;
                    System.out.print(s);
                    len = s.length();
                }
            }
            
        }
    }

    public final void println() {
        for (T v : this) {
            System.out.println(v);
        }
    }

    public final void println(CharSequence prefix) {
        for (T v : this) {
            System.out.print(prefix);
            System.out.println(v);
        }
    }

    /**
     * Iterates over this instance, dropping all values it produces.
     */
    public final void run() {
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            it.next();
        }
    }

    public final void subscribe() {
        run();
    }

    public final void subscribe(Action1<? super T> consumer) {
        for (T v : this) {
            consumer.call(v);
        }
    }

    public final void subscribe(Action1<? super T> consumer, Action1<Throwable> onError) {
        try {
            for (T v : this) {
                consumer.call(v);
            }
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            onError.call(ex);
        }
    }

    public final void subscribe(Action1<? super T> consumer, Action1<Throwable> onError, Action0 onCompleted) {
        try {
            for (T v : this) {
                consumer.call(v);
            }
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            onError.call(ex);
            return;
        }
        onCompleted.call();
    }

    public final void subscribe(Observer<? super T> observer) {
        try {
            for (T v : this) {
                observer.onNext(v);
            }
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            observer.onError(ex);
            return;
        }
        observer.onCompleted();
    }
    
    public final void subscribe(Subscriber<? super T> subscriber) {
        try {
            for (T v : this) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                subscriber.onNext(v);
            }
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            if (subscriber.isUnsubscribed()) {
                return;
            }
            subscriber.onError(ex);
            return;
        }
        if (subscriber.isUnsubscribed()) {
            return;
        }
        subscriber.onCompleted();
    }

    /**
     * Consumes this Iterable and removes all elements for
     * which the predicate returns false; in other words,
     * retain those elements of a mutable source that match
     * the predicate.
     * @param predicate the predicate called with the current
     * element and should return true for elements to keep, false
     * for elements to remove.
     * @throws UnsupportedOperationException if the this Iterable
     * doesn't allow removing elements.
     * @see #removeAll(Pred)
     * @since 1.0
     */
    public final void retainAll(Pred<? super T> predicate) {
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            T v = it.next();
            if (!predicate.test(v)) {
                it.remove();
            }
        }
    }

    /**
     * Consumes this Iterable and removes all elements for
     * which the predicate returns true; in other words,
     * remove those elements of a mutable source that match
     * the predicate.
     * @param predicate the predicate called with the current
     * element and should return true for elements to remove, false
     * for elements to keep.
     * @throws UnsupportedOperationException if the this Iterable
     * doesn't allow removing elements.
     * @see #retainAll(Pred)
     * @see #removeAll()
     * @since 1.0
     */
    public final void removeAll(Pred<? super T> predicate) {
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            T v = it.next();
            if (predicate.test(v)) {
                it.remove();
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    // Helper methods
    // --------------------------------------------------------------------------------------------
    
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
     * Calls the given callable and rethrows its exception
     * (as RuntimeException if necessary).
     * @param <U> the value type
     * @param callable the callable to call
     * @return the value returned by the callable
     */
    protected static <U> U checkedCall(Callable<U> callable) {
        try {
            return callable.call();
        } catch (Throwable ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            }
            if (ex instanceof Error) {
                throw (Error)ex;
            }
            throw new RuntimeException(ex);
        }
    }
}
