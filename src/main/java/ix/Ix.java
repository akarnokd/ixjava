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
 * <p>
 * All operators tolerate {@code null} elements.
 * <p>
 * The Iterables have to be run in a single-threaded manner and none of
 * the participating operators expect or support concurrency.
 * @param <T> the value type
 * @since 1.0
 */
public abstract class Ix<T> implements Iterable<T> {

    /**
     * Emits all characters from the given CharSequence as integer values.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param cs the source character sequence, not null
     * @return the new Ix instance
     * @throws NullPointerException if cs is null
     * @since 1.0
     */
    public static Ix<Integer> characters(CharSequence cs) {
        return new IxCharacters(cs, 0, cs.length());
    }
    
    /**
     * Emits a range of characters from the given CharSequence as integer values.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param cs the source character sequence, not null
     * @param start the start character index, inclusive, non-negative
     * @param end the end character index, exclusive, non-negative
     * @return the new Ix instance
     * @throws NullPointerException if cs is null
     * @throws IndexOutOfBoundsException if start is out of range [0, cs.length]
     * @since 1.0
     */
    public static Ix<Integer> characters(CharSequence cs, int start, int end) {
        int len = cs.length();
        if (start < 0 || end < 0 || start > len || end > len) {
            throw new IndexOutOfBoundsException("start=" + start + ", end=" + end + ", length=" + len);
        }
        return new IxCharacters(cs, start, end);
    }

    /**
     * Concatenates the elements of Iterable sources, provided as an Iterable itself, sequentially.
     * <p>
     * The result's iterator() forwards the remove() calls to the current iterator.
     * <p>
     * Note that merge and concat operations are the same in the Iterable world.
     * @param <T> the common base type
     * @param sources the Iterable sequence of source Iterables
     * @return the new Ix instance
     * @throws NullPointerException if sources is null
     * @since 1.0
     * @see #merge(Iterable)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Ix<T> concat(Iterable<? extends Iterable<? extends T>> sources) {
        return new IxFlattenIterable<Iterable<? extends T>, T>(
                (Iterable)nullCheck(sources, "sources is null"), 
                IdentityHelper.<Iterable<? extends T>>instance());
    }

    /**
     * Concatenates the elements of two Iterable sources sequentially
     * <p>
     * The result's iterator() forwards the remove() calls to the current iterator.
     * <p>
     * Note that merge and concat operations are the same in the Iterable world.
     * @param <T> the value type
     * @param source1 the first source, not null
     * @param source2 the second source, not null
     * @return the new Iterable source
     * @throws NullPointerException if any of the sources is null
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public static <T> Ix<T> concat(Iterable<? extends T> source1, Iterable<? extends T> source2) {
        return concatArray(nullCheck(source1, "source1 is null"), nullCheck(source2, "source2 is null"));
    }

    /**
     * Concatenates the elements of three Iterable sources sequentially
     * <p>
     * The result's iterator() forwards the remove() calls to the current iterator.
     * <p>
     * Note that merge and concat operations are the same in the Iterable world.
     * @param <T> the value type
     * @param source1 the first source, not null
     * @param source2 the second source, not null
     * @param source3 the third source, not null
     * @return the new Iterable source
     * @throws NullPointerException if any of the sources is null
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public static <T> Ix<T> concat(Iterable<? extends T> source1, Iterable<? extends T> source2,
            Iterable<? extends T> source3) {
        return concatArray(nullCheck(source1, "source1 is null"), 
                nullCheck(source2, "source2 is null"), nullCheck(source3, "source3 is null"));
    }

    /**
     * Concatenates the elements of three Iterable sources sequentially
     * <p>
     * The result's iterator() forwards the remove() calls to the current iterator.
     * <p>
     * Note that merge and concat operations are the same in the Iterable world.
     * @param <T> the value type
     * @param source1 the first source, not null
     * @param source2 the second source, not null
     * @param source3 the third source, not null
     * @param source4 the fourth source, not null
     * @return the new Iterable source
     * @throws NullPointerException if any of the sources is null
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public static <T> Ix<T> concat(Iterable<? extends T> source1, Iterable<? extends T> source2,
            Iterable<? extends T> source3, Iterable<? extends T> source4) {
        return concatArray(nullCheck(source1, "source1 is null"), nullCheck(source2, "source2 is null"), 
                nullCheck(source3, "source3 is null"), nullCheck(source4, "source4 is null"));
    }

    /**
     * Concatenates the elements of Iterable sources, provided as an array, sequentially.
     * <p>
     * The result's iterator() forwards the remove() calls to the current iterator.
     * <p>
     * Note that merge and concat operations are the same in the Iterable world.
     * @param <T> the common base type
     * @param sources the array of source Iterables
     * @return the new Ix instance
     * @throws NullPointerException if sources is null
     * @since 1.0
     * @see #mergeArray(Iterable...)
     */
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

    /**
     * Defers the generation of the actual Iterable till the iterator() is called on
     * the resulting Ix.
     * <p>
     * The result's iterator() forwards the remove() calls to the generated Iterable's Iterator.
     * @param <T> the value type
     * @param factory the function that returns an Iterable when the resulting Ix.iterator() is called
     * @return the new Ix source
     * @throws NullPointerException if factory is null
     * @since 1.0
     */
    public static <T> Ix<T> defer(Func0<? extends Iterable<? extends T>> factory) {
        return new IxDefer<T>(nullCheck(factory, "factory is null"));
    }
    
    /**
     * No elements are emitted.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <T> the value type
     * @return the new Ix instance
     * @since 1.0
     */
    public static <T> Ix<T> empty() {
        return IxEmpty.instance();
    }
    
    /**
     * Wraps the given Interable source into an Ix instance (if
     * not already an Ix subclass).
     * <p>
     * The result's iterator() forwards the remove() calls to the source's
     * iterator().
     * @param <T> the value type
     * @param source the Iterable to wrap, not null
     * @return the new Ix instance
     * @throws NullPointerException if source is null
     * @since 1.0
     */
    public static <T> Ix<T> from(Iterable<T> source) {
        if (source instanceof Ix) {
            return (Ix<T>)source;
        }
        return new IxWrapper<T>(nullCheck(source, "source"));
    }
    
    /**
     * Emits all the elements of the given array. 
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <T> the value type
     * @param values the array of values, not null
     * @return the new Ix instance
     * @throws NullPointerException if values is null
     * @since 1.0
     */
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

