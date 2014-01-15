/*
* Copyright 2011-2014 David Karnok
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

import ix.operators.ToIterable;
import ix.util.GroupedIterable;
import ix.util.IxHelperFunctions;
import ix.util.Pair;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import rx.Notification;
import rx.Observable;
import rx.Scheduler;
import rx.util.functions.Action0;
import rx.util.functions.Action1;
import rx.util.functions.Func0;
import rx.util.functions.Func1;
import rx.util.functions.Func2;
import rx.util.functions.Functions;

/**
 * An iterable builder which offers methods to chain the
 * sequence of some Interactive operations.
 * <p>This builder is the dual of the
 * {@link rx.Observable} class.</p>
 * @author akarnokd, Jan 25, 2012
 * @param <T> the element type
 * @since 0.96
 */
public final class IterableBuilder<T> implements Iterable<T> {
    /**
     * Defers the source iterable creation to registration time and
     * calls the given <code>func</code> for the actual source.
     * @param <T> the element type
     * @param func the function that returns an iterable.
     * @return the new iterable
     */
    public static <T> IterableBuilder<T> defer(
            final Func0<? extends Iterable<T>> func) {
        return from(Interactive.defer(func));
    }
    /**
     * Creates a new iterable builder instance by wrapping the given
     * array. Changes to the array will be visible through
     * the iterator.
     * @param <T> the element type
     * @param from the source index inclusive
     * @param to the destination index exclusive
     * @param ts the array of ts
     * @return the created iterable builder
     */
    public static <T> IterableBuilder<T> fromPart(int from, int to, final T... ts) {
        return from(Interactive.toIterablePart(from, to, ts));
    }
    /**
     * Creates a new iterable builder instance by wrapping the given
     * source sequence, if not already a builder.
     * @param <T> the element type
     * @param source the source sequence
     * @return the created iterable builder
     */
    public static <T> IterableBuilder<T> from(final Iterable<T> source) {
        if (source instanceof IterableBuilder) {
            return (IterableBuilder<T>)source;
        }
        return new IterableBuilder<T>(source);
    }
    /**
     * Creates a new iterable builder instance by wrapping the given
     * source sequence.
     * @param <T> the element type
     * @param source the source sequence
     * @return the created iterable builder
     * @since 0.97
     */
    public static <T> IterableBuilder<T> newBuilder(final Iterable<T> source) {
        return new IterableBuilder<T>(source);
    }
    /**
     * Creates a new iterable builder by wrapping the given observable.
     * <p>The resulting iterable does not support the {@code remove()} method.</p>
     * @param <T> the element type
     * @param source the source observable
     * @return the created iterable builder
     */
    public static <T> IterableBuilder<T> from(Observable<T> source) {
        return from(new ToIterable<T>(source));
    }
    /**
     * Creates a new iterable builder instance by wrapping the given
     * array. Changes to the array will be visible through
     * the iterator.
     * @param <T> the element type
     * @param ts the array of ts
     * @return the created iterable builder
     */
    public static <T> IterableBuilder<T> from(final T... ts) {
        return from(Interactive.toIterable(ts));
    }
    /**
     * A generator function which returns Ts based on the termination condition and the way it computes the next values.
     * This is equivalent to:
     * <code><pre>
     * T value = seed;
     * while (predicate(value)) {
     *     yield value;
     *     value = next(value);
     * }
     * </pre></code>
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param <T> the element type
     * @param seed the initial value
     * @param predicate the predicate to terminate the process
     * @param next the function that computes the next value.
     * @return the new iterable
     */
    public static <T> IterableBuilder<T> generate(T seed, Func1<? super T, Boolean> predicate, Func1<? super T, ? extends T> next) {
        return from(Interactive.generate(seed, predicate, next));
    }
    /**
     * Creates an integer iteratable builder which returns numbers from the start position in the count size.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param start the starting value.
     * @param count the number of elements to return, negative count means counting down from the start.
     * @return the iterator.
     */
    public static IterableBuilder<Integer> range(int start, int count) {
        return from(Interactive.range(start, count));
    }
    /**
     * Creates an long iterable builder which returns numbers from the start position in the count size.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param start the starting value.
     * @param count the number of elements to return, negative count means counting down from the start.
     * @return the iterator.
     */
    public static IterableBuilder<Long> range(long start, long count) {
        return from(Interactive.range(start, count));
    }
    /**
     * Creates an iterable builder which repeats the given
     * value indefinitely.
     * <p>The resulting iterable does not support the {@code remove()} method.</p>
     * @param <T> the element type
     * @param t the value to repeat
     * @return the created iterable builder
     */
    public static <T> IterableBuilder<T> repeat(final T t) {
        return from(Interactive.repeat(t));
    }
    /**
     * Returns an iterable builder which repeats the given single value the specified number of times.
     * <p>The returned iterable does not support the {@code remove()} method.</p>
     * @param <T> the value type
     * @param t the value to repeat
     * @param count the repeat amount
     * @return the iterable
     * @since 0.96
     */
    public static <T> IterableBuilder<T> repeat(final T t, int count) {
        return from(Interactive.repeat(t, count));
    }
    /** The backing iterable. */
    protected final Iterable<T> it;
    /**
     * Constructor.
     * @param source the backing iterable
     */
    protected IterableBuilder(Iterable<T> source) {
        this.it = source;
    }
    /**
     * Creates an iterable which traverses the source iterable and maintains a running sum value based
     * on the <code>sum</code> function parameter. Once the source is depleted, it
     * applies the <code>divide</code> function and returns its result.
     * This operator is a general base for averaging (where {@code sum(u, t) => u + t}, {@code divide(u, index) => u / index}),
     * summing (where {@code sum(u, t) => u + t}, and {@code divide(u, index) => u)}),
     * minimum, maximum, etc.
     * If the traversal of the source fails due an exception, that exception is reflected on the
     * {@code next()} call of the returned iterator.
     * The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.
     * @param <U> the itermediate aggregation type
     * @param <V> the resulting aggregation type
     * @param sum the function which takes the current itermediate value,
     * the current source value and should produce a new intermediate value.
     * for the first element of T, the U parameter will receive null
     * @param divide the function which takes the last intermediate value and a total count of Ts seen and should return the final aggregation value.
     * @return the new iterable
     */
    public <U, V> IterableBuilder<V> aggregate(
            final Func2<? super U, ? super T, ? extends U> sum,
            final Func2<? super U, ? super Integer, ? extends V> divide) {
        return from(Interactive.aggregate(it, sum, divide));
    }
    /**
     * Returns an iterable which contains true if all
     * elements of the source iterable satisfy the predicate.
     * The operator might return a false before fully iterating the source.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param predicate the predicate
     * @return the new iterable
     */
    public IterableBuilder<Boolean> all(final Func1<? super T, Boolean> predicate) {
        return from(Interactive.all(it, predicate));
    }
    /**
     * Determines if the given source has any elements at all.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @return the new iterable with a single true or false
     */
    public IterableBuilder<Boolean> any() {
        return from(Interactive.any(it));
    }
    /**
     * Tests if there is any element of the source that satisfies the given predicate function.
     * @param predicate the predicate tester function
     * @return the new iterable
     */
    public IterableBuilder<Boolean> any(
            final Func1<? super T, Boolean> predicate
    ) {
        return from(Interactive.any(it, predicate));
    }
    /**
     * Returns a pair of the maximum argument and value from the given sequence.
     * @param <V> the value type for the comparison, must be self comparable
     * @param valueSelector the value selector function
     * @return the pair of the first maximum element and value, null if the sequence was empty
     * @since 0.96
     */
    public <V extends Comparable<? super V>> Pair<T, V> argAndMax(
            Func1<? super T, ? extends V> valueSelector) {
        return Interactive.argAndMax(it, valueSelector);
    }
    /**
     * Returns a pair of the maximum argument and value from the given sequence.
     * @param <V> the value type
     * @param valueSelector the selector to extract the value from T
     * @param valueComparator the comparator to compare two values
     * @return the first pair of max argument and value or null if the source sequence was empty
     * @since 0.96
     */
    public <V> Pair<T, V> argAndMax(
            Func1<? super T, ? extends V> valueSelector,
            Comparator<? super V> valueComparator) {
        return Interactive.argAndMax(it, valueSelector, valueComparator);
    }
    /**
     * Returns a pair of the minimum argument and value from the given sequence.
     * @param <V> the value type for the comparison, must be self comparable
     * @param valueSelector the value selector function
     * @return the pair of the first minimum element and value, null if the sequence was empty
     * @since 0.96
     */
    public <V extends Comparable<? super V>> Pair<T, V> argAndMin(
            Func1<? super T, ? extends V> valueSelector) {
        return Interactive.argAndMin(it, valueSelector);
    }
    /**
     * Returns a pair of the minimum argument and value from the given sequence.
     * @param <V> the value type
     * @param valueSelector the selector to extract the value from T
     * @param valueComparator the comparator to compare two values
     * @return the first pair of minimum argument and value or null if the source sequence was empty
     * @since 0.96
     */
    public <V> Pair<T, V> argAndMin(
            Func1<? super T, ? extends V> valueSelector,
            Comparator<? super V> valueComparator) {
        return Interactive.argAndMin(it, valueSelector, valueComparator);
    }
    /**
     * Computes and signals the average value of the BigDecimal source.
     * The source may not send nulls.
     * <p>Note that it uses forced cast of this sequence. If T != BigDecimal this
     * method is guaranteed to throw ClassCastException.</p>
     * @return the observable for the average value
     */
    @SuppressWarnings("unchecked")
    public IterableBuilder<BigDecimal> averageBigDecimal() {
        return from(Interactive.averageBigDecimal((Iterable<BigDecimal>)it));
    }
    /**
     * Computes and signals the average value of the BigInteger source.
     * The source may not send nulls.
     * <p>Note that it uses forced cast of this sequence. If T != BigInteger this
     * method is guaranteed to throw ClassCastException.</p>
     * @return the observable for the average value
     */
    
