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

import org.junit.Assert;

public enum IxTestHelper {
    ;

    static String classOf(Object o) {
        return o != null ? o.getClass().getSimpleName() : "null";
    }

    /**
     * Asserts that the iterable produces the specified array of values and
     * verifies if it honors the iterator contract.
     * @param <T> the value type
     * @param source the source sequence to validate
     * @param values the values expected
     */
    public static <T> void assertValues(Iterable<T> source, T... values) {
        Iterator<T> a = source.iterator();
        int i = 0;

        for (;;) {
            boolean b1 = a.hasNext();
            boolean b2 = a.hasNext();

            Assert.assertEquals("Inconsistent hasNext()", b1, b2);

            if (!b1) {
                break;
            }

            if (i == values.length) {
                throw new AssertionError("The source is longer than " + values.length);
            }

            T t = a.next();

            if (t instanceof Object[]) {
                Assert.assertArrayEquals((Object[])values[i], (Object[])t);
            } else {
            Assert.assertEquals("index=" + i + ", expected class="
                    + classOf(values[i])
                    + ", actual class="
                    + classOf(t), values[i], t);
            }
            i++;
        }

        if (i != values.length) {
            throw new AssertionError("The source is shorter than " + values.length + ": " + i);
        }

        try {
            a.next();
            Assert.fail("The next() should have thrown a NoSuchElementException");
        } catch (NoSuchElementException ex) {
            // expected
        }
    }

    /**
     * Assert that calling remove() on the source's Iterator throws
     * an UnsupportedOperationException.
     * @param source the source to validate
     */
    public static void assertNoRemove(Iterable<?> source) {
        Iterator<?> it = source.iterator();

        if (it.hasNext()) {
            it.next();
            try {
                it.remove();
                Assert.fail("Should have thrown UnsupportedOperationException");
            } catch (UnsupportedOperationException ex) {
                // expected
            }
        }
    }

    public static List<Integer> range(int start, int count) {
        return Ix.range(start, count).collectToList().first();
    }
}