    /**
     * Emits a range of elements from the given array.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <T> the value type
     * @param start the staring index, inclusive, non-negative
     * @param end the end index, exclusive, non-negative
     * @param values the array of values
     * @return the new Ix instance
     * @throws NullPointerException if values is null
     * @throws IndexOutOfBoundsException if either start or end are not in [0, values.length]
     * @since 1.0
     */
    public static <T> Ix<T> fromArrayRange(int start, int end, T... values) {
        if (start < 0 || end < 0 || start > values.length || end > values.length) {
            throw new IndexOutOfBoundsException("start=" + start + ", end=" + end + ", length=" + values.length);
        }
        return new IxFromArray<T>(start, end, values);
    }

    /**
     * Generates a sequence of values via a generic indexed for-loop style construct;
     * the index starts with the given seed, checked via a condition (to terminate),
     * generated from the index via the selector and then a new index is generated via next.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <T> the index value type
     * @param <R> the result value type
     * @param seed the initial value of the index
     * @param condition the receives the current index (before selector is called) and if it
     * returns false, the sequence terminates
     * @param next the function that receives the current index and returns the next index
     * @param selector the function that receives the current index and returns the value
     * to be emitted.
     * @return the new Ix instance
     * @throws NullPointerException if condition, next or selector is null
     * @since 1.0
     */
    public static <T, R> Ix<R> forloop(T seed, Pred<? super T> condition, 
            Func1<? super T, ? extends T> next,
            Func1<? super T, ? extends R> selector) {
        return new IxForloop<T, R>(seed, nullCheck(condition, "condition is null"),
                nullCheck(selector, "selector is null"), nullCheck(next, "next is null"));
    }

    /**
     * Calls the given action to generate a value or terminate whenever the next() 
     * is called on the resulting Ix.iterator().
     * <p>
     * The result's iterator() doesn't support remove().
     * <p>
     * The action may call {@code onNext} at most once to signal the next value per action invocation.
     * The {@code onCompleted} should be called to indicate no further values will be generated (may be
     * called with an onNext in the same action invocation). Calling {@code onError} will immediately
     * throw the given exception (as is if it's a RuntimeException or Error; or wrapped into a RuntimeException). 
     * @param <T> the value type
     * @param nextSupplier the action called with an Observer API to receive value, not null
     * @return the new Ix instance
     * @throws NullPointerException if nextSupplier is null
     * @since 1.0
     */
    public static <T> Ix<T> generate(Action1<Observer<T>> nextSupplier) {
        return new IxGenerateStateless<T>(nullCheck(nextSupplier, "nextSupplier is null"));
    }

    /**
     * Calls the given function (with per-iterator state) to generate a value or terminate 
     * whenever the next() is called on the resulting Ix.iterator().
     * <p>
     * The result's iterator() doesn't support remove().
     * <p>
     * The action may call {@code onNext} at most once to signal the next value per action invocation.
     * The {@code onCompleted} should be called to indicate no further values will be generated (may be
     * called with an onNext in the same action invocation). Calling {@code onError} will immediately
     * throw the given exception (as is if it's a RuntimeException or Error; or wrapped into a RuntimeException). 
     * @param <T> the value type
     * @param <S> the state type supplied to and returned by the nextSupplier function
     * @param stateSupplier the function that returns a state for each invocation of iterator()
     * @param nextSupplier the action called with an Observer API to receive value, not null
     * @return the new Ix instance
     * @throws NullPointerException if stateSupplier or nextSupplier is null
     * @since 1.0
     */
    public static <T, S> Ix<T> generate(Func0<S> stateSupplier, Func2<S, Observer<T>, S> nextSupplier) {
        return generate(stateSupplier, nextSupplier, IxEmptyAction.instance1());
    }

    /**
     * Calls the given function (with per-iterator state) to generate a value or terminate 
     * whenever the next() is called on the resulting Ix.iterator().
     * <p>
     * The result's iterator() doesn't support remove().
     * <p>
     * The action may call {@code onNext} at most once to signal the next value per action invocation.
     * The {@code onCompleted} should be called to indicate no further values will be generated (may be
     * called with an onNext in the same action invocation). Calling {@code onError} will immediately
     * throw the given exception (as is if it's a RuntimeException or Error; or wrapped into a RuntimeException).
     * <p>
     * Note that since there is no direct way to cancel an Iterator, the stateDisposer is only invoked
     * when the nextSupplier calls a terminal method. 
     * @param <T> the value type
     * @param <S> the state type supplied to and returned by the nextSupplier function
     * @param stateSupplier the function that returns a state for each invocation of iterator()
     * @param nextSupplier the action called with an Observer API to receive value, not null
     * @param stateDisposer the action called when the nextSupplier signals an {@code onError} or {@code onCompleted}.
     * @return the new Ix instance
     * @throws NullPointerException if stateSupplier, nextSupplier or stateDisposer is null
     * @since 1.0
     */
    public static <T, S> Ix<T> generate(Func0<S> stateSupplier, Func2<S, Observer<T>, S> nextSupplier, Action1<? super S> stateDisposer) {
        return new IxGenerate<T, S>(nullCheck(stateSupplier, "stateSupplier is null"), 
                nullCheck(nextSupplier, "nextSupplier is null"), nullCheck(stateDisposer, "stateDisposer is null"));
    }

    /**
     * Emits a single constant value.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <T> the value type
     * @param value the constant value to emit
     * @return the new Ix instance
     * @since 1.0
     */
    public static <T> Ix<T> just(T value) {
        return new IxJust<T>(value);
    }

    /**
     * Concatenates the elements of Iterable sources, provided as an Iterable itself, sequentially.
     * <p>
     * The result's iterator() forwards the remove() calls to the current iterator.
     * <p>
     * Note that merge and concat operations are the same in the Iterable world.
     * @param <T> the common base type
     * @param sources the Iterable sequence of source Iterables
     * @return the new Ix instance
     * @throws NullPointerException if sources is null
     * @since 1.0
     * @see #concat(Iterable)
     */
    public static <T> Ix<T> merge(Iterable<? extends Iterable<? extends T>> sources) {
        return concat(sources);
    }
    