    @SuppressWarnings("unchecked")
    public IterableBuilder<BigDecimal> averageBigInteger() {
        return from(Interactive.averageBigInteger((Iterable<BigInteger>)it));
    }
    /**
     * Computes and signals the average value of the Double source.
     * The source may not send nulls.
     * @return the observable for the average value
     */
    
    @SuppressWarnings("unchecked")
    public IterableBuilder<Double> averageDouble() {
        return from(Interactive.averageDouble((Iterable<Double>)it));
    }
    /**
     * Computes and signals the average value of the Float source.
     * The source may not send nulls.
     * @return the observable for the average value
     */
    
    @SuppressWarnings("unchecked")
    public IterableBuilder<Float> averageFloat() {
        return from(Interactive.averageFloat((Iterable<Float>)it));
    }
    /**
     * Computes and signals the average value of the integer source.
     * The source may not send nulls.
     * The intermediate aggregation used double values.
     * @return the observable for the average value
     */
    
    @SuppressWarnings("unchecked")
    public IterableBuilder<Double> averageInt() {
        return from(Interactive.averageInt((Iterable<Integer>)it));
        
    }
    /**
     * Computes and signals the average value of the Long source.
     * The source may not send nulls.
     * The intermediate aggregation used double values.
     * @return the observable for the average value
     */
    
