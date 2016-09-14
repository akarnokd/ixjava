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

public class AsComposeTest {

    @Test
    public void normal() {
        Integer value = Ix.range(1, 5).as(new IxFunction<Ix<Integer>, Integer>() {
            @Override
            public Integer apply(Ix<Integer> ix) {
                return ix.first();
            }
        });

        Assert.assertEquals(1, value.intValue());
    }

    @Test
    public void compose() {
        Ix<String> source = Ix.range(1, 5).compose(new IxFunction<Ix<Integer>, Iterable<String>>() {
            @Override
            public Iterable<String> apply(Ix<Integer> ix) {
                final int[] index = { 1 };
                return ix.map(new IxFunction<Integer, String>() {
                    @Override
                    public String apply(Integer v) {
                        return v + "-" + (index[0]++);
                    }
                });
            }
        });

        IxTestHelper.assertValues(source, "1-1", "2-2", "3-3", "4-4", "5-5");

        IxTestHelper.assertValues(source, "1-1", "2-2", "3-3", "4-4", "5-5");
    }
}