    /**
     * Concatenates the elements of Iterable sources, provided as an array, sequentially.
     * <p>
     * The result's iterator() forwards the remove() calls to the current iterator.
     * <p>
     * Note that merge and concat operations are the same in the Iterable world.
     * @param <T> the common base type
     * @param sources the array of source Iterables
     * @return the new Ix instance
     * @throws NullPointerException if sources is null
     * @since 1.0
     * @see #concatArray(Iterable...)
     */
    public static <T> Ix<T> mergeArray(Iterable<? extends T>... sources) {
        return concatArray(sources); // concat and merge are the same in the Iterable world
    }
    
    /**
     * Emits a range of incrementing integer values, starting from {@code start} and
     * up to {@code count} times.
     * @param start the starting value
     * @param count the number of integers to emit, non-negative
     * @return the new Ix instance
     * @throws IllegalArgumentException if count is negative
     * @since 1.0
     */
    public static Ix<Integer> range(int start, int count) {
        if (count == 0) {
            return empty();
        }
        if (count == 1) {
            return just(start);
        }
        if (count < 0) {
            throw new IllegalArgumentException("count >= 0 required but it was " + count);
        }
        return new IxRange(start, count);
    }

    /**
     * Repeats the given value indefinitely.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <T> the value type
     * @param value the value to emit whenever next() is called
     * @return the new Ix instance
     * @since 1.0
     */
    public static <T> Ix<T> repeatValue(T value) {
        return new IxRepeat<T>(value);
    }

    /**
     * Repeats the given value at most count times.
     * <p>A count of zero will yield an empty sequence, a count of one
     * will yield a sequence with only one element and so forth.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <T> the value type
     * @param value the value to emit
     * @param count the number of times to emit the value, non-negative
     * @return the new Ix instance
     * @throws IllegalArgumentException if count is negative
     * @since 1.0
     */
    public static <T> Ix<T> repeatValue(T value, long count) {
        return new IxRepeatCount<T>(value, nonNegative(count, "count"));
    }

    /**
     * Repeats the given value until the given predicate returns true.
     * <p>A count of zero will yield an empty sequence, a count of one
     * will yield a sequence with only one element and so forth.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <T> the value type
     * @param value the value to emit
     * @param stopPredicate the predicate called before any emission; returning
     * false keeps repeating the value, returning true terminates the sequence
     * @return the new Ix instance
     * @throws NullPointerException if stopPredicate is null
     * @since 1.0
     */
    public static <T> Ix<T> repeatValue(T value, Pred0 stopPredicate) {
        return repeatValue(value, Long.MAX_VALUE, stopPredicate);
    }

    /**
     * Repeats the given value at most count times or until the given predicate returns true.
     * <p>A count of zero will yield an empty sequence, a count of one
     * will yield a sequence with only one element and so forth.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <T> the value type
     * @param value the value to emit
     * @param count the number of times to emit the value, non-negative
     * @param stopPredicate the predicate called before any emission; returning
     * false keeps repeating the value, returning true terminates the sequence
     * @return the new Ix instance
     * @throws IllegalArgumentException if count is negative
     * @throws NullPointerException if stopPredicate is null
     * @since 1.0
     */
    public static <T> Ix<T> repeatValue(T value, long count, Pred0 stopPredicate) {
        return new IxRepeatPredicate<T>(value, nonNegative(count, "count"), nullCheck(stopPredicate, "stopPredicate is null"));
    }
    
    /**
     * Emits a sequence of substring of a string split by the given separator.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param string the string to split, not null
     * @param by the separator to split along, not null
     * @return the new Ix instance
     * @throws NullPointerException if string or by is null
     * @since 1.0
     */
    public static Ix<String> split(String string, String by) {
        return new IxSplit(nullCheck(string, "string is null"), nullCheck(by, "by is null"));
    }
    
    /**
     * Combines the next element from each source Iterable via a zipper function.
     * <p>
     * If one of the source Iterables is sorter the sequence terminates eagerly.
     * <p>
     * The result's iterator() doesn't support remove().
     * 
     * @param <T> the common element type of the sources
     * @param <R> the result value type
     * @param sources the array of Iterable sources, not null
     * @param zipper the function that takes an array of values and returns a value
     * to be emitted, one from each source, not null
     * @return the new Ix instance
     * @throws NullPointerException if sources or zipper is null
     * @since 1.0
     */
    public static <T, R> Ix<R> zip(Iterable<? extends T>[] sources, FuncN<R> zipper) {
        return new IxZipArray<T, R>(nullCheck(sources, "sources is null"), nullCheck(zipper, "zipper is null"));
    }

    /**
     * Combines the next element from each source Iterable, provided as an Iterable itself, 
     * via a zipper function.
     * <p>
     * If one of the source Iterables is sorter the sequence terminates eagerly.
     * <p>
     * The result's iterator() doesn't support remove().
     * 
     * @param <T> the common element type of the sources
     * @param <R> the result value type
     * @param sources the Iterable of Iterable sources, not null
     * @param zipper the function that takes an array of values and returns a value
     * to be emitted, one from each source, not null
     * @return the new Ix instance
     * @throws NullPointerException if sources or zipper is null
     * @since 1.0
     */
    public static <T, R> Ix<R> zip(Iterable<? extends Iterable<? extends T>> sources, FuncN<R> zipper) {
        return new IxZipIterable<T, R>(nullCheck(sources, "sources is null"), nullCheck(zipper, "zipper is null"));
    }

    /**
     * Combines the next element from each source Iterable, provided as an Iterable itself, 
     * via a zipper function.
     * <p>
     * If one of the source Iterables is sorter the sequence terminates eagerly.
     * <p>
     * The result's iterator() doesn't support remove().
     * 
     * @param <T1> the first source's element type
     * @param <T2> the second source's element type
     * @param <R> the result value type
     * @param source1 the first source Iterable
     * @param source2 the second source Iterable
     * @param zipper the function that takesone from each source, not null
     * @return the new Ix instance
     * @throws NullPointerException if any of the sources or zipper is null
     * @since 1.0
     */
    public static <T1, T2, R> Ix<R> zip(
            Iterable<T1> source1, Iterable<T2> source2, 
            Func2<? super T1, ? super T2, ? extends R> zipper) {
        return new IxZip2<T1, T2, R>(nullCheck(source1, "source1 is null"), 
                nullCheck(source2, "source2 is null"), nullCheck(zipper, "zipper is null"));
    }