    @SuppressWarnings("unchecked")
    public IterableBuilder<Double> averageLong() {
        return from(Interactive.averageLong((Iterable<Long>)it));
    }
    /**
     * Returns an iterable which buffers the source elements
     * into <code>bufferSize</code> lists.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param bufferSize the buffer size.
     * @return the new iterable
     */
    public IterableBuilder<List<T>> buffer(int bufferSize) {
        return from(Interactive.buffer(it, bufferSize));
    }
    // #GWT-IGNORE-START
    /**
     * Casts the source iterable into a different typ by using a type token.
     * If the source contains a wrong element, the <code>next()</code>
     * will throw a <code>ClassCastException</code>.
     * <p>The returned iterator forwards all <code>remove()</code> calls
     * to the source.</p>
     * @param <U> the result element type
     * @param token the type token
     * @return the new iterable
     */
    public <U> IterableBuilder<U> cast(final Class<U> token) {
        return from(Interactive.cast(it, token));
    }
    // #GWT-IGNORE-END
    /**
     * Concatenate this iterable with the other iterable in a way, that calling the second <code>iterator()</code>
     * only happens when there is no more element in the first iterator.
     * <p>The returned iterator forwards all <code>remove()</code> calls
     * to the current source (first or next).
     * @param other the second iterable
     * @return the new iterable
     */
    public IterableBuilder<T> concat(Iterable<? extends T> other) {
        return from(Interactive.concat(it, other));
    }
    /**
     * Concatenate this iterable with the sequence of array values.
     * @param values the array values
     * @return the created iterable builder
     */
    public IterableBuilder<T> concat(final T... values) {
        return concat(Interactive.toIterable(values));
    }
    /**
     * Creates an iterable builder which contains the concatenation
     * of this iterable and the rest iterable provided.
     * @param others the other iterables
     * @return the created iterable
     */
    @SuppressWarnings("unchecked")
    public IterableBuilder<T> concatAll(Iterable<? extends Iterable<? extends T>> others) {
        return from(Interactive.concat(Interactive.startWith(others, it)));
    }
    /**
     * Returns an iterable which checks for the existence of the supplied
     * value by comparing the elements of the source iterable using reference
     * and <code>equals()</code>. The iterable then returns a single true or false.
     * @param value the value to check
     * @return the new iterable
     */
    public IterableBuilder<Boolean> contains(final T value) {
        return from(Interactive.contains(it, value));
    }
    /**
     * Counts the elements of the iterable source by using a 32 bit <code>int</code>.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @return the new iterable
     */
    public IterableBuilder<Integer> count() {
        return from(Interactive.count(it));
    }
    /**
     * Counts the elements of the iterable source by using a 64 bit <code>long</code>.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @return the new iterable
     */
    public IterableBuilder<Long> countLong() {
        return from(Interactive.countLong(it));
    }
    /**
     * Convert the source materialized elements into normal iterator behavior.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @return the new iterable
     */
    @SuppressWarnings("unchecked")
    public IterableBuilder<T> dematerialize() {
        return from(Interactive.dematerialize((Iterable<Notification<T>>)it));
    }
    /**
     * Creates an iterable which ensures that subsequent values of T are not equal  (reference and equals).
     * @return the new iterable
     */
    public IterableBuilder<T> distinctNext() {
        return from(Interactive.distinctNext(it));
    }
    /**
     * Creates an iterable which ensures that subsequent values of
     * T are not equal in respect to the extracted keys (reference and equals).
     * @param <U> the key type
     * @param keySelector the function to extract the keys which will be compared
     * @return the new iterable
     */
    public <U> IterableBuilder<T> distinctNext(final Func1<T, U> keySelector) {
        return from(Interactive.distinctNext(it, keySelector));
    }
    /**
     * Returns an iterable which filters its elements based if they vere ever seen before in
     * the current iteration.
     * Value equality is computed by reference equality and <code>equals()</code>
     * @return the new iterable
     */
    public IterableBuilder<T> distinct() {
        return from(Interactive.distinct(it));
    }
    /**
     * Returns an iterable which filters its elements by an unique key
     * in a way that when multiple source items produce the same key, only
     * the first one ever seen gets relayed further on.
     * Key equality is computed by reference equality and <code>equals()</code>
     * @param <U> the key element type
     * @param keySelector the key selector for only-once filtering
     * @return the new iterable
     */
    public <U> IterableBuilder<T> distinct(Func1<? super T, ? extends U> keySelector) {
        return from(Interactive.distinct(it, keySelector, Functions.<T>identity()));
    }
    /**
     * Returns an iterable which reiterates over and over again on <code>source</code>
     * as long as the gate is true. The gate function is checked only
     * when a pass over the source stream was completed.
     * Note that using this operator on an empty iterable may result
     * in a direct infinite loop in hasNext() or next() calls depending on the gate function.
     * <p>The returned iterator forwards all <code>remove()</code> calls
     * to the source.</p>
     * @param gate the gate function to stop the repeat
     * @return the new iterable
     */
    
