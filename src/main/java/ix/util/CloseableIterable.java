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


/**
 * An iterable type which returns iterators that support a close() method.
 * @author akarnokd, 2011.10.26.
 * @param <T> the iterator element type
 */
public interface CloseableIterable<T> extends Iterable<T> {
	@Override
	CloseableIterator<T> iterator();
}
