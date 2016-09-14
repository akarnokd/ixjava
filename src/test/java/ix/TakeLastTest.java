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

public class TakeLastTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.range(1, 10).takeLast(5);

        IxTestHelper.assertValues(source, 6, 7, 8, 9, 10);
    }

    @Test
    public void normalTwo() {
        Ix<Integer> source = Ix.range(1, 2).takeLast(1);

        IxTestHelper.assertValues(source, 2);
    }

    @Test
    public void normalThree() {
        Ix<Integer> source = Ix.range(1, 16).takeLast(15);

        IxTestHelper.assertValues(source, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
    }

    @Test
    public void zero() {
        Ix<Integer> source = Ix.range(1, 10).takeLast(0);

        IxTestHelper.assertValues(source);
    }

    @Test
    public void all() {
        Ix<Integer> source = Ix.range(1, 10).takeLast(10);

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void more() {
        Ix<Integer> source = Ix.range(1, 10).takeLast(15);

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }


    @Test
    public void just() {
        Ix<Integer> source = Ix.just(1).takeLast(1);

        IxTestHelper.assertValues(source, 1);
    }

    @Test
    public void empty() {
        Ix<Integer> source = Ix.<Integer>empty().takeLast(1);

        IxTestHelper.assertValues(source);
    }

    @Test
    public void emptyZero() {
        Ix<Integer> source = Ix.<Integer>empty().takeLast(0);

        IxTestHelper.assertValues(source);
    }

}
