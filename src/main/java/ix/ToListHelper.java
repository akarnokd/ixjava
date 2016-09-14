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

import java.util.*;

enum ToListHelper implements IxSupplier<List<Object>>, IxConsumer2<List<Object>, Object>, IxFunction<List<Object>, Object[]> {
    INSTANCE;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> IxSupplier<List<T>> initialFactory() {
        return (IxSupplier)INSTANCE;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> IxConsumer2<List<T>, T> collector() {
        return (IxConsumer2)INSTANCE;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> IxFunction<List<T>, Object[]> toArray() {
        return (IxFunction)INSTANCE;
    }
    @Override
    public void accept(List<Object> t1, Object t2) {
        t1.add(t2);
    }

    @Override
    public List<Object> get() {
        return new ArrayList<Object>();
    }

    @Override
    public Object[] apply(List<Object> t) {
        return t.toArray();
    }

}