    /**
     * Combines the next element from each source Iterable, provided as an Iterable itself, 
     * via a zipper function.
     * <p>
     * If one of the source Iterables is sorter the sequence terminates eagerly.
     * <p>
     * The result's iterator() doesn't support remove().
     * 
     * @param <T1> the first source's element type
     * @param <T2> the second source's element type
     * @param <T3> the third source's element type
     * @param <R> the result value type
     * @param source1 the first source Iterable
     * @param source2 the second source Iterable
     * @param source3 the third source Iterable
     * @param zipper the function that takesone from each source, not null
     * @return the new Ix instance
     * @throws NullPointerException if any of the sources or zipper is null
     * @since 1.0
     */
    public static <T1, T2, T3, R> Ix<R> zip(
            Iterable<T1> source1, Iterable<T2> source2, 
            Iterable<T3> source3,
            Func3<? super T1, ? super T2, ? super T3, ? extends R> zipper) {
        return new IxZip3<T1, T2, T3, R>(nullCheck(source1, "source1 is null"), nullCheck(source2, "source2 is null"), 
                nullCheck(source3, "source3 is null"), nullCheck(zipper, "zipper is null"));
    }

    /**
     * Combines the next element from each source Iterable, provided as an Iterable itself, 
     * via a zipper function.
     * <p>
     * If one of the source Iterables is sorter the sequence terminates eagerly.
     * <p>
     * The result's iterator() doesn't support remove().
     * 
     * @param <T1> the first source's element type
     * @param <T2> the second source's element type
     * @param <T3> the third source's element type
     * @param <T4> the fourth source's element type
     * @param <R> the result value type
     * @param source1 the first source Iterable
     * @param source2 the second source Iterable
     * @param source3 the third source Iterable
     * @param source4 the fourth source Iterable
     * @param zipper the function that takesone from each source, not null
     * @return the new Ix instance
     * @throws NullPointerException if any of the sources or zipper is null
     * @since 1.0
     */
    public static <T1, T2, T3, T4, R> Ix<R> zip(
            Iterable<T1> source1, Iterable<T2> source2, 
            Iterable<T3> source3, Iterable<T4> source4,
            Func4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> zipper) {
        return new IxZip4<T1, T2, T3, T4, R>(nullCheck(source1, "source1 is null"), 
                nullCheck(source2, "source2 is null"), nullCheck(source3, "source3 is null"), 
                nullCheck(source4, "source4 is null"), nullCheck(zipper, "zipper is null"));
    }

    //---------------------------------------------------------------------------------------
    // Instance operators
    //---------------------------------------------------------------------------------------
    
    /**
     * Emits true if all elements of this sequence match a given predicate (including empty).
     * <p>
     * The result's iterator() doesn't support remove().
     * @param predicate the predicate receiving each element
     * @return the new Ix instance
     * @throws NullPointerException if predicate is null
     * @since 1.0 
     */
    public final Ix<Boolean> all(Pred<? super T> predicate) {
        return new IxAll<T>(this, nullCheck(predicate, "predicate is null"));
    }
    
    /**
     * Emits true if any element of this sequence matches the given predicate,
     * false otherwise (or for empty sequences).
     * <p>
     * The result's iterator() doesn't support remove().
     * @param predicate the predicate receiving each element
     * @return the new Ix instance
     * @throws NullPointerException if predicate is null
     * @since 1.0
     */
    public final Ix<Boolean> any(Pred<? super T> predicate) {
        return new IxAny<T>(this, nullCheck(predicate, "predicate is null"));
    }

    /**
     * Calls the given transformers with this and returns its value allowing
     * fluent conversions to non-Ix types.
     * @param <R> the result type
     * @param transformer the function receiving this Ix instance and returns a value
     * @return the value returned by the transformer function
     * @throws NullPointerException if transformer is null
     * @since 1.0
     */
    public final <R> R as(Func1<? super Ix<T>, R> transformer) {
        return transformer.call(this);
    }
    
    /**
     * Calculates the float-based average of this sequence of numbers.
     * <p>The returned sequence is empty if this sequence is empty.
     * <p>This operator force-casts this sequence which may lead
     * to ClassCastException if any of this sequence's elements is not
     * a subclass of Number.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public final Ix<Float> averageFloat() {
        return new IxAverageFloat((Iterable<Number>)this);
    }

    /**
     * Calculates the double-based average of this sequence of numbers.
     * <p>The returned sequence is empty if this sequence is empty.
     * <p>This operator force-casts this sequence which may lead
     * to ClassCastException if any of this sequence's elements is not
     * a subclass of Number.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public final Ix<Double> averageDouble() {
        return new IxAverageDouble((Iterable<Number>)this);
    }
    
    /**
     * Buffers the subsequent {@code size} elements into a sequence of
     * non-overlapping Lists.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param size the number of elements to group together, positive
     * @return the new Ix instance
     * @throws IllegalArgumentException if size is non-positive
     * @since 1.0
     */
    public final Ix<List<T>> buffer(int size) {
        return new IxBuffer<T>(this, positive(size, "size"));
    }
    
    /**
     * Buffers the subsequent {@code size} elemeints into a sequence of
     * potentially overlapping Lists.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param size the number of elements to group together, positive
     * @param skip specifies how often to start a new list
     * @return the new Ix instance
     * @throws IllegalArgumentException if size or skip is non-positive
     * @since 1.0
     */
    public final Ix<List<T>> buffer(int size, int skip) {
        if (size == skip) {
            return buffer(size);
        }
        if (size < skip) {
            return new IxBufferSkip<T>(this, positive(size, "size"), positive(skip, "skip"));
        }
        return new IxBufferOverlap<T>(this, positive(size, "size"), positive(skip, "skip"));
    }
    
    /**
     * Collect the elements into a collection via collector action and emit that collection
     * as a single item.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <C> the collection type
     * @param initialFactory the function returning a collection for each iterator() call
     * @param collector the action called with the collection and the current item
     * @return the new Ix instance
     * @throws NullPointerException if initialFactory or collector is null
     * @since 1.0
     */
    public final <C> Ix<C> collect(Func0<C> initialFactory, Action2<C, T> collector) {
        return new IxCollect<T, C>(this, nullCheck(initialFactory, "initalFactory is null"), nullCheck(collector, "collector"));
    }

