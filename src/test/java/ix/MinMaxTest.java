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

public class MinMaxTest {

    @Test
    public void minComparator() {
        Ix<Integer> source = Ix.range(1, 5).min(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b.compareTo(a);
            }
        });

        IxTestHelper.assertValues(source, 5);
    }

    @Test
    public void minComparatorEmpty() {
        Ix<Integer> source = Ix.<Integer>empty().min(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b.compareTo(a);
            }
        });

        IxTestHelper.assertValues(source);
    }

    @Test
    public void minComparatorJust() {
        Ix<Integer> source = Ix.just(1).min(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b.compareTo(a);
            }
        });

        IxTestHelper.assertValues(source, 1);
    }

    @Test
    public void maxComparator() {
        Ix<Integer> source = Ix.range(1, 5).max(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b.compareTo(a);
            }
        });

        IxTestHelper.assertValues(source, 1);
    }

    @Test
    public void maxComparatorEmpty() {
        Ix<Integer> source = Ix.<Integer>empty().max(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b.compareTo(a);
            }
        });

        IxTestHelper.assertValues(source);
    }

    @Test
    public void maxComparatorJust() {
        Ix<Integer> source = Ix.just(1).max(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b.compareTo(a);
            }
        });

        IxTestHelper.assertValues(source, 1);
    }

    @Test
    public void min() {
        Ix<Integer> source = Ix.range(1, 5).min();

        IxTestHelper.assertValues(source, 1);
    }

    @Test
    public void minEmpty() {
        Ix<Integer> source = Ix.<Integer>empty().min();

        IxTestHelper.assertValues(source);
    }

    @Test
    public void minJust() {
        Ix<Integer> source = Ix.just(1).min();

        IxTestHelper.assertValues(source, 1);
    }

    @Test
    public void max() {
        Ix<Integer> source = Ix.range(1, 5).max();

        IxTestHelper.assertValues(source, 5);
    }

    @Test
    public void maxEmpty() {
        Ix<Integer> source = Ix.<Integer>empty().max();

        IxTestHelper.assertValues(source);
    }

    @Test
    public void maxJust() {
        Ix<Integer> source = Ix.just(1).max();

        IxTestHelper.assertValues(source, 1);
    }
}
