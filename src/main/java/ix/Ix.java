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
    public static <T> Ix<T> defer(IxSupplier<? extends Iterable<? extends T>> factory) {
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
     * Wraps the given Iterable source into an Ix instance (if
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
    public static <T, R> Ix<R> forloop(T seed, IxPredicate<? super T> condition,
            IxFunction<? super T, ? extends T> next,
            IxFunction<? super T, ? extends R> selector) {
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
     * @param nextSupplier the action called with an IxEmitter API to receive value, not null
     * @return the new Ix instance
     * @throws NullPointerException if nextSupplier is null
     * @since 1.0
     */
    public static <T> Ix<T> generate(IxConsumer<IxEmitter<T>> nextSupplier) {
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
     * @param nextSupplier the action called with an IxEmitter API to receive value, not null
     * @return the new Ix instance
     * @throws NullPointerException if stateSupplier or nextSupplier is null
     * @since 1.0
     */
    public static <T, S> Ix<T> generate(IxSupplier<S> stateSupplier, IxFunction2<S, IxEmitter<T>, S> nextSupplier) {
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
     * @param nextSupplier the action called with an IxEmitter API to receive value, not null
     * @param stateDisposer the action called when the nextSupplier signals an {@code onError} or {@code onCompleted}.
     * @return the new Ix instance
     * @throws NullPointerException if stateSupplier, nextSupplier or stateDisposer is null
     * @since 1.0
     */
    public static <T, S> Ix<T> generate(IxSupplier<S> stateSupplier, IxFunction2<S, IxEmitter<T>, S> nextSupplier, IxConsumer<? super S> stateDisposer) {
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
     * Merges self-comparable items from an Iterable sequence of Iterable sequences, picking
     * the smallest item from all those inner Iterables until all sources complete.
     * @param <T> the value type
     * @param sources the Iterable sequence of Iterables of self-comparable items
     * @return the new Ix instance
     * @since 1.0
     */
    public static <T extends Comparable<? super T>> Ix<T> orderedMerge(Iterable<? extends Iterable<? extends T>> sources) {
        return orderedMerge(sources, SelfComparator.INSTANCE);
    }

    /**
     * Merges items from an Iterable sequence of Iterable sequences, picking
     * the smallest item (according to a custom comparator) from all those inner
     * Iterables until all sources complete.
     * @param <T> the value type
     * @param sources the Iterable sequence of Iterables
     * @param comparator the comparator to compare items and pick the one that returns negative will be picked
     * @return the new Ix instance
     * @since 1.0
     */
    public static <T> Ix<T> orderedMerge(Iterable<? extends Iterable<? extends T>> sources, Comparator<? super T> comparator) {
        return new IxOrderedMergeIterable<T>(nullCheck(sources, "sources is null"), nullCheck(comparator, "comparator is null"));
    }

    /**
     * Merges self-comparable items from an Iterable sequence of Iterable sequences, picking
     * the smallest item from all those inner Iterables until all sources complete.
     * @param <T> the value type
     * @param sources the Iterable sequence of Iterables of self-comparable items
     * @return the new Ix instance
     * @since 1.0
     */
    public static <T extends Comparable<? super T>> Ix<T> orderedMergeArray(Iterable<? extends T>... sources) {
        return orderedMergeArray(SelfComparator.INSTANCE, sources);
    }

    /**
     * Merges items from an array of Iterable sequences, picking
     * the smallest item (according to a custom comparator) from all those inner
     * Iterables until all sources complete.
     * @param <T> the value type
     * @param sources the Iterable sequence of Iterables
     * @param comparator the comparator to compare items and pick the one that returns negative will be picked
     * @return the new Ix instance
     * @since 1.0
     */
    public static <T> Ix<T> orderedMergeArray(Comparator<? super T> comparator, Iterable<? extends T>... sources) {
        return new IxOrderedMergeArray<T>(nullCheck(sources, "sources is null"), nullCheck(comparator, "comparator is null"));
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
     * Prevents the downstream from calling remove() and throws
     * an UnsupportedOperationException instead.
     * @return the new Ix instance
     * @see #readOnly(boolean)
     * @since 1.0
     */
    public final Ix<T> readOnly() {
        return new IxReadOnly<T>(this, false);
    }

    /**
     * Prevents the downstream from calling remove() by optionally
     * ignoring it or throwing an UnsupportedOperationException.
     * @param silent if true, remove() calls are ignored; if false,
     * remove() calls throw an UnsupportedOperationException
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<T> readOnly(boolean silent) {
        return new IxReadOnly<T>(this, silent);
    }

    /**
     * Repeatedly calls the given callable indefinitely and
     * emits the returned value.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <T> the value type
     * @param callable the callable to call
     * @return the new Ix instance
     * @since 1.0
     */
    public static <T> Ix<T> repeatCallable(Callable<T> callable) {
        return new IxRepeatCallable<T>(nullCheck(callable, "callable is null"));
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
     * <p>
     * A count of zero will yield an empty sequence, a count of one
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
     * <p>
     * A count of zero will yield an empty sequence, a count of one
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
    public static <T> Ix<T> repeatValue(T value, IxBooleanSupplier stopPredicate) {
        return repeatValue(value, Long.MAX_VALUE, stopPredicate);
    }

    /**
     * Repeats the given value at most count times or until the given predicate returns true.
     * <p>
     * A count of zero will yield an empty sequence, a count of one
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
    public static <T> Ix<T> repeatValue(T value, long count, IxBooleanSupplier stopPredicate) {
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
    public static <T, R> Ix<R> zip(Iterable<? extends T>[] sources, IxFunction<? super Object[], R> zipper) {
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
    public static <T, R> Ix<R> zip(Iterable<? extends Iterable<? extends T>> sources, IxFunction<? super Object[], R> zipper) {
        return new IxZipIterable<T, R>(nullCheck(sources, "sources is null"), nullCheck(zipper, "zipper is null"));
    }

    /**
     * Combines the next element from each source Iterable via a zipper function.
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
     * @param zipper the function that takes one from each source, not null
     * @return the new Ix instance
     * @throws NullPointerException if any of the sources or zipper is null
     * @since 1.0
     */
    public static <T1, T2, R> Ix<R> zip(
            Iterable<T1> source1, Iterable<T2> source2,
            IxFunction2<? super T1, ? super T2, ? extends R> zipper) {
        return new IxZip2<T1, T2, R>(nullCheck(source1, "source1 is null"),
                nullCheck(source2, "source2 is null"), nullCheck(zipper, "zipper is null"));
    }

    /**
     * Combines the next element from each source Iterable via a zipper function.
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
     * @param zipper the function that takes one from each source, not null
     * @return the new Ix instance
     * @throws NullPointerException if any of the sources or zipper is null
     * @since 1.0
     */
    public static <T1, T2, T3, R> Ix<R> zip(
            Iterable<T1> source1, Iterable<T2> source2,
            Iterable<T3> source3,
            IxFunction3<? super T1, ? super T2, ? super T3, ? extends R> zipper) {
        return new IxZip3<T1, T2, T3, R>(nullCheck(source1, "source1 is null"), nullCheck(source2, "source2 is null"),
                nullCheck(source3, "source3 is null"), nullCheck(zipper, "zipper is null"));
    }

    /**
     * Combines the next element from each source Iterable via a zipper function.
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
     * @param zipper the function that takes one from each source, not null
     * @return the new Ix instance
     * @throws NullPointerException if any of the sources or zipper is null
     * @since 1.0
     */
    public static <T1, T2, T3, T4, R> Ix<R> zip(
            Iterable<T1> source1, Iterable<T2> source2,
            Iterable<T3> source3, Iterable<T4> source4,
            IxFunction4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> zipper) {
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
    public final Ix<Boolean> all(IxPredicate<? super T> predicate) {
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
    public final Ix<Boolean> any(IxPredicate<? super T> predicate) {
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
    public final <R> R as(IxFunction<? super Ix<T>, R> transformer) {
        return transformer.apply(this);
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
     * Buffers the subsequent {@code size} elements into a sequence of
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
     * Buffer until an item is encountered for which the predicate returns true,
     * triggering a new buffer.
     * <p>Neither the previous nor the next buffer will contain the item that caused the
     * split
     * @param predicate the predicate called with each item and should return false
     * to trigger a new buffer
     * @return the new Ix instance
     * @see #bufferUntil(IxPredicate)
     * @see #bufferWhile(IxPredicate)
     * @since 1.0
     */
    public final Ix<List<T>> bufferSplit(IxPredicate<? super T> predicate) {
        return new IxBufferSplit<T>(this, nullCheck(predicate, "predicate is null"));
    }

    /**
     * Buffer until an item is encountered after which the predicate returns true
     * to start a new buffer.
     * <p>The item will be part of the previous buffer.
     * @param predicate the predicate called with each item after the item
     * has been added to the current buffer and should return true to start a new buffer
     * @return the new Ix instance
     * @see #bufferSplit(IxPredicate)
     * @see #bufferWhile(IxPredicate)
     * @since 1.0
     */
    public final Ix<List<T>> bufferUntil(IxPredicate<? super T> predicate) {
        return new IxBufferUntil<T>(this, nullCheck(predicate, "predicate is null"));
    }

    /**
     * Buffer while an item is encountered before which the predicate returns false
     * to start a new buffer.
     * <p>The item will be part of the next buffer
     * @param predicate the predicate called with each item after the item
     * has been added to the current buffer and should return true to start a new buffer
     * @return the new Ix instance
     * @see #bufferSplit(IxPredicate)
     * @see #bufferUntil(IxPredicate)
     * @since 1.0
     */
    public final Ix<List<T>> bufferWhile(IxPredicate<? super T> predicate) {
        return new IxBufferWhile<T>(this, nullCheck(predicate, "predicate is null"));
    }

    /**
     * Cast the elements to the specified class.
     * <p>
     * Note that this is a forced cast on this Ix instance and if
     * not compatible, a ClassCastException might be thrown downstream.
     * @param <R> the target type
     * @param clazz the type token to capture the target type
     * @return the new Ix instance
     * @since 1.0
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final <R> Ix<R> cast(Class<R> clazz) {
        return (Ix)this;
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
    public final <C> Ix<C> collect(IxSupplier<C> initialFactory, IxConsumer2<C, T> collector) {
        return new IxCollect<T, C>(this, nullCheck(initialFactory, "initialFactory is null"), nullCheck(collector, "collector"));
    }

    /**
     * Collects the elements of this sequence into an Object array.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<Object[]> collectToArray() {
        return collect(ToListHelper.<T>initialFactory(), ToListHelper.<T>collector())
                .map(ToListHelper.<T>toArray());
    }

    /**
     * Collects the elements of this sequence into a List.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<List<T>> collectToList() {
        return collect(ToListHelper.<T>initialFactory(), ToListHelper.<T>collector());
    }

    /**
     * Collects the elements of this sequence into a Map where the key is
     * determined from each element via the keySelector function; duplicates are
     * overwritten.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <K> the key type
     * @param keySelector the function that receives the current element and returns
     * a key for it to be used as the Map key.
     * @return the new Ix instance
     * @throws NullPointerException if keySelector is null
     * @since 1.0
     */
    public final <K> Ix<Map<K, T>> collectToMap(IxFunction<? super T, ? extends K> keySelector) {
        IxFunction<T, T> f = IdentityHelper.instance();
        return this.collectToMap(keySelector, f);
    }

    /**
     * Collects the elements of this sequence into a Map where the key is
     * determined from each element via the keySelector function and
     * the value is derived from the same element via the valueSelector function; duplicates are
     * overwritten.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <K> the key type
     * @param <V> the value type
     * @param keySelector the function that receives the current element and returns
     * a key for it to be used as the Map key.
     * @param valueSelector the function that receives the current element and returns
     * a value for it to be used as the Map value
     * @return the new Ix instance
     * @throws NullPointerException if keySelector or valueSelector is null
     * @since 1.0
     */
    public final <K, V> Ix<Map<K, V>> collectToMap(IxFunction<? super T, ? extends K> keySelector, IxFunction<? super T, ? extends V> valueSelector) {
        return new IxToMap<T, K, V>(this, nullCheck(keySelector, "keySelector is null"), nullCheck(valueSelector, "valueSelector is null"));
    }

    /**
     * Collects the elements of this sequence into a multi-Map where the key is
     * determined from each element via the keySelector function.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <K> the key type
     * @param keySelector the function that receives the current element and returns
     * a key for it to be used as the Map key.
     * @return the new Ix instance
     * @throws NullPointerException if keySelector is null
     * @since 1.0
     */
    public final <K> Ix<Map<K, Collection<T>>> collectToMultimap(IxFunction<? super T, ? extends K> keySelector) {
        IxFunction<T, T> f = IdentityHelper.instance();
        return this.collectToMultimap(keySelector, f);
    }

    /**
     * Collects the elements of this sequence into a multi-Map where the key is
     * determined from each element via the keySelector function and
     * the value is derived from the same element via the valueSelector function.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <K> the key type
     * @param <V> the value type
     * @param keySelector the function that receives the current element and returns
     * a key for it to be used as the Map key.
     * @param valueSelector the function that receives the current element and returns
     * a value for it to be used as the Map value
     * @return the new Ix instance
     * @throws NullPointerException if keySelector or valueSelector is null
     * @since 1.0
     */
    public final <K, V> Ix<Map<K, Collection<V>>> collectToMultimap(IxFunction<? super T, ? extends K> keySelector, IxFunction<? super T, ? extends V> valueSelector) {
        return new IxToMultimap<T, K, V>(this, keySelector, valueSelector);
    }

    /**
     * Collects the elements of this sequence into a Set.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<Set<T>> collectToSet() {
        return new IxToSet<T>(this);
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
    public final <R> Ix<R> compose(IxFunction<? super Ix<T>, ? extends Iterable<? extends R>> transformer) {
        return new IxCompose<T, R>(this, nullCheck(transformer, "transformer is null"));
    }

    /**
     * Maps each element from this sequence into subsequent Iterable sequences whose elements are
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
     * @see #flatMap(IxFunction)
     */
    public final <R> Ix<R> concatMap(IxFunction<? super T, ? extends Iterable<? extends R>> mapper) {
        return new IxFlattenIterable<T, R>(this, nullCheck(mapper, "mapper is null"));
    }

    /**
     * Emits elements of this sequence followed by the elements of the other sequence.
     * <p>
     * Note that mergeWith and concatWith operations are the same in the Iterable world.
     * <p>
     * The result's iterator() forwards the call remove() to the current Iterator.
     * @param other the other sequence to emits elements of
     * @return the new Ix instance
     * @throws NullPointerException if other is null
     * @since 1.0
     * @see #mergeWith(Iterable)
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
    public final <K> Ix<T> distinct(IxFunction<? super T, K> keySelector) {
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
    public final Ix<T> distinctUntilChanged(IxPredicate2<? super T, ? super T> comparer) {
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
    public final <K> Ix<T> distinctUntilChanged(IxFunction<? super T, K> keySelector) {
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
    public final Ix<T> doOnNext(IxConsumer<? super T> action) {
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
    public final Ix<T> doOnCompleted(Runnable action) {
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
     * Emit every Nth item only from upstream.
     * <p>Example: Ix.range(1, 5).every(2) will yield {2, 4}.
     * @param nth how many items to skip + 1
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<T> every(int nth) {
        return new IxEvery<T>(this, positive(nth, "nth"));
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
     * @see #union(Iterable)
     * @see #intersect(Iterable)
     */
    public final Ix<T> except(Iterable<? extends T> other) {
        return new IxExcept<T>(this, nullCheck(other, "other is null"));
    }

    /**
     * Emits elements of this sequence which match the given predicate only.
     * <p>
     * The result's iterator() forwards the call to remove() to this' Iterator.
     * @param predicate the predicate receiving the current element and if it
     * returns true, the value is emitted, ignored otherwise.
     * @return  the new Ix instance
     * @throws NullPointerException if predicate is null
     * @since 1.0
     */
    public final Ix<T> filter(IxPredicate<T> predicate) {
        return new IxFilter<T>(this, nullCheck(predicate, "predicate is null"));
    }

    /**
     * Maps each element from this sequence into subsequent Iterable sequences whose elements are
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
     * @see #concatMap(IxFunction)
     */
    public final <R> Ix<R> flatMap(IxFunction<? super T, ? extends Iterable<? extends R>> mapper) {
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
     * @see #groupBy(IxFunction, IxFunction)
     */
    public final <K> Ix<GroupedIx<K, T>> groupBy(IxFunction<? super T, ? extends K> keySelector) {
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
     * @see #groupBy(IxFunction, IxFunction)
     */
    public final <K, V> Ix<GroupedIx<K, V>> groupBy(IxFunction<? super T, ? extends K> keySelector,
            IxFunction<? super T, ? extends V> valueSelector) {
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
     * Hides the identity of this Ix instance and prevents certain identity-based optimizations.
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
     * @see #except(Iterable)
     * @see #union(Iterable)
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
    public final <R> Ix<R> lift(IxFunction<? super Iterator<T>, ? extends Iterator<R>> lifter) {
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
    public final <R> Ix<R> map(IxFunction<? super T, ? extends R> mapper) {
        return new IxMap<T, R>(this, mapper);
    }

    /**
     * Emits the first maximum element according to the given comparator.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param comparator the comparator called with the latest maximum element and
     * the current element; if it returns a positive value, the current element
     * becomes the maximum element.
     * @return the new Ix instance
     * @throws NullPointerException if comparator is null
     * @since 1.0
     * @see #min(Comparator)
     */
    public final Ix<T> max(Comparator<? super T> comparator) {
        return new IxMinMax<T>(this, comparator, -1);
    }

    /**
     * Emits the first maximum element according to their natural order.
     * <p>
     * The sequence may throw a ClassCastException if any of the elements is
     * not self-comparable (i.e., doesn't implement the Comparable interface).
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     * @see #min()
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final Ix<T> max() {
        return max((Comparator)SelfComparator.INSTANCE);
    }

    /**
     * Returns the first maximum integer value.
     * <p>
     * The sequence may throw a ClassCastException if any of the elements is not
     * an Integer object.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public final Ix<Integer> maxInt() {
        return new IxMaxInt((Ix<Integer>)this);
    }

    /**
     * Returns the first maximum long value.
     * <p>
     * The sequence may throw a ClassCastException if any of the elements is not
     * a Long object.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public final Ix<Long> maxLong() {
        return new IxMaxLong((Ix<Long>)this);
    }

    /**
     * Emits elements of this sequence followed by the elements of the other sequence.
     * <p>
     * Note that mergeWith and concatWith operations are the same in the Iterable world.
     * <p>
     * The result's iterator() forwards the call remove() to the current Iterator.
     * @param other the other sequence to emits elements of
     * @return the new Ix instance
     * @throws NullPointerException if other is null
     * @since 1.0
     * @see #concatWith(Iterable)
     */
    public final Ix<T> mergeWith(Iterable<? extends T> other) {
        return concatWith(other);
    }

    /**
     * Emits the first minimum element according to the given comparator.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param comparator the comparator called with the latest minimum element and
     * the current element; if it returns a negative value, the current element
     * becomes the minimum element.
     * @return the new Ix instance
     * @throws NullPointerException if comparator is null
     * @since 1.0
     * @see #max(Comparator)
     */
    public final Ix<T> min(Comparator<? super T> comparator) {
        return new IxMinMax<T>(this, comparator, 1);
    }

    /**
     * Emits the first minimum element according to their natural order.
     * <p>
     * The sequence may throw a ClassCastException if any of the element is
     * not self-comparable (i.e., doesn't implement the Comparable interface).
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     * @see #min()
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final Ix<T> min() {
        return min((Comparator)SelfComparator.INSTANCE);
    }

    /**
     * Returns the first minimum integer value.
     * <p>
     * The sequence may throw a ClassCastException if any of the elements is not
     * an Integer object.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public final Ix<Integer> minInt() {
        return new IxMinInt((Ix<Integer>)this);
    }

    /**
     * Returns the first minimum long value.
     * <p>
     * The sequence may throw a ClassCastException if any of the elements is not
     * a Long object.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public final Ix<Long> minLong() {
        return new IxMinLong((Ix<Long>)this);
    }

    /**
     * Orders elements according to their natural order.
     * <p>
     * The sequence may throw a ClassCastException if any of the elements is not
     * a self-comparable object (i.e., doesn't implement the Comparable interface).
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     * @see #orderByReverse()
     * @see #orderBy(Comparator)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final Ix<T> orderBy() {
        return orderBy((Comparator)SelfComparator.INSTANCE);
    }

    /**
     * Orders elements according to the comparator.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param comparator the comparator comparing two elements; if it returns a negative value,
     * the first element will be before the second; if it returns a positive value,
     * the first element will be after the second.
     * @return the new Ix instance
     * @throws NullPointerException if comparator is null
     * @since 1.0
     * @see #orderBy()
     * @see #orderByReverse(Comparator)
     */
    public final Ix<T> orderBy(Comparator<? super T> comparator) {
        return new IxOrderBy<T, T>(this, IdentityHelper.<T>instance(), nullCheck(comparator, "comparator is null"), 1);
    }

    /**
     * Orders elements according to the natural order of the extracted keys from these elements.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <K> the key type
     * @param keySelector the function receiving each element and returns a self-comparable key for them.
     * @return the new Ix instance
     * @throws NullPointerException if keySelector is null
     * @since 1.0
     * @see #orderByReverse(IxFunction)
     */
    public final <K extends Comparable<? super K>> Ix<T> orderBy(IxFunction<? super T, K> keySelector) {
        return new IxOrderBy<T, K>(this, nullCheck(keySelector, "keySelector is null"), SelfComparator.INSTANCE, 1);
    }

    /**
     * Orders elements according to their reverse natural order.
     * <p>
     * The sequence may throw a ClassCastException if any of the elements is not
     * a self-comparable object (i.e., doesn't implement the Comparable interface).
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     * @see #orderBy()
     * @see #orderByReverse(Comparator)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final Ix<T> orderByReverse() {
        return orderByReverse((Comparator)SelfComparator.INSTANCE);
    }

    /**
     * Orders elements according to the reversed comparator.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param comparator the comparator comparing two elements; if it returns a negative value,
     * the first element will be after the second; if it returns a positive value,
     * the first element will be before the second.
     * @return the new Ix instance
     * @throws NullPointerException if comparator is null
     * @since 1.0
     * @see #orderByReverse()
     * @see #orderBy(Comparator)
     */
    public final Ix<T> orderByReverse(Comparator<? super T> comparator) {
        return new IxOrderBy<T, T>(this, IdentityHelper.<T>instance(), nullCheck(comparator, "comparator is null"), -1);
    }

    /**
     * Orders elements according to the reverse natural order of the extracted keys from these elements.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <K> the key type
     * @param keySelector the function receiving each element and returns a self-comparable key for them.
     * @return the new Ix instance
     * @throws NullPointerException if keySelector is null
     * @since 1.0
     * @see #orderByReverse(IxFunction)
     */
    public final <K extends Comparable<? super K>> Ix<T> orderByReverse(IxFunction<? super T, K> keySelector) {
        return new IxOrderBy<T, K>(this, nullCheck(keySelector, "keySelector is null"), SelfComparator.INSTANCE, -1);
    }

    /**
     * Shares an underlying Iterator that is consumed only once and each created iterator() that calls
     * next() will receive the elements; other iterator() instances may receive different or no elements
     * at all.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new iterator sequence
     * @since 1.0
     */
    public final Ix<T> publish() {
        return new IxPublish<T>(this);
    }

    /**
     * Shares an Iterator, exposed as an Ix sequence, for the duration of the transform function called
     * for each iterator() invocation and emits elements of the resulting iterable sequence of the function.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <R> the result value type
     * @param transform the function that receives an Ix instance sharing a single underlying Iterator to this
     * sequence and returns another Iterable to be the result sequence
     * @return the new Iterable sequence
     * @throws NullPointerException if transform is null
     * @since 1.0
     */
    public final <R> Ix<R> publish(IxFunction<? super Ix<T>, ? extends Iterable<? extends R>> transform) {
        return new IxPublishSelector<T, R>(this, nullCheck(transform, "transform is null"));
    }

    /**
     * Reduces the elements of this sequence into a single value via a reducer function.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param reducer the function that receives the previous reduced element (or the first)  and the current element
     * and returns a new reduced element
     * @return the new Ix instance
     * @throws NullPointerException if reducer is null
     * @since 1.0
     * @see #reduce(IxSupplier, IxFunction2)
     */
    public final Ix<T> reduce(IxFunction2<T, T, T> reducer) {
        return new IxAggregate<T>(this, nullCheck(reducer, "reducer is null"));
    }

    /**
     * Given a per-iterator() initial value, reduces the elements of this sequence into a single
     * value via a reducer function.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <C> the reduced value type
     * @param initialFactory a function called for each iterator() invocation and returns the first
     * reduced value
     * @param reducer the function called with the previous (or initial) reduced value and the current element
     * and returns a new reduced value
     * @return the new Ix instance
     * @throws NullPointerException if initialFactory or reducer is null
     * @since 1.0
     * @see #reduce(IxFunction2)
     */
    public final <C> Ix<C> reduce(IxSupplier<C> initialFactory, IxFunction2<C, T, C> reducer) {
        return new IxReduce<T, C>(this, initialFactory, reducer);
    }

    /**
     * Removes those elements via Iterator.remove() from this sequence that match the
     * given predicate.
     * <p>
     * The result's iterator() forwards the calls to remove() to this' Iterator.
     * @param predicate the function called with the current element and returns true
     * if that particular element should be removed.
     * @return the new Ix instance
     * @throws NullPointerException if predicate is null
     * @since 1.0
     * @see #retain(IxPredicate)
     */
    public final Ix<T> remove(IxPredicate<? super T> predicate) {
        return new IxRemove<T>(this, nullCheck(predicate, "predicate is null"));
    }

    /**
     * Repeats this sequence indefinitely.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<T> repeat() {
        return concat(repeatValue(this));
    }

    /**
     * Repeats this sequence at most the given number of times.
     * <p>A count of zero will yield an empty sequence, a count of one
     * will yield a sequence with only one element and so forth.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param times the number of times to emit the value, non-negative
     * @return the new Ix instance
     * @throws IllegalArgumentException if count is negative
     * @since 1.0
     */
    public final Ix<T> repeat(long times) {
        return concat(repeatValue(this, times));
    }

    /**
     * Repeats this sequence if the given predicate returns true after the sequence
     * completes in a round.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param stopPredicate the predicate called before any emission; returning
     * false keeps repeating the value, returning true terminates the sequence
     * @return the new Ix instance
     * @throws NullPointerException if stopPredicate is null
     * @since 1.0
     */
    public final Ix<T> repeat(IxBooleanSupplier stopPredicate) {
        return concat(repeatValue(this, stopPredicate));
    }

    /**
     * Repeats this sequence if the given predicate returns true after the sequence
     * completes in a round or at most the given number of times.
     * <p>
     * A count of zero will yield an empty sequence, a count of one
     * will yield a sequence with only one element and so forth.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param times the number of times to emit the value, non-negative
     * @param stopPredicate the predicate called before any emission; returning
     * false keeps repeating the value, returning true terminates the sequence
     * @return the new Ix instance
     * @throws IllegalArgumentException if count is negative
     * @throws NullPointerException if stopPredicate is null
     * @since 1.0
     */
    public final Ix<T> repeat(long times, IxBooleanSupplier stopPredicate) {
        return concat(repeatValue(this, times, stopPredicate));
    }

    /**
     * Caches and replays all elements of this sequence to consumers of this' iterator().
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<T> replay() {
        return new IxReplay<T>(this);
    }

    /**
     * Caches and replays the last {@code size} elements of this sequence to consumers of this' iterator().
     * <p>
     * Consumption by any of the iterator() may move the source sequence forward and subsequent iterator()
     * consumers may get a different set of values
     * <p>
     * The result's iterator() doesn't support remove().
     * @param size the maximum number of elements to keep replaying to new iterator() consumers, positive
     * @return the new Ix instance
     * @throws IllegalArgumentException if size is non-positive
     * @since 1.0
     */
    public final Ix<T> replay(int size) {
        return new IxReplaySize<T>(this, positive(size, "size"));
    }

    /**
     * Caches and replays the elements of this sequence for the duration of the given transform function
     * without consuming this sequence multiple times.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <R> the result value type
     * @param transform the function receiving a view into the cache and returns an Iterable sequence whose
     * elements will be emitted by this Ix.
     * @return the new Ix instance
     * @throws NullPointerException if transform is null
     * @since 1.0
     */
    public final <R> Ix<R> replay(IxFunction<? super Ix<T>, ? extends Iterable<? extends R>> transform) {
        return new IxReplaySelector<T, R>(this, nullCheck(transform, "transform is null"));
    }

    /**
     * Caches and replays the the last {@code size} elements of this sequence for the duration of the given transform function
     * without consuming this sequence multiple times.
     * <p>
     * Consumption by any of the inner Ix' iterator() may move the shared sequence forward and subsequent iterator()
     * consumers may get a different set of values
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <R> the result value type
     * @param size the maximum number of elements to keep replaying to new inner Ix iterator() consumers, positive
     * @param transform the function receiving a view into the cache and returns an Iterable sequence whose
     * elements will be emitted by this Ix.
     * @return the new Ix instance
     * @throws NullPointerException if transform is null
     * @throws IllegalArgumentException if size is non-positive
     * @since 1.0
     */
    public final <R> Ix<R> replay(int size, IxFunction<? super Ix<T>, ? extends Iterable<? extends R>> transform) {
        return new IxReplaySizeSelector<T, R>(this, positive(size, "size"), nullCheck(transform, "transform is null"));
    }

    /**
     * Removes those elements via Iterator.remove() from this sequence that don't match the
     * given predicate.
     * <p>
     * The result's iterator() forwards the calls to remove() to this' Iterator.
     * @param predicate the function called with the current element and returns false
     * if that particular element should be removed.
     * @return the new Ix instance
     * @throws NullPointerException if predicate is null
     * @since 1.0
     * @see #remove(IxPredicate)
     */
    public final Ix<T> retain(IxPredicate<? super T> predicate) {
        return new IxRetain<T>(this, nullCheck(predicate, "predicate is null"));
    }

    /**
     * Plays this sequence in reverse.
     * <p>
     * The reversal requires consuming the entire sequence and doesn't work with
     * infinite sequences.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    public final Ix<T> reverse() {
        return new IxReverse<T>(this);
    }

    /**
     * Performs a running accumulation, that is, returns intermediate elements returned by the
     * scanner function.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param scanner the function that receives the previous (or first) accumulated element and the current
     * element and returns a value to be emitted and to become the accumulated element
     * @return the new Ix instance
     * @throws NullPointerException if scanner is null
     * @since 1.0
     */
    public final Ix<T> scan(IxFunction2<T, T, T> scanner) {
        return new IxScan<T>(this, nullCheck(scanner, "scanner is null"));
    }

    /**
     * Performs a running accumulation, that is, returns intermediate elements returned by the
     * scanner function, starting with a per-iterator initial value.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <R> the accumulated value type
     * @param initialFactory function called for each iterator() and returns the initial accumulator value
     * @param scanner the function that receives the previous (or first) accumulated element and the current
     * element and returns a value to be emitted and to become the accumulated element
     * @return the new Ix instance
     * @throws NullPointerException if initialFactory or scanner is null
     * @since 1.0
     */
    public final <R> Ix<R> scan(IxSupplier<R> initialFactory, IxFunction2<R, T, R> scanner) {
        return new IxScanSeed<T, R>(this, nullCheck(initialFactory, "initialFactory is null"), nullCheck(scanner, "scanner is null"));
    }

    /**
     * Determines if two sequences have the same elements in the same order and are the same length based
     * on null-safe object equality.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param other the other sequence to compare with
     * @return the new Ix instance
     * @throws NullPointerException if other is null
     * @since 1.0
     */
    public final Ix<Boolean> sequenceEqual(Iterable<? extends T> other) {
        return sequenceEqual(nullCheck(other, "other is null"), EqualityHelper.INSTANCE);
    }

    /**
     * Determines if two sequences have the same elements in the same order and are the same length based
     * on the given comparer's boolean value.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param other the other sequence to compare with
     * @param comparer the predicate receiving elements from this and the other sequence and returns true if those
     * elements are equal
     * @return the new Ix instance
     * @throws NullPointerException if other is null
     * @since 1.0
     */
    public final Ix<Boolean> sequenceEqual(Iterable<? extends T> other, IxPredicate2<? super T, ? super T> comparer) {
        return new IxSequenceEqual<T>(this, nullCheck(other, "other is null"), nullCheck(comparer, "comparer is null"));
    }

    /**
     * Skips the first n elements from this sequence.
     * <p>
     * The result's iterator() forwards the calls to remove() to this' Iterator.
     * @param n the elements to skip, non-positive values won't skip any elements
     * @return the new Ix instance
     * @since 1.0
     * @see #take(int)
     */
    public final Ix<T> skip(int n) {
        if (n <= 0) {
            return this;
        }
        return new IxSkip<T>(this, n);
    }

    /**
     * Skips the last n elements from this sequence.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param n the elements to skip, non-positive values won't skip any elements
     * @return the new Ix instance
     * @since 1.0
     * @see #takeLast(int)
     */
    public final Ix<T> skipLast(int n) {
        if (n <= 0) {
            return this;
        }
        return new IxSkipLast<T>(this, n);
    }

    /**
     * Skips the first elements while the given predicate returns true; once it returns false
     * all subsequent elements are emitted.
     * <p>
     * The result's iterator() forwards the calls to remove() to this' Iterator.
     * @param predicate the predicate called with the current element and returns true
     * to skip that element
     * @return the new Ix instance
     * @throws NullPointerException if predicate is null
     * @since 1.0
     * @see #takeWhile(IxPredicate)
     */
    public final Ix<T> skipWhile(IxPredicate<? super T> predicate) {
        return new IxSkipWhile<T>(this, nullCheck(predicate, "predicate is null"));
    }

    /**
     * Emits elements of the given array followed by the elements of this sequence.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param values the array of values to emit first
     * @return the new Ix instance
     * @throws NullPointerException if values is null
     * @since 1.0
     */
    public final Ix<T> startWith(T... values) {
        return concat(fromArray(values), this);
    }

    /**
     * Sums values of this sequence as integer.
     * <p>
     * The operation may throw a ClassCastException if any of the elements
     * is not an Integer.
     * <p>
     * An empty sequence yields an empty sum.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public final Ix<Integer> sumInt() {
        return new IxSumInt((Ix<Integer>)this);
    }

    /**
     * Sums values of this sequence as long.
     * <p>
     * The operation may throw a ClassCastException if any of the elements
     * is not an Long.
     * <p>
     * An empty sequence yields an empty sum.
     * <p>
     * The result's iterator() doesn't support remove().
     * @return the new Ix instance
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public final Ix<Long> sumLong() {
        return new IxSumLong((Ix<Long>)this);
    }

    /**
     * Emits the elements of the other sequence if this sequence is empty.
     * <p>
     * The result's Iterator forwards calls of remove() to this' or the other's Iterator.
     * @param other the other Iterable instance, not null
     * @return the new Ix instance
     * @throws NullPointerException if other is null
     * @since 1.0
     */
    public final Ix<T> switchIfEmpty(Iterable<? extends T> other) {
        return new IxSwitchIfEmpty<T>(this, nullCheck(other, "other is null"));
    }

    /**
     * Emits the first n elements (or less) of this sequence.
     * <p>
     * The result's Iterator forwards calls of remove() to this' Iterator.
     * @param n the number of items to emit at most, non-negative
     * @return the new Ix instance
     * @throws IllegalArgumentException if n is negative
     * @since 1.0
     * @see #skip(int)
     */
    public final Ix<T> take(int n) {
        return new IxTake<T>(this, nonNegative(n, "n"));
    }

    /**
     * Emits the last n elements (or fewer) of this sequence.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param n the number of last elements to emit
     * @return the new Ix instance
     * @throws IllegalArgumentException if n is negative
     * @since 1.0
     * @see #skipLast(int)
     */
    public final Ix<T> takeLast(int n) {
        return new IxTakeLast<T>(this, nonNegative(n, "n"));
    }

    /**
     * Take elements from this sequence until the given predicate returns true
     * for the current element (checked after emission of that element).
     * <p>
     * The result's Iterator forwards calls of remove() to this' Iterator.
     * @param stopPredicate the function receiving the current element and returns
     * true if no further elements should be emitted after this element.
     * @return the new Ix instance
     * @throws NullPointerException if stopPredicate is null
     * @since 1.0
     */
    public final Ix<T> takeUntil(IxPredicate<? super T> stopPredicate) {
        return new IxTakeUntil<T>(this, nullCheck(stopPredicate, "stopPredicate is null"));
    }

    /**
     * Take elements from this sequence while the given predicate returns true
     * for the current element (checked before emission of that element).
     * <p>
     * The result's Iterator forwards calls of remove() to this' Iterator.
     * @param predicate the function receiving the current element and returns
     * false if no further elements should be emitted (not even the current).
     * @return the new Ix instance
     * @throws NullPointerException if predicate is null
     * @since 1.0
     */
    public final Ix<T> takeWhile(IxPredicate<? super T> predicate) {
        return new IxTakeWhile<T>(this, nullCheck(predicate, "predicate is null"));
    }


    /**
     * Maps this sequence of numbers into a sequence of longs.
     * <p>
     * The sequence may throw a ClassCastException if any of the elements
     * is not a subclass of Number.
     * <p>
     * The result's Iterator forwards calls of remove() to this' Iterator.
     * @return the new Ix instance
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public final Ix<Long> toLong() {
        return ((Ix<Number>)this).map(NumberToLongHelper.INSTANCE);
    }

    /**
     * Allows working with the Iterator of this sequence and emit elements in
     * a more flexible way
     * <p>
     * The result's iterator() doesn't support remove().
     * @param <R> the result value type
     * @param transformer the functional interface whose moveNext is called with the
     * current Iterator and should signal a value to be emitted for it.
     * @return the new Ix instance
     * @throws NullPointerException if transformer is null
     */
    public final <R> Ix<R> transform(IxTransform<T, R> transformer) {
        return new IxTransformer<T, R>(this, nullCheck(transformer, "transformer is null"));
    }

    /**
     * Emits a distinct set of values from both this and the other sequence.
     * <p>
     * The result's iterator() doesn't support remove().
     * @param other the other Iterable sequence, not null
     * @return the new Ix instance
     * @throws NullPointerException if other is null
     * @since 1.0
     * @see #except(Iterable)
     * @see #intersect(Iterable)
     */
    public final Ix<T> union(Iterable<? extends T> other) {
        return new IxUnion<T>(this, nullCheck(other, "other is null"));
    }

    /**
     * Emits inner Ix Iterables of non-overlapping sequences mapped from this sequence
     * with the given maximum size each.
     *
     * <p>
     * The result's and the inner Ix' iterator() don't support remove().
     * @param size the maximum size of the inner windows, positive
     * @return the new Ix instance
     * @throws IllegalArgumentException if size is non-positive
     * @since 1.0
     * @see #window(int, int)
     */
    public final Ix<Ix<T>> window(int size) {
        return new IxWindow<T>(this, positive(size, "size"));
    }

    /**
     * Emits inner Ix Iterables of potentially overlapping sequences mapped from this
     * sequence with the given maximum size each and started each {@code skip} source elements.
     * @param size the maximum size of the inner windows, positive
     * @param skip after how many elements to start a new window (repeatedly), positive
     * @return the new Ix instance
     * @throws IllegalArgumentException if size or skip is non-positive
     * @since 1.0
     * @see #window(int)
     */
    public final Ix<Ix<T>> window(int size, int skip) {
        if (size == skip) {
            return window(size);
        }
        if (size < skip) {
            return new IxWindowSkip<T>(this, positive(size, "size"), positive(skip, "skip"));
        }
        return new IxWindowOverlap<T>(this, positive(size, "size"), positive(skip, "skip"));
    }

    /**
     * Combines the next element from this and the other source Iterable via a zipper function.
     * <p>
     * If one of the source Iterables is sorter the sequence terminates eagerly.
     * <p>
     * The result's iterator() doesn't support remove().
     *
     * @param <U> the other source's element type
     * @param <R> the result value type
     * @param other the the other source Iterable
     * @param zipper the function that takes one from each source, not null
     * @return the new Ix instance
     * @throws NullPointerException if other or zipper is null
     * @since 1.0
     */
    public final <U, R> Ix<R> zipWith(Iterable<U> other, IxFunction2<? super T, ? super U, ? extends R> zipper) {
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
     * @see #foreachWhile(IxPredicate)
     */
    public final void foreach(IxConsumer<? super T> action) {
        for (T t : this) {
            action.accept(t);
        }
    }

    /**
     * Consumes the entire sequence and calls the given predicate with each value;
     * which can stop the iteration by returning false.
     * @param predicate the predicate to call with the current element and should
     * return true to continue the loop or false to quit the loop.
     * @throws NullPointerException if action is null
     * @since 1.0
     * @see #foreach(IxConsumer)
     */
    public final void foreachWhile(IxPredicate<? super T> predicate) {
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
                    System.out.println();
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
     * @see #retainAll(IxPredicate)
     * @see #removeAll()
     * @since 1.0
     */
    public final void removeAll(IxPredicate<? super T> predicate) {
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
     * @see #removeAll(IxPredicate)
     * @since 1.0
     */
    public final void retainAll(IxPredicate<? super T> predicate) {
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
     * Returns the single element of this sequence, defaultValue
     * if this sequence is empty or IndexOutOfBoundsException if this sequence has more
     * than one element
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
    public final void subscribe(IxConsumer<? super T> onNext) {
        for (T v : this) {
            onNext.accept(v);
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
    public final void subscribe(IxConsumer<? super T> onNext, IxConsumer<Throwable> onError) {
        try {
            for (T v : this) {
                onNext.accept(v);
            }
        } catch (Throwable ex) {
            onError.accept(ex);
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
    public final void subscribe(IxConsumer<? super T> onNext, IxConsumer<Throwable> onError, Runnable onCompleted) {
        try {
            for (T v : this) {
                onNext.accept(v);
            }
        } catch (Throwable ex) {
            onError.accept(ex);
            return;
        }
        onCompleted.run();
    }

    /**
     * Collects the elements of this sequence into an Object array.
     * <p>
     * @return the new Object array instance
     * @since 1.0
     */
    public final Object[] toArray() {
        return toList().toArray();
    }

    /**
     * Collects the elements of this sequence into a generic array provided.
     * <p>
     * @param <U> the output array type
     * @param array the target array to fill in or use as a template if not long enough
     * @return the new generic array instance
     * @since 1.0
     */
    public final <U> U[] toArray(U[] array) {
        return toList().toArray(array);
    }

    /**
     * Collects the elements of this sequence into a List.
     * <p>
     * @return the List instance
     * @since 1.0
     */
    public final List<T> toList() {
        List<T> list = new ArrayList<T>();
        return into(list);
    }


    /**
     * Collects the elements of this sequence into a Map where the key is
     * determined from each element via the keySelector function; duplicates are
     * overwritten.
     * <p>
     * @param <K> the key type
     * @param keySelector the function that receives the current element and returns
     * a key for it to be used as the Map key.
     * @return the new Map instance
     * @throws NullPointerException if keySelector is null
     * @since 1.0
     */
    public final <K> Map<K, T> toMap(IxFunction<? super T, ? extends K> keySelector) {
        return this.<K>collectToMap(keySelector).first();
    }

    /**
     * Collects the elements of this sequence into a Map where the key is
     * determined from each element via the keySelector function and
     * the value is derived from the same element via the valueSelector function; duplicates are
     * overwritten.
     * <p>
     * @param <K> the key type
     * @param <V> the value type
     * @param keySelector the function that receives the current element and returns
     * a key for it to be used as the Map key.
     * @param valueSelector the function that receives the current element and returns
     * a value for it to be used as the Map value
     * @return the new Map instance
     * @throws NullPointerException if keySelector or valueSelector is null
     * @since 1.0
     */
    public final <K, V> Map<K, V> toMap(IxFunction<? super T, ? extends K> keySelector, IxFunction<? super T, ? extends V> valueSelector) {
        return this.<K, V>collectToMap(keySelector, valueSelector).first();
    }

    /**
     * Collects the elements of this sequence into a multi-Map where the key is
     * determined from each element via the keySelector function.
     * <p>
     * @param <K> the key type
     * @param keySelector the function that receives the current element and returns
     * a key for it to be used as the Map key.
     * @return the new Map instance
     * @throws NullPointerException if keySelector is null
     * @since 1.0
     */
    public final <K> Map<K, Collection<T>> toMultimap(IxFunction<? super T, ? extends K> keySelector) {
        return this.<K>collectToMultimap(keySelector).first();
    }

    /**
     * Collects the elements of this sequence into a multi-Map where the key is
     * determined from each element via the keySelector function and
     * the value is derived from the same element via the valueSelector function.
     * <p>
     * @param <K> the key type
     * @param <V> the value type
     * @param keySelector the function that receives the current element and returns
     * a key for it to be used as the Map key.
     * @param valueSelector the function that receives the current element and returns
     * a value for it to be used as the Map value
     * @return the new Map instance
     * @throws NullPointerException if keySelector or valueSelector is null
     * @since 1.0
     */
    public final <K, V> Map<K, Collection<V>> toMultimap(IxFunction<? super T, ? extends K> keySelector, IxFunction<? super T, ? extends V> valueSelector) {
        return this.<K, V>collectToMultimap(keySelector, valueSelector).first();
    }

    /**
     * Collects the elements of this sequence into a Set.
     * <p>
     * @return the new Ix instance
     * @since 1.0
     */
    public final Set<T> toSet() {
        Set<T> list = new HashSet<T>();
        return into(list);
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
     * an IllegalArgumentException otherwise.
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
     * an IllegalArgumentException otherwise.
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
     * an IllegalArgumentException otherwise.
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
