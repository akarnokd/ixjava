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

import org.junit.Test;

public class UnionTest {

    @Test
    public void distinct() {
        Ix<Integer> source = Ix.range(1, 5).union(Ix.range(6, 5));

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void overlap() {
        Ix<Integer> source = Ix.range(1, 5).union(Ix.range(3, 5));

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void overlapReverse() {
        Ix<Integer> source = Ix.range(3, 5).union(Ix.range(1, 5));

        IxTestHelper.assertValues(source, 3, 4, 5, 6, 7, 1, 2);

        IxTestHelper.assertNoRemove(source);
    }


    @Test
    public void same() {
        Ix<Integer> source = Ix.range(1, 5).union(Ix.range(1, 5));

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void firstInSecond() {
        Ix<Integer> source = Ix.range(1, 3).union(Ix.range(1, 5));

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void secondInFirst() {
        Ix<Integer> source = Ix.range(1, 5).union(Ix.range(1, 3));

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void firstEmpty() {
        Ix<Integer> source = Ix.<Integer>empty().union(Ix.range(1, 5));

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void secondEmpty() {
        Ix<Integer> source = Ix.range(1, 5).union(Ix.<Integer>empty());

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void bothEmpty() {
        Ix<Integer> source = Ix.<Integer>empty().union(Ix.<Integer>empty());

        IxTestHelper.assertValues(source);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void duplicates() {
        Ix<Integer> source = Ix.fromArray(1, 2, 2, 1, 4, 5).union(Ix.fromArray(2, 3, 1, 5, 4));

        IxTestHelper.assertValues(source, 1, 2, 4, 5, 3);

        IxTestHelper.assertNoRemove(source);
    }
}
