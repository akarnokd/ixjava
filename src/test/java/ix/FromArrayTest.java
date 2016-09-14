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

import org.junit.*;

public class FromArrayTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.fromArray(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void range() {
        Ix<Integer> source = Ix.fromArrayRange(1, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        IxTestHelper.assertValues(source, 2, 3, 4, 5, 6, 7, 8);
    }

    @Test
    public void empty() {
        Ix<Integer> source = Ix.fromArray();

        Assert.assertSame(source.getClass().toString(), source, Ix.empty());
    }

    @Test
    public void just() {
        Ix<Integer> source = Ix.fromArray(1);

        Assert.assertTrue(source.getClass().toString(), source instanceof IxScalarCallable);
    }

    @Test
    public void rangeChecks() {
        try {
            Ix.fromArrayRange(-1, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            Assert.fail("Failed to throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            Assert.assertEquals("start=-1, end=1, length=10", ex.getMessage());
        }

        try {
            Ix.fromArrayRange(1, -1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            Assert.fail("Failed to throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            Assert.assertEquals("start=1, end=-1, length=10", ex.getMessage());
        }

        try {
            Ix.fromArrayRange(12, -1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            Assert.fail("Failed to throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            Assert.assertEquals("start=12, end=-1, length=10", ex.getMessage());
        }

        try {
            Ix.fromArrayRange(1, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            Assert.fail("Failed to throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            Assert.assertEquals("start=1, end=12, length=10", ex.getMessage());
        }

        try {
            Ix.fromArrayRange(12, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            Assert.fail("Failed to throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            Assert.assertEquals("start=12, end=12, length=10", ex.getMessage());
        }

    }
}
