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

import rx.functions.Action1;

public final class DoOnNextIterable<T> implements Iterable<T> {
	private final Iterable<? extends T> source;
	private final Action1<? super T> action;

	public DoOnNextIterable(Iterable<? extends T> source,
			Action1<? super T> action) {
		this.source = source;
		this.action = action;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** The source iterator. */
	        final Iterator<? extends T> it = source.iterator();
	        @Override
	        public boolean hasNext() {
	            return it.hasNext();
	        }
	        @Override
	        public T next() {
	            T value = it.next();
	            action.call(value);
	            return value;
	        }
	        @Override
	        public void remove() {
	            it.remove();
	        }
	    };
	}
}