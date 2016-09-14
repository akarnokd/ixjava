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

public class ScanTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.range(1, 5).scan(new IxFunction2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        });

        IxTestHelper.assertValues(source, 1, 3, 6, 10, 15);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void just() {
        Ix<Integer> source = Ix.just(1).scan(new IxFunction2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        });

        IxTestHelper.assertValues(source, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void empty() {
        Ix<Integer> source = Ix.<Integer>empty().scan(new IxFunction2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        });

        IxTestHelper.assertValues(source);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalSeed() {
        Ix<Integer> source = Ix.range(1, 5).scan(new IxSupplier<Integer>() {
            @Override
            public Integer get() {
                return 100;
            }
        },new IxFunction2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        });

        IxTestHelper.assertValues(source, 100, 101, 103, 106, 110, 115);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void justSeed() {
        Ix<Integer> source = Ix.just(1).scan(new IxSupplier<Integer>() {
            @Override
            public Integer get() {
                return 100;
            }
        }, new IxFunction2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        });

        IxTestHelper.assertValues(source, 100, 101);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void emptySeed() {
        Ix<Integer> source = Ix.<Integer>empty().scan(new IxSupplier<Integer>() {
            @Override
            public Integer get() {
                return 100;
            }
        }, new IxFunction2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        });

        IxTestHelper.assertValues(source, 100);

        IxTestHelper.assertNoRemove(source);
    }
}