    public IterableBuilder<T> doWhile(
            final Func0<Boolean> gate) {
        return from(Interactive.doWhile(it, gate));
    }
    /**
     * Creates an iterable sequence which returns all elements from source
     * followed by the supplied value as last.
     * <p>The returned iterable forwards all {@code remove()}
     * methods to the source iterable, except the last element where it
     * throws UnsupportedOperationException.</p>
     * @param value the value to append
     * @return the new iterable
     */
    public IterableBuilder<T> endWith(final T value) {
        return from(Interactive.endWith(it, value));
    }
    /**
     * Returns an iterable which executes the given action after
     * the stream completes.
     * <p>The returned iterator forwards all <code>remove()</code> calls
     * to the source.</p>
     * @param action the action to invoke
     * @return the new iterable
     */
    public IterableBuilder<T> finish(Action0 action) {
        return from(Interactive.finish(it, action));
    }
    /**
     * Returns the first element from the iterable sequence or
     * throws a NoSuchElementException.
     * @return the first element
     */
    public T first() {
        return Interactive.first(it);
    }
    /**
     * Creates an iterable which traverses the source iterable,
     * and based on the key selector, groups values extracted by valueSelector into GroupedIterables,
     * which can be interated over later on.
     * The equivalence of the keys are determined via reference
     * equality and <code>equals()</code> equality.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param <K> the result group element type
     * @param <V> the result group keys
     * @param keySelector the key selector
     * @param valueSelector the value selector
     * @return the new iterable
     * @since 0.96.1
     */
    public <K, V> IterableBuilder<GroupedIterable<K, V>> groupBy0(
            final Func1<? super T, ? extends K> keySelector,
            final Func1<? super T, ? extends V> valueSelector) {
        return from(Interactive.groupBy(it, keySelector, valueSelector));
    }
    /**
     * Creates an iterable which traverses the source iterable,
     * and based on the key selector, groups values the elements into GroupedIterables,
     * which can be interated over later on.
     * The equivalence of the keys are determined via reference
     * equality and <code>equals()</code> equality.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param <K> the result group element type
     * @param keySelector the key selector
     * @return the new iterable
     * @since 0.96.1
     */
    public <K> IterableBuilder<GroupedIterable<K, T>> groupBy0(
            final Func1<? super T, ? extends K> keySelector) {
        return from(Interactive.groupBy(it, keySelector, Functions.<T>identity()));
    }
    /**
     * Creates an iterable which traverses the source iterable,
     * and based on the key selector, groups values the elements into GroupedIterables,
     * which can be interated over later on.
     * The equivalence of the keys are determined via reference
     * equality and <code>equals()</code> equality.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param <K> the result group element type
     * @param keySelector the key selector
     * @return the new iterable
     * @since 0.96.1
     */
    public <K> IterableBuilder<Pair<K, IterableBuilder<T>>> groupBy(
            final Func1<? super T, ? extends K> keySelector) {
        return groupBy(keySelector, Functions.<T>identity());
    }
    /**
     * Creates an iterable which traverses the source iterable,
     * and based on the key selector, groups values extracted by valueSelector into
     * a pair of Key and IterableBuilder instances,
     * which can be interated over later on.
     * The equivalence of the keys are determined via reference
     * equality and <code>equals()</code> equality.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param <K> the result group element type
     * @param <V> the result group keys
     * @param keySelector the key selector
     * @param valueSelector the value selector
     * @return the new iterable
     * @since 0.96.1
     */
    public <K, V> IterableBuilder<Pair<K, IterableBuilder<V>>> groupBy(
            final Func1<? super T, ? extends K> keySelector,
            final Func1<? super T, ? extends V> valueSelector) {
        return from(Interactive.select(Interactive.groupBy(it, keySelector, valueSelector),
                new Func1<GroupedIterable<K, V>, Pair<K, IterableBuilder<V>>>() {
                    @Override
                    public Pair<K, IterableBuilder<V>> call(
                            GroupedIterable<K, V> param1) {
                        return Pair.of(param1.key(), from(param1));
                    }
                }));
    }
    /**
     * Construct a new iterable which will invoke the specified action
     * before the source value gets relayed through it.
     * Can be used to inject side-effects before returning a value.
     * <p>The returned iterator forwards all <code>remove()</code> calls
     * to the source.</p>
     * @param action the action to invoke before each next() is returned.
     * @return the new iterable
     */
    public IterableBuilder<T> invoke(Action1<? super T> action) {
        return from(Interactive.invoke(it, action));
    }
    /**
     * Returns a single true if the target iterable is empty.
     * @return the new iterable
     */
    public IterableBuilder<Boolean> isEmpty() {
        return from(Interactive.isEmpty(it));
    }
    @Override
    public Iterator<T> iterator() {
        return it.iterator();
    }
    /**
     * Concatenates the source strings one after another and uses the given separator.
     * <p>The returned iterator forwards all <code>remove()</code> calls
     * to the source.</p>
     * @param separator the separator to use
     * @return the new iterable
     */
    public IterableBuilder<String> join(String separator) {
        return from(Interactive.join(it, separator));
    }
    /**
     * Returns the last element of the iterable or throws a <code>NoSuchElementException</code> if the iterable is empty.
     * @return the last value
     */
    public T last() {
        return Interactive.last(it);
    }
    /**
     * Transforms the sequence of the source iterable into an option sequence of
     * Option.some(), Option.none() and Option.error() values, depending on
     * what the source's hasNext() and next() produces.
     * The returned iterator will throw an <code>UnsupportedOperationException</code> for its <code>remove()</code> method.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @return the new iterable
     */
    public IterableBuilder<Notification<T>> materialize() {
        return from(Interactive.materialize(it));
    }
    /**
     * Returns the maximum value of the given iterable source.
     * @param <U> the self comparable type
     * @return the new iterable
     */
    @SuppressWarnings("unchecked")
    public <U extends Comparable<? super U>> IterableBuilder<U> max() {
        return from(Interactive.max((Iterable<U>)it));
    }
    /**
     * Returns the maximum value of the given iterable source in respect to the supplied comparator.
     * @param comparator the comparator to use
     * @return the new iterable
     */
    public IterableBuilder<T> max(Comparator<? super T> comparator) {
        return from(Interactive.max(it, comparator));
    }
    /**
     * Returns an iterator which will produce a single List of the maximum values encountered
     * in the source stream based on the supplied key selector.
     * @param <U> the source element type, which must be self comparable
     * @return the new iterable
     */
    @SuppressWarnings("unchecked")
    public <U extends Comparable<? super U>> IterableBuilder<List<U>> maxBy() {
        return from(Interactive.maxBy((Iterable<U>)it));
    }
    /**
     * Returns an iterator which will produce a single List of the maximum values encountered
     * in the source stream based on the supplied comparator.
     * @param comparator the key comparator
     * @return the new iterable
     */
    public IterableBuilder<List<T>> maxBy(Comparator<? super T> comparator) {
        return from(Interactive.maxBy(it, comparator));
    }
    /**
     * Returns an iterator which will produce a single List of the maximum values encountered
     * in the source stream based on the supplied key selector.
     * @param <U> the key type, which must be self-comparable
     * @param keySelector the selector for keys
     * @return the new iterable
     */
    public <U extends Comparable<? super U>> IterableBuilder<List<T>> mayBy(Func1<? super T, U> keySelector) {
        return from(Interactive.maxBy(it, keySelector));
    }
    /**
     * Returns an iterator which will produce a single List of the minimum values encountered
     * in the source stream based on the supplied key selector and comparator.
     * @param <U> the key type
     * @param keySelector the selector for keys
     * @param keyComparator the key comparator
     * @return the new iterable
     */
    public <U> IterableBuilder<List<T>> mayBy(Func1<? super T, U> keySelector, Comparator<? super U> keyComparator) {
        return from(Interactive.maxBy(it, keySelector, keyComparator));
    }
    /**
     * Enumerates the source iterable once and caches its results.
     * Any iterator party will basically drain this cache, e.g.,
     * reiterating over this iterable will produce no results.
     * Note: the name is not a misspelling, see <a href='http://en.wikipedia.org/wiki/Memoization'>Memoization</a>.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param bufferSize the size of the buffering
     * @return the new iterable
     */
    public IterableBuilder<T> memoize(int bufferSize) {
        return from(Interactive.memoize(it, bufferSize));
    }
    /**
     * The returned iterable ensures that the source iterable is only traversed once, regardless of
     * how many iterator attaches to it and each iterator see only the values.
     * Note: the name is not a misspelling, see <a href='http://en.wikipedia.org/wiki/Memoization'>Memoization</a>.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @return the new iterable
     */
    public IterableBuilder<T> memoizeAll() {
        return from(Interactive.memoizeAll(it));
    }
    /**
     * Returns the maximum value of the given iterable source.
     * @param <U> the self comparable type
     * @return the new iterable
     */
    @SuppressWarnings("unchecked")
    public <U extends Comparable<? super U>> IterableBuilder<U> min() {
        return from(Interactive.min((Iterable<U>)it));
    }
    /**
     * Returns the minimum value of the given iterable source in respect to the supplied comparator.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param comparator the comparator to use
     * @return the new iterable
     */
    public IterableBuilder<T> min(Comparator<? super T> comparator) {
        return from(Interactive.min(it, comparator));
    }
    /**
     * Returns an iterator which will produce a single List of the minimum values encountered
     * in the source stream based on the supplied comparator.
     * @param comparator the key comparator
     * @return the new iterable
     */
    public IterableBuilder<List<T>> minBy(Comparator<? super T> comparator) {
        return from(Interactive.minBy(it, comparator));
    }
    /**
     * Returns an iterator which will produce a single List of the minimum values encountered
     * in the source stream based on the supplied key selector.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param <U> the key type, which must be self-comparable
     * @param keySelector the selector for keys
     * @return the new iterable
     */
    public <U extends Comparable<? super U>> IterableBuilder<List<T>> minBy(Func1<? super T, U> keySelector) {
        return from(Interactive.minBy(it, keySelector));
    }
    /**
     * Returns an iterator which will produce a single List of the minimum values encountered
     * in the source stream based on the supplied key selector and comparator.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param <U> the key type
     * @param keySelector the selector for keys
     * @param keyComparator the key comparator
     * @return the new iterable
     */
    public <U> IterableBuilder<List<T>> minBy(Func1<? super T, U> keySelector, Comparator<? super U> keyComparator) {
        return from(Interactive.minBy(it, keySelector, keyComparator));
    }
    /**
     * Returns an iterator which will produce a single List of the minimum values encountered
     * in the source stream based on the supplied key selector.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param <U> the source element type, which must be self comparable
     * @return the new iterable
     */
    @SuppressWarnings("unchecked")
    public <U extends Comparable<? super U>> IterableBuilder<List<U>> minxBy() {
        return from(Interactive.minBy((Iterable<U>)it));
    }
    /**
     * Returns an iterable which traverses the entire
     * source iterable and creates an ordered list
     * of elements. Once the source iterator completes,
     * the elements are streamed to the output.
     * <p>Note: the element type should be self comparable or a ClassCastException is thrown.</p>
     * @param <U> the source element type, which must be self comparable
     * @return the new iterable
     */
    @SuppressWarnings("unchecked")
    public <U extends Comparable<? super U>> IterableBuilder<U> orderBy() {
        return from(Interactive.orderBy((Iterable<U>)it));
    }
    /**
     * Returns an iterable which traverses the entire
     * source iterable and creates an ordered list
     * of elements. Once the source iterator completes,
     * the elements are streamed to the output.
     * @param comparator the value comparator
     * @return the new iterable
     */
    public IterableBuilder<T> orderBy(Comparator<? super T> comparator) {
        return from(Interactive.orderBy(it, comparator));
    }
    /**
     * Returns an iterable which traverses the entire
     * source iterable and creates an ordered list
     * of elements. Once the source iterator completes,
     * the elements are streamed to the output.
     * @param <U> the key type for the ordering, must be self comparable
     * @param keySelector the key selector for comparison
     * @return the new iterable
     */
    public <U extends Comparable<? super U>> IterableBuilder<T> orderBy(Func1<? super T, ? extends U> keySelector) {
        return from(Interactive.orderBy(it, keySelector));
    }
    /**
     * Returns an iterable which traverses the entire
     * source iterable and creates an ordered list
     * of elements. Once the source iterator completes,
     * the elements are streamed to the output.
     * @param <U> the key type for the ordering
     * @param keySelector the key selector for comparison
     * @param keyComparator the key comparator function
     * @return the new iterable
     */
    public <U> IterableBuilder<T> orderBy(Func1<? super T, ? extends U> keySelector, Comparator<? super U> keyComparator) {
        return from(Interactive.orderBy(it, keySelector, keyComparator));
    }
    /**
     * Applies the <code>func</code> function for a shared instance of the source,
     * e.g., <code>func.invoke(share(source))</code>.
     * @param <U> the return types
     * @param func invoke the function on the buffering iterable and return an iterator over it.
     * @return the new iterable
     */
    public <U> IterableBuilder<U> prune(Func1<? super Iterable<? extends T>, ? extends Iterable<U>> func) {
        return from(Interactive.prune(it, func));
    }
    /**
     * The returned iterable ensures that the source iterable is only traversed once, regardless of
     * how many iterator attaches to it and each iterator see only the same cached values.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for <code>remove()</code> method of its first element, then it might throw for any
     * subsequent element, depending on the source iterable.</p>
     * @param <U> the return types
     * @param func invoke the function on the buffering iterable and return an iterator over it.
     * @param initial the initial value to append to the output stream
     * @return the new iterable
     */
    public <U> IterableBuilder<U> publish(final Func1<? super Iterable<? super T>, ? extends Iterable<? extends U>> func,
            final U initial) {
        return from(Interactive.publish(it, func, initial));
    }
    /**
     * The returned iterable ensures that the source iterable is only traversed once, regardless of
     * how many iterator attaches to it and each iterator see only the values.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param <U> the return types
     * @param func invoke the function on the buffering iterable and return an iterator over it.
     * @return the new iterable
     */
    public <U> IterableBuilder<U> publish(final Func1<? super Iterable<T>, ? extends Iterable<U>> func) {
        return from(Interactive.publish(it, func));
    }
    /**
     * The returned iterable ensures that the source iterable is only traversed once, regardless of
     * how many iterator attaches to it and each iterator may only see one source element.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param <U> the return types
     * @param func invoke the function on the buffering iterable and return an iterator over it.
     * @return the new iterable
     */
    public <U> IterableBuilder<U> replay(final Func1<? super Iterable<T>, ? extends Iterable<U>> func) {
        return from(Interactive.replay(it, func));
    }
    /**
     * The returned iterable ensures that the source iterable is only traversed once, regardless of
     * how many iterator attaches to it and each iterator see only the some cached values.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param <U> the return types
     * @param func invoke the function on the buffering iterable and return an iterator over it.
     * @param bufferSize the buffer size
     * @return the new iterable
     */
    public <U> IterableBuilder<U> replay(final Func1<? super Iterable<T>, ? extends Iterable<U>> func,
            final int bufferSize) {
        return from(Interactive.replay(it, func, bufferSize));
    }
    /**
     * Iterates over the given source without using its returned value.
     * This method is useful when the concrete values from the iterator
     * are not needed but the iteration itself implies some side effects.
     */
    public void run() {
        Interactive.run(it);
    }
    /**
     * Iterate over the source and submit each value to the
     * given action. Basically, a for-each loop with pluggable
     * action.
     * This method is useful when the concrete values from the iterator
     * are not needed but the iteration itself implies some side effects.
     * @param action the action to invoke on with element
     */
    public void run(
            final Action1<? super T> action) {
        Interactive.run(it, action);
    }
    /**
     * Generates an iterable which acts like a running sum when iterating over the source iterable, e.g.,
     * For each element in T, it computes a value by using the current aggregation value and returns it.
     * The first call to the aggregator function will receive a zero for its first argument.
     * @param <U> the destination element type
     * @param aggregator the function which takes the current running aggregation value, the current element and produces a new aggregation value.
     * @return the new iterable
     */
    public <U> IterableBuilder<U> scan(final Func2<? super U, ? super T, ? extends U> aggregator) {
        return from(Interactive.scan(it, aggregator));
    }
    /**
     * Generates an iterable which acts like a running sum when iterating over the source iterable, e.g.,
     * For each element in T, it computes a value by using the current aggregation value and returns it.
     * The first call to the aggregator function will receive a zero for its first argument.
     * <p>The returned iterator forwards all <code>remove()</code> calls
     * to the source.</p>
     * @param <U> the destination element type
     * @param seed the initial value of the running aggregation
     * @param aggregator the function which takes the current running aggregation value, the current element and produces a new aggregation value.
     * @return the new iterable
     */
    public <U> IterableBuilder<U> scan(final U seed,
            final Func2<? super U, ? super T, ? extends U> aggregator) {
        return from(Interactive.scan(it, seed, aggregator));
    }
    /**
     * Creates an iterable which is a transforms the source
     * elements by using the selector function.
     * The function receives the current element.
     * @param <U> the output element type
     * @param selector the selector function
     * @return the new iterable
     */
    public <U> IterableBuilder<U> select(final Func1<? super T, ? extends U> selector) {
        return from(Interactive.select(it, selector));
    }
    /**
     * Creates an iterable which is a transforms the source
     * elements by using the selector function.
     * The function receives the current index and the current element.
     * <p>The returned iterator forwards all <code>remove()</code> calls
     * to the source.</p>
     * @param <U> the output element type
     * @param selector the selector function
     * @return the new iterable
     */
    public <U> IterableBuilder<U> select(final Func2<Integer, ? super T, ? extends U> selector) {
        return from(Interactive.select(it, selector));
    }
    /**
     * Creates an iterable which returns a stream of Us for each source Ts.
     * The iterable stream of Us is returned by the supplied selector function.
     * <p>The returned iterator forwards all <code>remove()</code> calls
     * to the current source (which might not accept it).
     * @param <U> the output element type
     * @param selector the selector for multiple Us for each T
     * @return the new iterable
     */
    public <U> IterableBuilder<U> selectMany(final Func1<? super T, ? extends Iterable<? extends U>> selector) {
        return from(Interactive.selectMany(it, selector));
    }
    /**
     * Returns an iterable which ensures the source iterable is
     * only traversed once and clients may take values from each other,
     * e.g., they share the same iterator.
     * @return the new iterable
     */
    public IterableBuilder<T> share() {
        return from(Interactive.share(it));
    }
    /**
     * Returns an iterable which skips the last <code>num</code> elements from the
     * source iterable.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param num the number of elements to skip at the end
     * @return the new iterable
     */
    public IterableBuilder<T> skipLast(final int num) {
        return from(Interactive.skipLast(it, num));
    }
    /**
     * Returns an iterable which prefixes the source iterable values
     * by a constant.
     * It is equivalent to <code>concat(singleton(value), source)</code>.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method for the first element, and might
     * throw for subsequent elements, depending on the source iterable.</p>
     * @param value the value to prefix
     * @return the new iterable.
     */
    @SuppressWarnings("unchecked")
    public IterableBuilder<T> startWith(T value) {
        return from(Interactive.startWith(it, value));
    }
    /**
     * Computes and signals the sum of the values of the BigDecimal source.
     * The source may not send nulls.
     * @return the observable for the sum value
     */
    
