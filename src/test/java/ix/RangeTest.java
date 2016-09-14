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

import org.junit.*;

public class RangeTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.range(1, 10);

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        IxTestHelper.assertNoRemove(source);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void cantRemove() {
        Iterator<Integer> it = Ix.range(1, 10).iterator();
        it.next();
        it.remove();
    }

    @Test
    public void empty() {
        Ix<Integer> source = Ix.range(1, 0);

        Assert.assertSame(Ix.empty(), source);
    }

    @Test
    public void just() {
        Ix<Integer> source = Ix.range(1, 1);

        Assert.assertTrue(source.getClass().toString(), source instanceof IxScalarCallable);
    }

    @Test
    public void negativeRange() {
        try {
            Ix.range(1, -99);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("count >= 0 required but it was -99", ex.getMessage());
        }
    }
}
