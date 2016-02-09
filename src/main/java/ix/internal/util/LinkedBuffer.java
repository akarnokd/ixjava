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
package ix.internal.util;

/**
 * A linked buffer, which can be only filled and queried.
 * @param <T> the element type
 */
public final class LinkedBuffer<T> {
    /** The node. */
    public static class N<T> {
        /** The element value. */
        public T value;
        /** The next node. */
        public LinkedBuffer.N<T> next;
    }
    /** The head pointer. */
    public final LinkedBuffer.N<T> head = new LinkedBuffer.N<T>();
    /** The tail pointer. */
    public LinkedBuffer.N<T> tail = head;
    /** The size. */
    public int size;
    /**
     * Add a new value.
     * @param value the new value
     */
    public void add(T value) {
        LinkedBuffer.N<T> n = new LinkedBuffer.N<T>();
        n.value = value;
        tail.next = n;
        tail = n;
        size++;
    }
}