    @SuppressWarnings("unchecked")
    public IterableBuilder<BigDecimal> sumBigDecimal() {
        return from(Interactive.sumBigDecimal((Iterable<BigDecimal>)it));
    }
    /**
     * Computes and signals the sum of the values of the BigInteger source.
     * The source may not send nulls.
     * @return the observable for the sum value
     */
    
    @SuppressWarnings("unchecked")
    public IterableBuilder<BigInteger> sumBigInteger() {
        return from(Interactive.sumBigInteger((Iterable<BigInteger>)it));
    }
    /**
     * Computes and signals the sum of the values of the Double source.
     * The source may not send nulls.
     * @return the observable for the sum value
     */
    
    @SuppressWarnings("unchecked")
    public IterableBuilder<Double> sumDouble() {
        return from(Interactive.sumDouble((Iterable<Double>)it));
    }
    /**
     * Computes and signals the sum of the values of the Float source.
     * The source may not send nulls.
     * @return the observable for the sum value
     */
    
    @SuppressWarnings("unchecked")
    public IterableBuilder<Float> sumFloat() {
        return from(Interactive.sumFloat((Iterable<Float>)it));
    }
    /**
     * Computes and signals the sum of the values of the Integer source.
     * The source may not send nulls. An empty source produces an empty sum
     * @return the observable for the sum value
     */
    
