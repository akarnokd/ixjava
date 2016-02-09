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

import rx.functions.Func1;

public final class ShareSelectorIterable<T, U> implements Iterable<U> {
	/** The source sequence. */
	private final Iterable<T> source;
	private final Func1<? super Iterable<T>, ? extends Iterable<U>> selector;

	public ShareSelectorIterable(Iterable<T> source,
			Func1<? super Iterable<T>, ? extends Iterable<U>> selector) {
		this.source = source;
		this.selector = selector;
	}

	@Override
	public Iterator<U> iterator() {
	    return selector.call(Interactive.share(source)).iterator();
	}
}