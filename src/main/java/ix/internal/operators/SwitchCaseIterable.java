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
import java.util.Map;

import rx.functions.Func0;

public final class SwitchCaseIterable<U, T> implements Iterable<U> {
	private final Func0<T> selector;
	private final Map<T, Iterable<U>> options;

	public SwitchCaseIterable(Func0<T> selector, Map<T, Iterable<U>> options) {
		this.selector = selector;
		this.options = options;
	}

	@Override
	public Iterator<U> iterator() {
	    Iterable<U> it = options.get(selector.call());
	    return it != null ? it.iterator() : Interactive.<U>empty().iterator();
	}
}