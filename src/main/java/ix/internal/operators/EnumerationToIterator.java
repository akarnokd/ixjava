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

import ix.Enumerator;
import ix.internal.util.SingleContainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import rx.Notification;

public final class EnumerationToIterator<T> implements Iterator<T> {
	/** The source enumerator. */
	private final Enumerator<? extends T> en;
	/** The peek-ahead buffer. */
	final SingleContainer<Notification<? extends T>> peek = new SingleContainer<Notification<? extends T>>();
	/** Completion indicator. */
	boolean done;

	public EnumerationToIterator(Enumerator<? extends T> en) {
		this.en = en;
	}

	@Override
	public boolean hasNext() {
	    if (!done && peek.isEmpty()) {
	        try {
	            if (en.next()) {
	                peek.add(Interactive.some(en.current()));
	            } else {
	                done = true;
	            }
	        } catch (Throwable t) {
	            done = true;
	            peek.add(Interactive.<T>err(t));
	        }
	    }
	    return peek.isEmpty();
	}

	@Override
	public T next() {
	    if (hasNext()) {
	        return Interactive.value(peek.take());
	    }
	    throw new NoSuchElementException();
	}

	@Override
	public void remove() {
	    throw new UnsupportedOperationException();
	}
}