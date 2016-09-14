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

import java.util.Iterator;

final class IxReplaySize<T> extends IxSource<T, T> {

    final int maxSize;

    Iterator<T> it;

    Node<T> head;

    Node<T> tail;

    int size;

    IxReplaySize(Iterable<T> source, int maxSize) {
        super(source);
        this.maxSize = maxSize;
        this.head = this.tail = new Node<T>(null);
    }

    @Override
    public Iterator<T> iterator() {
        if (it == null) {
            it = source.iterator();
        }
        return new ReplaySizeIterator<T>(this, head);
    }

    boolean moveNext() {
        if (!it.hasNext()) {
            return false;
        }

        Node<T> n = new Node<T>(it.next());
        tail.next = n;
        tail = n;

        int s = size;
        if (s != maxSize) {
            size = s + 1;
        } else {
            head = head.next;
        }
        return true;
    }

    static final class Node<T> {
        final T value;

        Node<T> next;

        Node(T value) {
            this.value = value;
        }
    }

    static final class ReplaySizeIterator<T> extends IxBaseIterator<T> {
        final IxReplaySize<T> parent;

        Node<T> node;

        ReplaySizeIterator(IxReplaySize<T> parent, Node<T> node) {
            this.parent = parent;
            this.node = node;
        }

        @Override
        protected boolean moveNext() {
            Node<T> n = node;

            if (n.next == null) {
                if (!parent.moveNext()) {
                    done = true;
                    return false;
                }
            }
            n = n.next;

            value = n.value;
            hasValue = true;
            node = n;

            return true;
        }
    }
}
