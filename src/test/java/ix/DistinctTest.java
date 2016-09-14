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

public class DistinctTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.fromArray(1, 2, 2, 1, 3, 4, 2).distinct();

        IxTestHelper.assertValues(source, 1, 2, 3, 4);
    }

    @Test
    public void empty() {
        Ix<Integer> source = Ix.<Integer>empty().distinct();

        IxTestHelper.assertValues(source);
    }

    @Test
    public void normalSelector() {
        Ix<Integer> source = Ix.fromArray(1, 2, 2, 1, 3, 4, 2).distinct(new IxFunction<Integer, Object>() {
            @Override
            public Object apply(Integer v) {
                return v & 1;
            }
        });

        IxTestHelper.assertValues(source, 1, 2);
    }
}
