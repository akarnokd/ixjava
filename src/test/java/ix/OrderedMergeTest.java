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

import java.util.Comparator;

import org.junit.Test;

public class OrderedMergeTest {

    @Test
    public void normalArray() {
        @SuppressWarnings("unchecked")
        Ix<Integer> source = Ix.orderedMergeArray(Ix.fromArray(1, 3), Ix.fromArray(2, 4, 5));

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalIterable() {
        @SuppressWarnings("unchecked")
        Ix<Integer> source = Ix.orderedMerge(Ix.fromArray(Ix.fromArray(1, 3), Ix.fromArray(2, 4, 5)));

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
        IxTestHelper.assertNoRemove(source);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void firstEmptyArray() {
        Ix<Integer> source = Ix.orderedMergeArray(Ix.<Integer>empty(), Ix.fromArray(2, 4, 5));

        IxTestHelper.assertValues(source, 2, 4, 5);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void secondEmptyArray() {
        Ix<Integer> source = Ix.orderedMergeArray(Ix.fromArray(1, 3), Ix.<Integer>empty());

        IxTestHelper.assertValues(source, 1, 3);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void bothEmptyArray() {
        Ix<Integer> source = Ix.orderedMergeArray(Ix.<Integer>empty(), Ix.<Integer>empty());

        IxTestHelper.assertValues(source);
    }

    @Test
    public void comparatorArray() {
        @SuppressWarnings("unchecked")
        Ix<Integer> source = Ix.orderedMergeArray(
                new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o2.compareTo(o1);
                    }
                },
                Ix.fromArray(3, 1), Ix.fromArray(5, 4, 2));

        IxTestHelper.assertValues(source, 5, 4, 3, 2, 1);
        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void comparatorIterable() {
        @SuppressWarnings("unchecked")
        Ix<Integer> source = Ix.orderedMerge(Ix.fromArray(Ix.fromArray(3, 1), Ix.fromArray(5, 4, 2)),
                new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        }
        );

        IxTestHelper.assertValues(source, 5, 4, 3, 2, 1);
        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void lotsOfIterables() {
        Ix<Integer> source = Ix.orderedMerge(Ix.repeatValue(Ix.just(1), 30)).sumInt();

        IxTestHelper.assertValues(source, 30);
    }
}