    /**
     * When the iterator() of the returned Ix is called, it calls the transformer function
     * with this Ix instance and emits the elements of the returned Iterable.
     * <p>
     * The result's iterator() forwards the call remove() to the returned Iterable's Iterator.
     * 
     * @param <R> the result value type
     * @param transformer the transformer called with this Ix when Ix.iterator() is invoked
     * @return the new Ix instance
     * @throws NullPointerException if transformer is null
     * @since 1.0
     */
    public final <R> Ix<R> compose(Func1<? super Ix<T>, ? extends Iterable<? extends R>> transformer) {
        return new IxCompose<T, R>(this, nullCheck(transformer, "transformer is null"));
    }
    
    /**
     * Maps each element from this sequence into subsequent Iterable sequences whose elmenents are
     * concatenated in order.
     * <p>
     * Note that flatMap and concatMap operations are the same in the Iterable world.
     * <p>
     * The result's iterator() forwards the call remove() to the current inner Iterator.
     * 
     * @param <R> the result value type
     * @param mapper the function 
     * @return the new Ix instance
     * @throws NullPointerException if mapper is null
     * @since 1.0
     * @see #flatMap(Func1)
     */
    public final <R> Ix<R> concatMap(Func1<? super T, ? extends Iterable<? extends R>> mapper) {
        return new IxFlattenIterable<T, R>(this, nullCheck(mapper, "mapper is null"));
    }
    
    /**
     * Emits elements of this sequence followed by the elements of the other sequence.
     * <p>
     * The result's iterator() forwards the call remove() to the current Iterator.
     * @param other the other sequence to emits elements of
     * @return the new Ix instance
     * @throws NullPointerException if other is null
     * @since 1.0
     */
    public final Ix<T> concatWith(Iterable<? extends T> other) {
        return concat(this, other);
    }
    
    /**
     * Emits true if the sequence contains the given Object, compared via null-safe
     * equals.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param o the value to find
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<Boolean> contains(Object o) {
        return new IxContains<T>(this, o);
    }
    
    /**
     * Emits the number of elements in this sequence.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the number of elements in this sequence
     * @since 1.0
     */
    public final Ix<Integer> count() {
        return new IxCount<T>(this);
    }

    /**
     * Emits the number of elements, as a long, in this sequence.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the number of elements in this sequence
     * @since 1.0
     */
    public final Ix<Long> countLong() {
        return new IxCountLong<T>(this);
    }

    /**
     * Emits the given value if this sequence is empty, streams this sequence otherwise.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param value the value to emit if this sequence is empty
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<T> defaultIfEmpty(T value) {
        return switchIfEmpty(Ix.just(value));
    }
    
    /**
     * Emits only distinct, never before seen elements (according to null-safe equals())
     * of this sequence.
     * <p>
     * Note that this operator uses a memory of seen elements which may grow unbounded
     * with long sequences.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<T> distinct() {
        return distinct(IdentityHelper.instance());
    }

    /**
     * Emits only distinct, never before seen keys extracted from elements 
     * (according to null-safe equals()) of this sequence.
     * <p>
     * Note that this operator uses a memory of seen elements which may grow unbounded
     * with long sequences.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <K> the key type
     * @param keySelector the function taking the current element and returning a key object
     * that will be compared with null-safe equals().
     * @return the new Ix instance
     * @throws NullPointerException if keySelector is null
     * @since 1.0
     */
    public final <K> Ix<T> distinct(Func1<? super T, K> keySelector) {
        return new IxDistinct<T, K>(this, nullCheck(keySelector, "keySelector is null"));
    }

    /**
     * Emits elements from this sequence if each element is different from the previous element
     * (according to a null-safe equals()), dropping elements that evaluate to the same as the previous.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<T> distinctUntilChanged() {
        return distinctUntilChanged(IdentityHelper.instance());
    }

    /**
     * Emits elements from this sequence if each element is different from the previous element
     * (according to a comparer), dropping elements that evaluate to the same as the previous.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param comparer the predicate receiving the previous element and the current element and
     * returns true if they are the same (thus ignoring the latter).
     * @return the new Ix instance
     * @throws NullPointerException if comparer is null
     * @since 1.0
     */
    public final Ix<T> distinctUntilChanged(Pred2<? super T, ? super T> comparer) {
        return new IxDistinctUntilChanged<T, T>(this, IdentityHelper.<T>instance(), nullCheck(comparer, "comparer is null"));
    }

    /**
     * Emits elements from this sequence if each element is different from the previous element
     * (according to a null-safe equals() of the extracted key), dropping elements that evaluate 
     * to the same as the previous.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <K> the key type
     * @param keySelector the function that receives the current element and returns a key that will be compared
     * via null-safe equals with the previous element's key
     * @return the new Ix instance
     * @throws NullPointerException if comparer is null
     * @since 1.0
     */
    public final <K> Ix<T> distinctUntilChanged(Func1<? super T, K> keySelector) {
        return new IxDistinctUntilChanged<T, K>(this, keySelector, EqualityHelper.INSTANCE);
    }
    
    /**
     * Calls the given action just before when the consumer calls next() of this Ix.iterator().
     * <p>
     * The result's iterator() forwards calls to remove() to this Iterator.
     * @param action the action to call for each item
     * @return the new Ix instance
     * @throws NullPointerException if action is null
     * @since 1.0
     */
    public final Ix<T> doOnNext(Action1<? super T> action) {
        return new IxDoOn<T>(this, nullCheck(action, "action is null"), IxEmptyAction.instance0());
    }

    /**
     * Calls the given action after consumption of this sequence has completed, i.e., when
     * hasNext() of this Ix.iterator() returns false.
     * <p>
     * The result's iterator() forwards calls to remove() to this' Iterator.
     * @param action the action to call after the source sequence completes
     * @return the new Ix instance
     * @throws NullPointerException if action is null
     * @since 1.0
     */
    public final Ix<T> doOnCompleted(Action0 action) {
        return new IxDoOn<T>(this, IxEmptyAction.instance1(), nullCheck(action, "action is null"));
    }

    /**
     * Emits the elements of this sequence followed by the elements of the given array of values.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param values the elements to emit after this sequence
     * @return the new Ix instance
     * @throws NullPointerException if values is null
     * @since 1.0
     */
    public final Ix<T> endWith(T... values) {
        return concat(this, fromArray(values));
    }
    
