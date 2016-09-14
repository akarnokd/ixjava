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

public class OrderByTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.fromArray(5, 4, 3, 2, 1).orderBy();

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalComparator() {
        Ix<Integer> source = Ix.fromArray(1, 2, 3, 4, 5).orderBy(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b.compareTo(a);
            }
        });

        IxTestHelper.assertValues(source, 5, 4, 3, 2, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalKeySelector() {
        Ix<Integer> source = Ix.fromArray(1, 2, 3, 4, 5).orderBy(new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return 3 - v;
            }
        });

        IxTestHelper.assertValues(source, 5, 4, 3, 2, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void empty() {
        Ix<Integer> source = Ix.<Integer>empty().orderBy();

        IxTestHelper.assertValues(source);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void just() {
        Ix<Integer> source = Ix.just(1).orderBy();

        IxTestHelper.assertValues(source, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalReverse() {
        Ix<Integer> source = Ix.range(1, 5).orderByReverse();

        IxTestHelper.assertValues(source, 5, 4, 3, 2, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalComparatorReverse() {
        Ix<Integer> source = Ix.fromArray(1, 2, 3, 4, 5).orderByReverse(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b.compareTo(a);
            }
        });

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalKeySelectorReverse() {
        Ix<Integer> source = Ix.fromArray(1, 2, 3, 4, 5).orderByReverse(new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return 3 - v;
            }
        });

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);

        IxTestHelper.assertNoRemove(source);
    }
}
