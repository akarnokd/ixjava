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
package ix.internal.operators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ConcatIterable<T> implements Iterable<T> {
	/** The source sequences. */
	private final Iterable<? extends Iterable<? extends T>> sources;

	public ConcatIterable(Iterable<? extends Iterable<? extends T>> sources) {
		this.sources = sources;
	}

	@Override
	public Iterator<T> iterator() {
	    final Iterator<? extends Iterable<? extends T>> si = sources.iterator();
	    if (si.hasNext()) {
	        return new Iterator<T>() {
	            /** The current iterable. */
	            Iterator<? extends T> iter = si.next().iterator();
	            /** Save the last iterator since hasNext might run forward into other iterators. */
	            Iterator<? extends T> itForRemove;
	            @Override
	            public boolean hasNext() {
	                while (!iter.hasNext()) {
	                    if (!si.hasNext()) {
	                        return false;
	                    }
	                    iter = si.next().iterator();
	                }
	                return true;
	            }
	            
	            @Override
	            public T next() {
	                if (!hasNext()) {
	                    throw new NoSuchElementException();
	                }
	                itForRemove = iter;
	                return iter.next();
	            }
	            
	            @Override
	            public void remove() {
	                if (itForRemove == null) {
	                    throw new IllegalStateException();
	                }
	                itForRemove.remove();
	                itForRemove = null;
	            }
	        };
	    }
	    return Interactive.<T>empty().iterator();
	}
}