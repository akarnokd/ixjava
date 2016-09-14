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

final class IxGroupBy<T, K, V> extends IxSource<T, GroupedIx<K, V>> {

    static final Object NULL = new Object();

    final IxFunction<? super T, ? extends K> keySelector;

    final IxFunction<? super T, ? extends V> valueSelector;

    IxGroupBy(Iterable<T> source, IxFunction<? super T, ? extends K> keySelector,
            IxFunction<? super T, ? extends V> valueSelector) {
        super(source);
        this.keySelector = keySelector;
        this.valueSelector = valueSelector;
    }

    @Override
    public Iterator<GroupedIx<K, V>> iterator() {
        return new GroupByIterator<T, K, V>(source.iterator(), keySelector, valueSelector);
    }

    static final class GroupByIterator<T, K, V> extends IxSourceIterator<T, GroupedIx<K, V>> {
        final IxFunction<? super T, ? extends K> keySelector;

        final IxFunction<? super T, ? extends V> valueSelector;

        final Map<K, GroupedIterable<K, V>> groups;

        final Queue<GroupedIterable<K, V>> queue;

        GroupByIterator(Iterator<T> it, IxFunction<? super T, ? extends K> keySelector,
                IxFunction<? super T, ? extends V> valueSelector) {
            super(it);
            this.keySelector = keySelector;
            this.valueSelector = valueSelector;
            this.groups = new HashMap<K, GroupedIterable<K, V>>();
            this.queue = new ArrayDeque<GroupedIterable<K, V>>();
        }

        @Override
        protected boolean moveNext() {
            GroupedIterable<K, V> g = queue.poll();
            if (g != null) {
                value = g;
                hasValue = true;
                return true;
            }
            if (mainMoveNext()) {
                g = queue.poll();
                value = g;
                hasValue = true;
                return true;
            }
            done = true;
            return false;
        }

        boolean mainMoveNext() {
            for (;;) {
                if (!it.hasNext()) {
                    groups.clear();
                    return false;
                }

                T v = it.next();

                K key = keySelector.apply(v);
                V val = valueSelector.apply(v);

                GroupedIterable<K, V> g = groups.get(key);
                if (g == null) {
                    g = new GroupedIterable<K, V>(key, this);
                    groups.put(key, g);
                    g.iterator.queue.offer(val != null ? val : NULL);

                    queue.offer(g);
                    return true;
                } else {
                    g.iterator.queue.offer(val != null ? val : NULL);
                }
            }
        }

        boolean groupMoveNext(GroupByGroupIterator<K, V> groupIterator) {
            if (done) {
                return false;
            }
            for (;;) {
                if (!it.hasNext()) {
                    groups.clear();
                    return false;
                }

                T v = it.next();

                K key = keySelector.apply(v);
                V val = valueSelector.apply(v);

                GroupedIterable<K, V> g = groups.get(key);
                if (g == null) {
                    g = new GroupedIterable<K, V>(key, this);
                    groups.put(key, g);
                    g.iterator.queue.offer(val != null ? val : NULL);
                    queue.offer(g);
                } else {
                    g.iterator.queue.offer(val != null ? val : NULL);
                    if (EqualityHelper.INSTANCE.test(key, groupIterator.key)) {
                        return true;
                    }
                }
            }
        }
    }

    static final class GroupedIterable<K, V> extends GroupedIx<K, V> {

        final GroupByGroupIterator<K, V> iterator;

        boolean once;

        GroupedIterable(K key, GroupByIterator<?, K, V> parent) {
            super(key);
            this.iterator = new GroupByGroupIterator<K, V>(parent, key);
        }

        @Override
        public Iterator<V> iterator() {
            if (!once) {
                once = true;
                return iterator;
            }
            throw new IllegalStateException("This GroupedIx iterable can be consumed only once.");
        }

        @Override
        public String toString() {
            return "GroupedIterable[key=" + iterator.key + ", queue=" + iterator.queue.size() + "]";
        }
    }

    static final class GroupByGroupIterator<K, V> extends IxBaseIterator<V> {
        final GroupByIterator<?, K, V> parent;

        final K key;

        final ArrayDeque<Object> queue;

        GroupByGroupIterator(GroupByIterator<?, K, V> parent, K key) {
            this.parent = parent;
            this.key = key;
            this.queue = new ArrayDeque<Object>();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected boolean moveNext() {
            Object o = queue.poll();
            if (o != null) {
                value = o == NULL ? null : (V)o;
                hasValue = true;
                return true;
            }
            if (parent.groupMoveNext(this)) {
                o = queue.poll();
                value = o == NULL ? null : (V)o;
                hasValue = true;
                return true;
            }
            done = true;
            return false;
        }
    }
}
