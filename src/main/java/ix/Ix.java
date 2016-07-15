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
 * Base class and entry point for fluent Iterables.
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
        return new IxDefer<T>(factory);
    }

    public static <T> Ix<T> generate(Action1<Observer<T>> nextSupplier) {
        return new IxGenerateStateless<T>(nextSupplier);
    }

    public static <T, S> Ix<T> generate(Func0<S> stateSupplier, Func2<S, Observer<T>, S> nextSupplier) {
        return generate(stateSupplier, nextSupplier, IxEmptyAction.instance1());
    }

    public static <T, S> Ix<T> generate(Func0<S> stateSupplier, Func2<S, Observer<T>, S> nextSupplier, Action1<? super S> stateDisposer) {
        return new IxGenerate<T, S>(stateSupplier, nextSupplier, stateDisposer);
    }
    
    public static <T, R> Ix<R> zip(Iterable<? extends T>[] sources, FuncN<R> zipper) {
        return new IxZipArray<T, R>(sources, zipper);
    }

    public static <T, R> Ix<R> zip(Iterable<? extends Iterable<? extends T>> sources, FuncN<R> zipper) {
        return new IxZipIterable<T, R>(sources, zipper);
    }

    public static <T1, T2, R> Ix<R> zip(
            Iterable<T1> it1, Iterable<T2> it2, 
            Func2<? super T1, ? super T2, ? extends R> zipper) {
        return new IxZip2<T1, T2, R>(it1, it2, zipper);
    }

    public static <T1, T2, T3, R> Ix<R> zip(
            Iterable<T1> it1, Iterable<T2> it2, 
            Iterable<T3> it3,
            Func3<? super T1, ? super T2, ? super T3, ? extends R> zipper) {
        return new IxZip3<T1, T2, T3, R>(it1, it2, it3, zipper);
    }

    public static <T1, T2, T3, T4, R> Ix<R> zip(
            Iterable<T1> it1, Iterable<T2> it2, 
            Iterable<T3> it3, Iterable<T4> it4,
            Func4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> zipper) {
        return new IxZip4<T1, T2, T3, T4, R>(it1, it2, it3, it4, zipper);
    }

    public static <T> Ix<T> repeatValue(T value) {
        return new IxRepeat<T>(value);
    }

    public static <T> Ix<T> repeatValue(T value, long count) {
        return new IxRepeatCount<T>(value, count);
    }

    public static <T> Ix<T> repeatValue(T value, Pred0 stopPredicate) {
        return repeatValue(value, Long.MAX_VALUE, stopPredicate);
    }

    public static <T> Ix<T> repeatValue(T value, long count, Pred0 stopPredicate) {
        return new IxRepeatPredicate<T>(value, count, stopPredicate);
    }

    public static <T, R> Ix<R> forloop(T seed, Pred<? super T> condition, 
            Func1<? super T, ? extends T> next,
            Func1<? super T, ? extends R> selector) {
        return new IxForloop<T, R>(seed, condition, selector, next);
    }
    
    public static Ix<String> split(String string, String by) {
        return new IxSplit(string, by);
    }

    //---------------------------------------------------------------------------------------
    // Instance operators
    //---------------------------------------------------------------------------------------

    public final <R> R as(Func1<? super Ix<T>, R> transformer) {
        return transformer.call(this);
    }

    public final <R> Ix<R> compose(Func1<? super Ix<T>, ? extends Iterable<? extends R>> transformer) {
        return new IxCompose<T, R>(this, transformer);
    }

    public final Ix<Boolean> any(Pred<? super T> predicate) {
        return new IxAny<T>(this, predicate);
    }

    
    public final Ix<Boolean> all(Pred<? super T> predicate) {
        return new IxAll<T>(this, predicate);
    }
    
    public final Ix<Boolean> hasElements() {
        return new IxHasElements<T>(this);
    }
    
    public final Ix<T> ignoreElements() {
        return new IxIgnoreElements<T>(this);
    }
    
    public final Ix<T> max(Comparator<? super T> comparator) {
        return new IxMinMax<T>(this, comparator, -1);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final Ix<T> max() {
        return max((Comparator)SelfComparator.INSTANCE);
    }

    public final Ix<T> min(Comparator<? super T> comparator) {
        return new IxMinMax<T>(this, comparator, 1);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final Ix<T> min() {
        return min((Comparator)SelfComparator.INSTANCE);
    }
    
    public final Ix<Boolean> contains(Object o) {
        return new IxContains<T>(this, o);
    }
    
    public final Ix<Integer> count() {
        return new IxCount<T>(this);
    }

    public final Ix<Long> countLong() {
        return new IxCountLong<T>(this);
    }
    
    public final Ix<T> distinct() {
        return distinct(IdentityHelper.instance());
    }

    public final <K> Ix<T> distinct(Func1<? super T, K> keySelector) {
        return new IxDistinct<T, K>(this, keySelector);
    }

    public final Ix<T> distinctUntilChanged() {
        return distinctUntilChanged(IdentityHelper.instance());
    }

    public final Ix<T> distinctUntilChanged(Pred2<? super T, ? super T> comparer) {
        return new IxDistinctUntilChanged<T, T>(this, IdentityHelper.<T>instance(), comparer);
    }

    public final <K> Ix<T> distinctUntilChanged(Func1<? super T, K> keySelector) {
        return new IxDistinctUntilChanged<T, K>(this, keySelector, EqualityHelper.INSTANCE);
    }
    
    public final Ix<T> doOnNext(Action1<? super T> action) {
        return new IxDoOn<T>(this, action, IxEmptyAction.instance0());
    }

    public final Ix<T> doOnCompleted(Action0 action) {
        return new IxDoOn<T>(this, IxEmptyAction.instance1(), action);
    }
    
    @SuppressWarnings("unchecked")
    public final Ix<T> startWith(T... values) {
        return concatArray(fromArray(values), this);
    }

    @SuppressWarnings("unchecked")
    public final Ix<T> endWith(T... values) {
        return concatArray(this, fromArray(values));
    }
    
    public final Ix<String> join() {
        return join(", ");
    }
    
    public final Ix<String> join(CharSequence separator) {
        return new IxJoin<T>(this, separator);
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
        return new IxBuffer<T>(this, size);
    }
    
    public final Ix<List<T>> buffer(int size, int skip) {
        if (size == skip) {
            return buffer(size);
        }
        if (size < skip) {
            return new IxBufferSkip<T>(this, size, skip);
        }
        return new IxBufferOverlap<T>(this, size, skip);
    }
    
    public final <K> Ix<GroupedIx<K, T>> groupBy(Func1<? super T, ? extends K> keySelector) {
        return groupBy(keySelector, IdentityHelper.<T>instance());
    }

    public final <K, V> Ix<GroupedIx<K, V>> groupBy(Func1<? super T, ? extends K> keySelector,
            Func1<? super T, ? extends V> valueSelector) {
        return new IxGroupBy<T, K, V>(this, keySelector, valueSelector);
    }
    
    public final Ix<T> repeat() {
        return concat(repeatValue(this));
    }
    
    public final Ix<T> repeat(long times) {
        return concat(repeatValue(this, times));
    }
    
    public final Ix<T> repeat(Pred0 predicate) {
        return concat(repeatValue(this, predicate));
    }

    public final Ix<T> repeat(long times, Pred0 predicate) {
        return concat(repeatValue(this, times, predicate));
    }

    public final Ix<T> publish() {
        return new IxPublish<T>(this);
    }

    public final <R> Ix<R> publish(Func1<? super Ix<T>, ? extends Iterable<? extends R>> transform) {
        return new IxPublishSelector<T, R>(this, transform);
    }

    public final Ix<T> replay() {
        return new IxReplay<T>(this);
    }

    public final Ix<T> replay(int size) {
        return new IxReplaySize<T>(this, size);
    }

    public final <R> Ix<R> replay(Func1<? super Ix<T>, ? extends Iterable<? extends R>> transform) {
        return new IxReplaySelector<T, R>(this, transform);
    }

    public final <R> Ix<R> replay(int size, Func1<? super Ix<T>, ? extends Iterable<? extends R>> transform) {
        return new IxReplaySizeSelector<T, R>(this, size, transform);
    }
    
    public final <K> Ix<Map<K, T>> toMap(Func1<? super T, ? extends K> keySelector) {
        Func1<T, T> f = IdentityHelper.instance();
        return this.toMap(keySelector, f);
    }

    public final <K, V> Ix<Map<K, V>> toMap(Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector) {
        return new IxToMap<T, K, V>(this, keySelector, valueSelector);
    }

    public final <K> Ix<Map<K, Collection<T>>> toMultimap(Func1<? super T, ? extends K> keySelector) {
        Func1<T, T> f = IdentityHelper.instance();
        return this.toMultimap(keySelector, f);
    }

    public final <K, V> Ix<Map<K, Collection<V>>> toMultimap(Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector) {
        return new IxToMultimap<T, K, V>(this, keySelector, valueSelector);
    }

    public final Ix<Ix<T>> window(int size) {
        return new IxWindow<T>(this, size);
    }

    public final Ix<Ix<T>> window(int size, int skip) {
        if (size == skip) {
            return window(size);
        }
        if (size < skip) {
            return new IxWindowSkip<T>(this, size, skip);
        }
        return new IxWindowOverlap<T>(this, size, skip);
    }
    
    @SuppressWarnings("unchecked")
    public final Ix<T> concatWith(Iterable<? extends T> other) {
        return concatArray(this, other);
    }

    public final Ix<T> mergeWith(Iterable<? extends T> other) {
        return concatWith(other);
    }
    
    public final <U, R> Ix<R> zipWith(Iterable<U> other, Func2<? super T, ? super U, ? extends R> zipper) {
        return zip(this, other, zipper);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final Ix<T> orderBy() {
        return orderBy((Comparator)SelfComparator.INSTANCE);
    }
    
    public final Ix<T> orderBy(Comparator<? super T> comparator) {
        return new IxOrderBy<T, T>(this, IdentityHelper.<T>instance(), comparator, 1);
    }
    
    public final <K extends Comparable<? super K>> Ix<T> orderBy(Func1<? super T, K> keySelector) {
        return new IxOrderBy<T, K>(this, keySelector, SelfComparator.INSTANCE, 1);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final Ix<T> orderByReverse() {
        return orderByReverse((Comparator)SelfComparator.INSTANCE);
    }
    
    public final Ix<T> orderByReverse(Comparator<? super T> comparator) {
        return new IxOrderBy<T, T>(this, IdentityHelper.<T>instance(), comparator, -1);
    }
    
    public final <K extends Comparable<? super K>> Ix<T> orderByReverse(Func1<? super T, K> keySelector) {
        return new IxOrderBy<T, K>(this, keySelector, SelfComparator.INSTANCE, -1);
    }

    public final Ix<T> scan(Func2<T, T, T> scanner) {
        return new IxScan<T>(this, scanner);
    }

    public final <R> Ix<R> scan(Func0<R> initialFactory, Func2<R, T, R> scanner) {
        return new IxScanSeed<T, R>(this, initialFactory, scanner);
    }
    
    public final Ix<T> remove(Pred<? super T> predicate) {
        return new IxRemove<T>(this, predicate);
    }

    public final Ix<T> retain(Pred<? super T> predicate) {
        return new IxRetain<T>(this, predicate);
    }

    public final <R> Ix<R> transform(IxTransform<T, R> transformer) {
        return new IxTransformer<T, R>(this, transformer);
    }
    
    public final <R> Ix<R> lift(Func1<? super Iterator<T>, ? extends Iterator<R>> lifter) {
        return new IxLift<T, R>(this, lifter);
    }
    
    @SuppressWarnings("unchecked")
    public final Ix<Float> averageFloat() {
        return new IxAverageFloat((Iterable<Number>)this);
    }

    @SuppressWarnings("unchecked")
    public final Ix<Double> averageDouble() {
        return new IxAverageDouble((Iterable<Number>)this);
    }

    public final Ix<T> defaultIfEmpty(T value) {
        return switchIfEmpty(Ix.just(value));
    }
    
    public final Ix<T> switchIfEmpty(Iterable<? extends T> other) {
        return new IxSwitchIfEmpty<T>(this, other);
    }
    
    public final Ix<T> except(Iterable<? extends T> other) {
        return new IxExcept<T>(this, other);
    }
    
    public final Ix<T> intersect(Iterable<? extends T> other) {
        return new IxIntersect<T>(this, other);
    }
    
    public final Ix<T> reverse() {
        return new IxReverse<T>(this);
    }
    
    public final Ix<Boolean> sequenceEqual(Iterable<? extends T> other) {
        return sequenceEqual(other, EqualityHelper.INSTANCE);
    }

    public final Ix<Boolean> sequenceEqual(Iterable<? extends T> other, Pred2<? super T, ? super T> comparer) {
        return new IxSequenceEqual<T>(this, other, comparer);
    }
    
    public final Ix<Set<T>> toSet() {
        return new IxToSet<T>(this);
    }
    
    public final Ix<T> union(Iterable<? extends T> other) {
        return new IxUnion<T>(this, other);
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
                } else {
                    System.out.print(s);
                    len += s.length();
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

    
    public final T single() {
        Iterator<T> it = iterator();
        if (it.hasNext()) {
            T v = it.next();
            if (it.hasNext()) {
                throw new IndexOutOfBoundsException("The source has more than one element.");
            }
            return v;
        }
        throw new NoSuchElementException("The source is empty.");
    }
    
    public final T single(T defaultValue) {
        Iterator<T> it = iterator();
        if (it.hasNext()) {
            T v = it.next();
            if (it.hasNext()) {
                throw new IndexOutOfBoundsException("The source has more than one element.");
            }
            return v;
        }
        return defaultValue;
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
