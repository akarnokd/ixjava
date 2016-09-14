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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReduceTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.range(1, 10).reduce(new IxSupplier<Integer>() {
            @Override
            public Integer get() {
                return 0;
            }
        }, new IxFunction2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) {
                return a + b;
            }
        });

        assertEquals(55, source.first().intValue());
    }

    @Test
    public void aggregate() {
        Ix<Integer> source = Ix.range(1, 10).reduce(new IxFunction2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) {
                return a + b;
            }
        });

        assertEquals(55, source.first().intValue());
    }

    @Test
    public void aggregateEmpty() {
        Ix<Integer> source = Ix.<Integer>empty().reduce(new IxFunction2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) {
                return a + b;
            }
        });

        IxTestHelper.assertValues(source);
    }
}
