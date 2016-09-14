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

public class SequenceEqualTest {

    @Test
    public void normal() {
        Ix<Boolean> source = Ix.range(1, 5).sequenceEqual(Ix.range(1, 5));

        IxTestHelper.assertValues(source, true);
    }

    @Test
    public void firstSorter() {
        Ix<Boolean> source = Ix.range(1, 4).sequenceEqual(Ix.range(1, 5));

        IxTestHelper.assertValues(source, false);
    }

    @Test
    public void secondSorter() {
        Ix<Boolean> source = Ix.range(1, 5).sequenceEqual(Ix.range(1, 4));

        IxTestHelper.assertValues(source, false);
    }

    @Test
    public void firstEmpty() {
        Ix<Boolean> source = Ix.<Integer>empty().sequenceEqual(Ix.range(1, 5));

        IxTestHelper.assertValues(source, false);
    }

    @Test
    public void secondEmpty() {
        Ix<Boolean> source = Ix.range(1, 5).sequenceEqual(Ix.<Integer>empty());

        IxTestHelper.assertValues(source, false);
    }
    @Test
    public void empty() {
        Ix<Boolean> source = Ix.empty().sequenceEqual(Ix.empty());

        IxTestHelper.assertValues(source, true);
    }

    @Test
    public void different() {
        Ix<Boolean> source = Ix.fromArray(1, 2, 3, 3, 5).sequenceEqual(Ix.range(1, 5));

        IxTestHelper.assertValues(source, false);
    }
}