    /**
     * Emits distinct elements from this and the other Iterable which are not 
     * in the other sequence (i.e., (A union B) minus (A intersection B)).
     * <p>
     * The result's iterator() doesn't support remove().
     * @param other the other Iterable sequence, not null
     * @return the new Ix instance
     * @throws NullPointerException if other is null
     * @since 1.0
     */
    public final Ix<T> except(Iterable<? extends T> other) {
        return new IxExcept<T>(this, nullCheck(other, "other is null"));
    }
    
    /**
     * Emits elements of this sequence which match the given predicate only.
     * <p>
     * The result's iterator() forwards the call to remove() to this' Iterator.
     * @param predicate the predicate receiving the current element and if it
     * returns true, the value is emitted, ingored otherwise.
     * @return  the new Ix instance
     * @throws NullPointerException if predicate is null
     * @since 1.0
     */
    public final Ix<T> filter(Pred<T> predicate) {
        return new IxFilter<T>(this, nullCheck(predicate, "predicate is null"));
    }
    
    /**
     * Maps each element from this sequence into subsequent Iterable sequences whose elmenents are
     * concatenated in order.
     * <p>
     * Note that flatMap and concatMap operations are the same in the Iterable world.
     * <p>
     * The result's iterator() forwards the call remove() to the current inner Iterator.
     * 
     * @param <R> the result value type
     * @param mapper the function 
     * @return the new Ix instance
     * @throws NullPointerException if mapper is null
     * @since 1.0
     * @see #concatMap(Func1)
     */
    public final <R> Ix<R> flatMap(Func1<? super T, ? extends Iterable<? extends R>> mapper) {
        return new IxFlattenIterable<T, R>(this, mapper);
    }
    
    /**
     * Groups elements of this sequence into distinct groups keyed by the keys returned by the keySelector.
     * <p>
     * The operator doesn't lose data and calling hasNext/next on either the returned Ix or on the inner
     * GroupedIx can move the source sequence forward.
     * <p>
     * The result's iterator() and the inner groups' Iterators don't support remove().
     * @param <K> the key type
     * @param keySelector the function receiving the current element and returns the key to be used for
     * grouping the values into the same inner GroupedIx.
     * @return the new Ix instance
     * @throws NullPointerException if keySelector is null
     * @since 1.0
     * @see #groupBy(Func1, Func1)
     */
    public final <K> Ix<GroupedIx<K, T>> groupBy(Func1<? super T, ? extends K> keySelector) {
        return groupBy(keySelector, IdentityHelper.<T>instance());
    }

    /**
     * Groups mapped elements (by the valueSelector) of this sequence into distinct groups 
     * keyed by the keys returned by the keySelector.
     * <p>
     * The operator doesn't lose data and calling hasNext/next on either the returned Ix or on the inner
     * GroupedIx can move the source sequence forward.
     * <p>
     * The result's iterator() and the inner groups' Iterators don't support remove().
     * @param <K> the key type
     * @param <V> the value type
     * @param keySelector the function receiving the current element and returns the key to be used for
     * grouping the values into the same inner GroupedIx.
     * @param valueSelector the function receiving the current element and returns the value to be emitted
     * by the appropriate group 
     * @return the new Ix instance
     * @throws NullPointerException if keySelector or valueSelector is null
     * @since 1.0
     * @see #groupBy(Func1, Func1)
     */
    public final <K, V> Ix<GroupedIx<K, V>> groupBy(Func1<? super T, ? extends K> keySelector,
            Func1<? super T, ? extends V> valueSelector) {
        return new IxGroupBy<T, K, V>(this, nullCheck(keySelector, "keySelector is null"), nullCheck(valueSelector, "valueSelector is null"));
    }

    /**
     * Emits true if this sequence has elements, emits false otherwise.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<Boolean> hasElements() {
        return new IxHasElements<T>(this);
    }
    
    /**
     * Hides the identity of this Ix instance and prevents certain identity-based optimiziations.
     * <p>
     * The result's iterator() forwards the remove() calls to this' Iterator.
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<T> hide() {
        return new IxWrapper<T>(this);
    }
    
    /**
     * Emits distinct values of this and the other Iterables that are present in
     * both sequences.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param other the other Iterable sequence
     * @return the new Ix instance
     * @throws NullPointerException if other is null
     * @since 1.0
     */
    public final Ix<T> intersect(Iterable<? extends T> other) {
        return new IxIntersect<T>(this, nullCheck(other, "other is null"));
    }
    
    /**
     * Runs through this sequence, ignoring all values until this sequence completes.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<T> ignoreElements() {
        return new IxIgnoreElements<T>(this);
    }
    
    /**
     * Converts elements of this sequence to String and concatenates them into
     * a single, comma separated String.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<String> join() {
        return join(", ");
    }
    
    /**
     * Converts elements of this sequence to String and concatenates them into
     * a single String separated by the given character sequence.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param separator the character sequence separating elements
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<String> join(CharSequence separator) {
        return new IxJoin<T>(this, separator);
    }
    
    /**
     * Calls the given lifter function with the iterator of this sequence and emits
     * elements of the returned Iterator.
     * <p>
     * The result's iterator() forwards the remove() calls to the returned Iterator.
     * @param <R> the result value type
     * @param lifter the function that receives the Iterator of this and returns an Iterator
     * that will be consumed further on
     * @return the new Ix instance
     * @throws NullPointerException if lifter is null
     * @since 1.0
     */
    public final <R> Ix<R> lift(Func1<? super Iterator<T>, ? extends Iterator<R>> lifter) {
        return new IxLift<T, R>(this, nullCheck(lifter, "lifter is null"));
    }
    
    /**
     * Maps each element of this sequence to some other value.
     * <p>
     * The result's iterator() forwards the remove() calls to this' Iterator.
     * @param <R> the result value type
     * @param mapper the function that receives an element from this sequence
     * and returns another value for it to be emitted.
     * @return the new Ix instance
     * @throws NullPointerException if mapper is null
     * @since 1.0
     */
    public final <R> Ix<R> map(Func1<? super T, ? extends R> mapper) {
        return new IxMap<T, R>(this, mapper);
    }
    
