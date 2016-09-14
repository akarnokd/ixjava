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

package ix;

import java.util.Iterator;

/**
 * Functional interface that allows transforming an
 * upstream Iterator for each downstream next() call.
 * <p>
 * If you need per consumer-state, wrap the parent operator
 * with compose():
 * <pre><code>
 * Ix.range(1, 10).compose(o -&gt; {
 *     int[] counter = { 0 };
 *     return o.transform((it, c) -&gt; {
 *         if (it.hasNext()) {
 *             it.next();
 *             c.call(++counter[0]);
 *             return NEXT;
 *         }
 *         return STOP;
 *     });
 * });
 * </code></pre>
 * @param <T> the source value type
 * @param <R> the result value type
 */
public interface IxTransform<T, R> {

    int STOP = 0;

    int NEXT = 1;

    int LAST = 2;

    int moveNext(Iterator<T> it, IxConsumer<? super R> consumer);
}