    @SuppressWarnings("unchecked")
    public IterableBuilder<Integer> sumInt() {
        return from(Interactive.sumInt((Iterable<Integer>)it));
    }
    /**
     * Computes and signals the sum of the values of the Integer source by using
     * a double intermediate representation.
     * The source may not send nulls. An empty source produces an empty sum
     * @return the observable for the sum value
     */
    
    @SuppressWarnings("unchecked")
    public IterableBuilder<Double> sumIntAsDouble() {
        return from(Interactive.sumIntAsDouble((Iterable<Integer>)it));
    }
    /**
     * Computes and signals the sum of the values of the Long source.
     * The source may not send nulls.
     * @return the observable for the sum value
     */
    
    @SuppressWarnings("unchecked")
    public IterableBuilder<Long> sumLong() {
        return from(Interactive.sumLong((Iterable<Long>)it));
    }
    /**
     * Computes and signals the sum of the values of the Long sourceby using
     * a double intermediate representation.
     * The source may not send nulls.
     * @return the observable for the sum value
     */
    
    @SuppressWarnings("unchecked")
    public IterableBuilder<Double> sumLongAsDouble() {
        return from(Interactive.sumLongAsDouble((Iterable<Long>)it));
    }
    /**
     * Returns the iterable which returns the first <code>num</code> element.
     * from the source iterable.
     * <p>The returned iterator forwards all <code>remove()</code> calls
     * to the source.</p>
     * @param num the number of items to take
     * @return the new iterable
     */
    public IterableBuilder<T> take(int num) {
        return from(Interactive.take(it, num));
    }
    /**
     * Returns an iterable which takes only the last <code>num</code> elements from the
     * source iterable.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param num the number of elements to skip at the end
     * @return the new iterable
     */
    public IterableBuilder<T> takeLast(int num) {
        return from(Interactive.takeLast(it, num));
    }
    /**
     * Returns an object array of all elements in this
     * iterable.
     * @return the object array
     */
    public Object[] toArray() {
        return toList().toArray();
    }
    /**
     * Returns all elements from this iterable into either
     * the given array or a new array if the size requires.
     * @param a the output array
     * @return the output array
     */
    public T[] toArray(T[] a) {
        return toList().toArray(a);
    }
    
