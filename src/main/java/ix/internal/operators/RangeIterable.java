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
package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class RangeIterable implements Iterable<Integer> {
	private final int start;
	private final int count;

	public RangeIterable(int start, int count) {
		this.start = start;
		this.count = count;
	}

	@Override
	public Iterator<Integer> iterator() {
	    return new Iterator<Integer>() {
	        int current = start;
	        @Override
	        public boolean hasNext() {
	            return current < start + count;
	        }
	        @Override
	        public Integer next() {
	            if (hasNext()) {
	                return current++;
	            }
	            throw new NoSuchElementException();
	        }
	        @Override
	        public void remove() {
	            throw new UnsupportedOperationException();
	        }
	    };
	}
}