    public final Ix<T> max(Comparator<? super T> comparator) {
        return new IxMinMax<T>(this, comparator, -1);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final Ix<T> max() {
        return max((Comparator)SelfComparator.INSTANCE);
    }
    
    @SuppressWarnings("unchecked")
    public final Ix<Integer> maxInt() {
        return new IxMaxInt((Ix<Integer>)this);
    }

    @SuppressWarnings("unchecked")
    public final Ix<Long> maxLong() {
        return new IxMaxLong((Ix<Long>)this);
    }

    public final Ix<T> mergeWith(Iterable<? extends T> other) {
        return concatWith(other);
    }

    public final Ix<T> min(Comparator<? super T> comparator) {
        return new IxMinMax<T>(this, comparator, 1);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final Ix<T> min() {
        return min((Comparator)SelfComparator.INSTANCE);
    }

    @SuppressWarnings("unchecked")
    public final Ix<Integer> minInt() {
        return new IxMinInt((Ix<Integer>)this);
    }

    @SuppressWarnings("unchecked")
    public final Ix<Long> minLong() {
        return new IxMinLong((Ix<Long>)this);
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

    public final Ix<T> publish() {
        return new IxPublish<T>(this);
    }

    public final <R> Ix<R> publish(Func1<? super Ix<T>, ? extends Iterable<? extends R>> transform) {
        return new IxPublishSelector<T, R>(this, transform);
    }
    
    public final Ix<T> reduce(Func2<T, T, T> reducer) {
        return new IxAggregate<T>(this, reducer);
    }
    
    public final <C> Ix<C> reduce(Func0<C> initialFactory, Func2<C, T, C> reducer) {
        return new IxReduce<T, C>(this, initialFactory, reducer);
    }
    
    public final Ix<T> remove(Pred<? super T> predicate) {
        return new IxRemove<T>(this, predicate);
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

    public final Ix<T> retain(Pred<? super T> predicate) {
        return new IxRetain<T>(this, predicate);
    }
    
    public final Ix<T> reverse() {
        return new IxReverse<T>(this);
    }

    public final Ix<T> scan(Func2<T, T, T> scanner) {
        return new IxScan<T>(this, scanner);
    }

    public final <R> Ix<R> scan(Func0<R> initialFactory, Func2<R, T, R> scanner) {
        return new IxScanSeed<T, R>(this, initialFactory, scanner);
    }
    
    public final Ix<Boolean> sequenceEqual(Iterable<? extends T> other) {
        return sequenceEqual(other, EqualityHelper.INSTANCE);
    }

    public final Ix<Boolean> sequenceEqual(Iterable<? extends T> other, Pred2<? super T, ? super T> comparer) {
        return new IxSequenceEqual<T>(this, other, comparer);
    }

    public final Ix<T> skip(int n) {
        if (n == 0) {
            return this;
        }
        return new IxSkip<T>(this, n);
    }

    public final Ix<T> skipLast(int n) {
        if (n == 0) {
            return this;
        }
        return new IxSkipLast<T>(this, n);
    }

    public final Ix<T> skipWhile(Pred<? super T> predicate) {
        return new IxSkipWhile<T>(this, predicate);
    }
    
    @SuppressWarnings("unchecked")
    public final Ix<T> startWith(T... values) {
        return concatArray(fromArray(values), this);
    }

    @SuppressWarnings("unchecked")
    public final Ix<Integer> sumInt() {
        return new IxSumInt((Ix<Integer>)this);
    }

    @SuppressWarnings("unchecked")
    public final Ix<Long> sumLong() {
        return new IxSumLong((Ix<Long>)this);
    }
    
    public final Ix<T> switchIfEmpty(Iterable<? extends T> other) {
        return new IxSwitchIfEmpty<T>(this, other);
    }

    public final Ix<T> take(int n) {
        return new IxTake<T>(this, n);
    }
    
    public final Ix<T> takeLast(int n) {
        return new IxTakeLast<T>(this, n);
    }
    
    public final Ix<T> takeUntil(Pred<? super T> stopPredicate) {
        return new IxTakeUntil<T>(this, stopPredicate);
    }
    
    public final Ix<T> takeWhile(Pred<? super T> predicate) {
        return new IxTakeWhile<T>(this, predicate);
    }
    
    public final Ix<Object[]> toArray() {
        return collect(ToListHelper.<T>initialFactory(), ToListHelper.<T>collector())
                .map(ToListHelper.<T>toArray());
    }
    
    public final Ix<List<T>> toList() {
        return collect(ToListHelper.<T>initialFactory(), ToListHelper.<T>collector());
    }
    
    @SuppressWarnings("unchecked")
    public final Ix<Long> toLong() {
        return ((Ix<Number>)this).map(NumberToLongHelper.INSTANCE);
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
    
    public final Ix<Set<T>> toSet() {
        return new IxToSet<T>(this);
    }

    public final <R> Ix<R> transform(IxTransform<T, R> transformer) {
        return new IxTransformer<T, R>(this, transformer);
    }
    
    public final Ix<T> union(Iterable<? extends T> other) {
        return new IxUnion<T>(this, other);
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
    
    public final <U, R> Ix<R> zipWith(Iterable<U> other, Func2<? super T, ? super U, ? extends R> zipper) {
        return zip(this, other, zipper);
    }

    //---------------------------------------------------------------------------------------
    // Leaving the Iterable world
    //---------------------------------------------------------------------------------------

    /**
     * Returns the first element of this sequence.
     * @return the first element
     * @throws NoSuchElementException if this sequence is empty
     * @since 1.0
     * @see #first(Object)
     * @see #last(Object)
     */
    @SuppressWarnings("unchecked")
    public final T first() {
        if (this instanceof Callable) {
            return checkedCall((Callable<T>) this);
        }
        return iterator().next();
    }

    /**
     * Returns the first element of this sequence or the defaultValue
     * if this sequence is empty.
     * @param defaultValue the value to return if this sequence is empty
     * @return the first element or the default value
     * @since 1.0
     * @see #first()
     * @see #last()
     */
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
    
    /**
     * Consumes the entire sequence and calls the given action with each value.
     * @param action the action to call
     * @throws NullPointerException if action is null
     * @since 1.0
     * @see #foreachWhile(Pred)
     */
    public final void foreach(Action1<? super T> action) {
        for (T t : this) {
            action.call(t);
        }
    }

    /**
     * Consumes the entire sequence and calls the given predicate with each value;
     * which can stop the iteration by returning false.
     * @param predicate the predicate to call with the current element and should
     * return true to continue the loop or false to quit the loop.
     * @throws NullPointerException if action is null
     * @since 1.0
     * @see #foreach(Pred)
     */
    public final void foreachWhile(Pred<? super T> predicate) {
        for (T t : this) {
            if (!predicate.test(t)) {
                break;
            }
        }
    }

    /**
     * Consumes the entire sequence and adds each element into the given collection
     * that is also returned.
     * @param <U> the collection of type accepting a (super)type of this element type
     * @param collection the collection to collect into
     * @return the collection itself
     * @throws NullPointerException if collection is null
     * @since 1.0
     */
    public final <U extends Collection<? super T>> U into(U collection) {
        for (T v : this) {
            collection.add(v);
        }
        return collection;
    }

    /**
     * Returns the last element of this sequence.
     * @return the last element of this sequence
     * @throws NoSuchElementException if the sequence is empty
     * @since 1.0
     * @see #last(Object)
     */
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
    
    /**
     * Returns the last element of this sequence or the defaultValue if
     * this sequence is empty.
     * @param defaultValue the value to return if this sequence is empty
     * @return the last element or the default value
     * @since 1.0
     * @see #last()
     * @see #first()
     */
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

    /**
     * Prints the elements of this sequence to the console, separated
     * by a comma+space and with a line break after roughly 80 characters.
     * @since 1.0
     */
    public final void print() {
        print(", ", 80);
    }

    /**
     * Prints the elements of this sequence to the console, separated
     * by the given separator and with a line break after roughly the
     * given charsPerLine amount.
     * @param separator the characters to separate the elements
     * @param charsPerLine indicates how long a line should be
     */
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

    /**
     * Prints each element of this sequence into a new line on the console.
     * @since 1.0
     */
    public final void println() {
        for (T v : this) {
            System.out.println(v);
        }
    }

    /**
     * Prints each element of this sequence into a new line on the console, prefixed
     * by the given character sequence.
     * @param prefix the prefix before each line
     */
    public final void println(CharSequence prefix) {
        for (T v : this) {
            System.out.print(prefix);
            System.out.println(v);
        }
    }    
    /**
     * Removes all elements by repeatedly calling this sequence's Iterator.remove().
     */
    public final void removeAll() {
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
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
     * Iterates over this instance, dropping all values it produces.
     * @see #subscribe()
     */
    public final void run() {
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            it.next();
        }
    }

    /**
     * Returns the single element of this sequence or throws a NoSuchElementException
     * if this sequence is empty or IndexOutOfBoundsException if this sequence has more
     * than on element
     * @return the single element of the sequence
     * @throws IndexOutOfBoundsException if the sequence has more than one element
     * @throws NoSuchElementException if the sequence is empty
     * @since 1.0
     * @see #single(Object)
     */
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
    

    /**
     * Returns the single element of this sequence, the defaltValue
     * if this sequence is empty or IndexOutOfBoundsException if this sequence has more
     * than on element
     * @param defaultValue the value to return if this sequence is empty
     * @return the single element of the sequence
     * @throws IndexOutOfBoundsException if the sequence has more than one element
     * @since 1.0
     * @see #single(Object)
     */
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

    /**
     * Iterates over this instance, dropping all values it produces.
     * @see #run()
     */
    public final void subscribe() {
        run();
    }

    /**
     * Iterates over this sequence and calls the given onNext action with
     * each element.
     * @param onNext the consumer to call with each element
     * @throws NullPointerException if consumer is null
     * @since 1.0
     */
    public final void subscribe(Action1<? super T> onNext) {
        for (T v : this) {
            onNext.call(v);
        }
    }

    /**
     * Iterates over this sequence and calls the given onNext action with
     * each element and calls the onError with any exception thrown by the iteration
     * or the onNext action.
     * @param onNext the consumer to call with each element
     * @param onError the consumer to call with the exception thrown 
     * @throws NullPointerException if onError is null
     * @since 1.0
     */
    public final void subscribe(Action1<? super T> onNext, Action1<Throwable> onError) {
        try {
            for (T v : this) {
                onNext.call(v);
            }
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            onError.call(ex);
        }
    }

    /**
     * Iterates over this sequence and calls the given onNext action with
     * each element and calls the onError with any exception thrown by the iteration
     * or the onNext action; otherwise calls the onCompleted action when the sequence completes
     * without exception.
     * @param onNext the consumer to call with each element
     * @param onError the consumer to call with the exception thrown
     * @param onCompleted the action called after the sequence has been consumed 
     * @throws NullPointerException if onError or onCompleted is null
     * @since 1.0
     */
    public final void subscribe(Action1<? super T> onNext, Action1<Throwable> onError, Action0 onCompleted) {
        try {
            for (T v : this) {
                onNext.call(v);
            }
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            onError.call(ex);
            return;
        }
        onCompleted.call();
    }

    /**
     * Consumes this sequence and calls the appropriate onXXX method on the given Observer instance.
     * @param observer the observer to forward values, error or completion to.
     * @throws NullPointerException if observer is null
     * @since 1.0
     */
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
    
    /**
     * Consumes this sequence and calls the appropriate onXXX method on the given Subscriber instance
     * as long as it has not unsubscribed.
     * @param subscriber the subscriber to forward values, error or completion to.
     * @throws NullPointerException if subscriber is null
     * @since 1.0
     */
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
    
    /**
     * Checks if the given value is non-negative and returns it; throws
     * an IllegalArgumentException otherwise
     * @param n the number to check
     * @param name the name of the parameter
     * @return n
     */
    protected static long nonNegative(long n, String name) {
        if (n < 0L) {
            throw new IllegalArgumentException(name + " >= 0 required but it was " + n);
        }
        return n;
    }
    
    /**
     * Checks if the given value is non-negative and returns it; throws
     * an IllegalArgumentException otherwise
     * @param n the number to check
     * @param name the name of the parameter
     * @return n
     */
    protected static int nonNegative(int n, String name) {
        if (n < 0L) {
            throw new IllegalArgumentException(name + " >= 0 required but it was " + n);
        }
        return n;
    }

    /**
     * Checks if the given value is positive and returns it; throws
     * an IllegalArgumentException otherwise
     * @param n the number to check
     * @param name the name of the parameter
     * @return n
     */
    protected static int positive(int n, String name) {
        if (n <= 0L) {
            throw new IllegalArgumentException(name + " > 0 required but it was " + n);
        }
        return n;
    }

}