    /**
     * Iterates over and returns all elements in a list.
     * @return the list of the values from this iterable
     */
    public List<T> toList() {
        List<T> result = new ArrayList<T>();
        into(result);
        return result;
    }
    /**
     * Converts this iterable into an observable builder
     * which uses the default scheduler of {@link hu.akarnokd.reactive4java.reactive.Reactive} to emit values.
     * @return the observable builder
     */
    public Observable<T> toObservable() {
        return Observable.from(it);
    }
    /**
     * Converts this iterable into an observable builder
     * which uses the supplied Scheduler to emit values.
     * @param scheduler the scheduler
     * @return the observable builder
     */
    public Observable<T> toObservable(Scheduler scheduler) {
        return Observable.from(it, scheduler);
    }
    /**
     * Creates an iterable which filters this iterable with the
     * given predicate factory function. The predicate returned by the factory receives an index
     * telling how many elements were processed thus far.
     * Use this construct if you want to use some memorizing predicat function (e.g., filter by subsequent distinct, filter by first occurrences only)
     * which need to be invoked per iterator() basis.
     * <p>The returned iterator forwards all <code>remove()</code> calls
     * to the source.</p>
     * @param filter the predicate function
     * @return the new iterable
     */
    public IterableBuilder<T> where(final Func1<? super T, Boolean> filter) {
        return from(Interactive.where(it, filter));
    }
    /**
     * Creates an iterable which filters this iterable with the
     * given predicate factory function. The predicate returned by the factory receives an index
     * telling how many elements were processed thus far.
     * @param predicate the predicate
     * @return the new iterable
     */
    public IterableBuilder<T> where(final Func2<Integer, ? super T, Boolean> predicate) {
        return from(Interactive.where(it, predicate));
    }
    /**
     * Pairs each element from this and the oher iterable source and
     * combines them into a new value by using the <code>combiner</code>
     * function.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param <U> the right source type
     * @param <V> the result type
     * @param right the right source
     * @param combiner the combiner function
     * @return the new iterable
     */
    public <U, V> IterableBuilder<V> zip(final Iterable<? extends U> right,
            final Func2<? super T, ? super U, ? extends V> combiner) {
        return from(Interactive.zip(it, right, combiner));
    }
    /**
     * Runs this iterable and prints the values.
     * <p>Is the same as using {@code this.run(Interactive.print())}.</p>
     */
    public void print() {
        Interactive.run(it, Interactive.print());
    }
    /**
     * Runs this iterable and prints the values.
     * <p>Is the same as using {@code this.run(Interactive.println())}.</p>
     */
    public void println() {
        Interactive.run(it, Interactive.println());
    }
    /**
     * Convert the iterable values into a map representation.
     * <p>If an element maps to the same key, the existing value will be overwritten.</p>
     * @param <K> the key type
     * @param <V> the value type
     * @param keySelector the function to extract a key from an element
     * @param valueSelector the function to extract a value from an element
     * @param mapProvider the map provider
     * @return the filled-in map.
     * @since 0.96.1
     */
    public <K, V> Map<K, V> toMap(
            Func1<? super T, ? extends K> keySelector,
            Func1<? super T, ? extends V> valueSelector,
            Func0<? extends Map<K, V>> mapProvider) {
        Map<K, V> map = mapProvider.call();
        Iterator<T> it = iterator();
        try {
            while (it.hasNext()) {
                T t = it.next();
                K key = keySelector.call(t);
                V value = valueSelector.call(t);
                map.put(key, value);
            }
        } finally {
            Interactive.unsubscribe(it);
        }
        return map;
    }
    /**
     * Convert the iterable values into a map representation.
     * <p>If an element maps to the same key, the existing value will be overwritten.</p>
     * <p>See Functions.hashMapProvider() and others for some standard map implementations.</p>
     * @param <K> the key type
     * @param keySelector the function to extract a key from an element
     * @param mapProvider the map provider
     * @return the filled-in map.
     * @since 0.96.1
     */
    public <K> Map<K, T> toMap(
            Func1<? super T, ? extends K> keySelector,
            Func0<? extends Map<K, T>> mapProvider) {
        return toMap(keySelector, Functions.<T>identity(), mapProvider);
    }
    /**
     * Convert the values into a multimap representation where each
     * key can have multiple values.
     * <p>See Functions.hashMapProvider(), Functions.arrayListProvider()
     * and others for some standard map implementations.</p>
     * @param <K> the key type
     * @param <V> the value type
     * @param <C> the collection type
     * @param keySelector the key selector
     * @param valueSelector the value selector
     * @param mapProvider the provider for the base map
     * @param collectionProvider the provider for the value collection
     * @return the multimap
     * @since 0.96.1
     */
    public <K, V, C extends Collection<V>> Map<K, C> toMultimap(
            Func1<? super T, ? extends K> keySelector,
            Func1<? super T, ? extends V> valueSelector,
            Func0<? extends Map<K, C>> mapProvider,
            Func0<? extends C> collectionProvider) {
        Map<K, C> result = mapProvider.call();
        Iterator<T> it = iterator();
        try {
            while (it.hasNext()) {
                T t = it.next();
                K key = keySelector.call(t);
                V value = valueSelector.call(t);
                
                C coll = result.get(key);
                if (coll == null) {
                    coll = collectionProvider.call();
                    result.put(key, coll);
                }
                coll.add(value);
            }
        } finally {
            Interactive.unsubscribe(it);
        }
        return result;
    }
    /**
     * Convinience method to create a hashmap from the elements.
     * @param <K> the key type
     * @param keySelector the key selector
     * @return the map
     * @since 0.96.1
     */
    public <K> Map<K, T> toHashMap(Func1<? super T, ? extends K> keySelector) {
        return toMap(keySelector, Functions.<T>identity(), IxHelperFunctions.<K, T>hashMapProvider());
    }
    /**
     * Convinience method to create a hash-multimap with list from the elements.
     * @param <K> the key type
     * @param keySelector the key selector
     * @return the multimap
     * @since 0.96.1
     */
    public <K> Map<K, List<T>> toHashMultimap(Func1<? super T, ? extends K> keySelector) {
        return toMultimap(
                keySelector,
                Functions.<T>identity(),
                IxHelperFunctions.<K, List<T>>hashMapProvider(),
                IxHelperFunctions.<T>arrayListProvider());
    }
    /**
     * Returns each pair of subsequent elements as pairs.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @return the iterable builder
     * @since 0.96.1
     */
    public IterableBuilder<Pair<T, T>> subsequent() {
        return from(Interactive.subsequent(it));
    }
    /**
     * Returns each pair of subsequent elements as pairs.
     * <p>The returned iterator will throw an <code>UnsupportedOperationException</code>
     * for its <code>remove()</code> method.</p>
     * @param count the number of subsequent elements
     * @return the iterable builder
     * @since 0.96.1
     */
    public IterableBuilder<IterableBuilder<T>> subsequent(int count) {
        return from(Interactive.subsequent(it, count))
                .select(IterableBuilder.<T>toBuilder());
    }
    /**
     * @param <T> the element type
     * @return a function which wraps its iterable parameter into an iterablebuilder instance
     * @since 0.96.1
     */
    public static <T> Func1<Iterable<T>, IterableBuilder<T>> toBuilder() {
        return new Func1<Iterable<T>, IterableBuilder<T>>() {
            @Override
            public IterableBuilder<T> call(Iterable<T> param1) {
                return from(param1);
            }
        };
    }
    /**
     * Add the elements of the sequence into the supplied collection.
     * @param <U> a collection type
     * @param out the output collection
     * @return the same out value
     * @since 0.97
     */
    public <U extends Collection<? super T>> U into(U out) {
        Iterator<T> it = iterator();
        try {
            while (it.hasNext()) {
                out.add(it.next());
            }
        } finally {
            Interactive.unsubscribe(it);
        }
        return out;
    }
    /**
     * Consumes the sequence and removes all items via the Iterator.remove().
     * @since 0.97
     */
    public void removeAll() {
        Iterator<T> it = iterator();
        try {
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        } finally {
            Interactive.unsubscribe(it);
        }
    }
}
