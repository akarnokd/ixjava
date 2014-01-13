/*
 * Copyright 2011-2013 David Karnok
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

package ix.util;

import java.util.NoSuchElementException;

/**
 * A simple circular buffer with absolute indices.
 * @author akarnokd, 2011.02.04.
 * @param <T> the contained element type
 */
public class CircularBuffer<T> {
	/** The buffer. */
	final Object[] buffer;
	/** The head pointer. */
	int head;
	/** The tail pointer. */
	int tail;
	/**
	 * Construct a new circular buffer.
	 * @param size the buffer size
	 */
	public CircularBuffer(int size) {
		buffer = new Object[size];
	}
	/**
	 * Add a new value to the buffer.
	 * If the buffer would overflow, it automatically removes the current head element.
	 * @param value the value
	 */
	public void add(T value) {
		buffer[(tail++) % buffer.length] = value;
		if (size() > buffer.length) {
			head++;
		}
	}
	/**
	 * Retrieve a buffer element based on an absolute index.
	 * @param index the absolute index
	 * @return the value
	 */
	@SuppressWarnings("unchecked")
	public T get(int index) {
		if (index < head) {
			throw new IllegalArgumentException("read before head");
		}
		if (index >= tail) {
			throw new IllegalArgumentException("read after tail");
		}
		return (T)buffer[index % buffer.length];
	}
	/**
	 * @return Takes the head of the buffer.
	 */
	@SuppressWarnings("unchecked")
	public T take() {
		if (tail == head) {
			throw new NoSuchElementException();
		}
		int idx = head++ % buffer.length;
		T value = (T)buffer[idx];
		buffer[idx] = null;
		return value; 
	}
	/** @return is the buffer empty? */
	public boolean isEmpty() {
		return head == tail;
	}
	/** @return the current size of the buffer. */
	public int size() {
		return tail - head;
	}
	/** @return the current head index. */
	public int head() {
		return head;
	}
	/** @return the current tail index. */
	public int tail() {
		return tail;
	}
}
