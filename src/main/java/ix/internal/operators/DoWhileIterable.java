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

import rx.functions.Func0;

public final class DoWhileIterable<T> implements Iterable<T> {
	private final Iterable<? extends T> source;
	private final Func0<Boolean> gate;

	public DoWhileIterable(Iterable<? extends T> source, Func0<Boolean> gate) {
		this.source = source;
		this.gate = gate;
	}

	@Override
	public Iterator<T> iterator() {
	    return new Iterator<T>() {
	        /** is this the first pass? */
	        Iterator<? extends T> it = source.iterator();
	        @Override
	        public boolean hasNext() {
	            while (true) {
	                if (it.hasNext()) {
	                    return true;
	                }
	                if (gate.call()) {
	                    it = source.iterator();
	                } else {
	                    break;
	                }
	            }
	            return false;
	        }
	        @Override
	        public T next() {
	            if (hasNext()) {
	                return it.next();
	            }
	            throw new NoSuchElementException();
	        }
	        
	        @Override
	        public void remove() {
	            it.remove();
	        }
	    };
	}
}