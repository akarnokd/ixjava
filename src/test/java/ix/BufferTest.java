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

import org.junit.Test;

public class BufferTest {

    @SuppressWarnings("unchecked")
    @Test
    public void normal() {
        Ix<List<Integer>> source = Ix.range(1, 5).buffer(2);

        IxTestHelper.assertValues(source, Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5));

        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void normalExactLength() {
        Ix<List<Integer>> source = Ix.range(1, 6).buffer(2);

        IxTestHelper.assertValues(source, Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5, 6));

        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void normalSameSkip() {
        Ix<List<Integer>> source = Ix.range(1, 5).buffer(2, 2);

        IxTestHelper.assertValues(source, Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5));

        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void normalSameSkipExactLength() {
        Ix<List<Integer>> source = Ix.range(1, 6).buffer(2, 2);

        IxTestHelper.assertValues(source, Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5, 6));

        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void normalSkip() {
        Ix<List<Integer>> source = Ix.range(1, 5).buffer(2, 3);

        IxTestHelper.assertValues(source, Arrays.asList(1, 2), Arrays.asList(4, 5));

        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void normalSkipShorter() {
        Ix<List<Integer>> source = Ix.range(1, 4).buffer(2, 3);

        IxTestHelper.assertValues(source, Arrays.asList(1, 2), Arrays.asList(4));

        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void normalSkipShortest() {
        Ix<List<Integer>> source = Ix.range(1, 3).buffer(2, 3);

        IxTestHelper.assertValues(source, Arrays.asList(1, 2));

        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void normalOverlap() {
        Ix<List<Integer>> source = Ix.range(1, 5).buffer(2, 1);

        IxTestHelper.assertValues(source,
                Arrays.asList(1, 2),
                Arrays.asList(2, 3),
                Arrays.asList(3, 4),
                Arrays.asList(4, 5),
                Arrays.asList(5));

        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void normalOverlapLonger() {
        Ix<List<Integer>> source = Ix.range(1, 10).buffer(3, 1);

        IxTestHelper.assertValues(source,
                Arrays.asList(1, 2, 3),
                Arrays.asList(2, 3, 4),
                Arrays.asList(3, 4, 5),
                Arrays.asList(4, 5, 6),
                Arrays.asList(5, 6, 7),
                Arrays.asList(6, 7, 8),
                Arrays.asList(7, 8, 9),
                Arrays.asList(8, 9, 10),
                Arrays.asList(9, 10),
                Arrays.asList(10)
            );

        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void normalOverlapSorter() {
        Ix<List<Integer>> source = Ix.range(1, 2).buffer(3, 2);

        IxTestHelper.assertValues(source,
                Arrays.asList(1, 2)
            );

        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void normalOverlapEmpty() {
        Ix<List<Integer>> source = Ix.<Integer>empty().buffer(3, 2);

        IxTestHelper.assertValues(source);

        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void normalOverlapJust() {
        Ix<List<Integer>> source = Ix.just(1).buffer(3, 2);

        IxTestHelper.assertValues(source, Arrays.asList(1));

        IxTestHelper.assertNoRemove(source);
    }
}
