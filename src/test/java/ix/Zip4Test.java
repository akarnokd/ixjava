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

public class Zip4Test {

    IxFunction4<Integer, Integer, Integer, Integer, Integer> sum = new IxFunction4<Integer, Integer, Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer a, Integer b, Integer c, Integer d) {
            return a + b + c + d;
        }
    };

    @Test
    public void normal() {

        Ix<Integer> source = Ix.zip(
                Ix.range(1, 2), Ix.range(10, 2), Ix.range(100, 2), Ix.range(1000, 2), sum);

        IxTestHelper.assertValues(source, 1111, 1115);
    }

    @Test
    public void firstShorter() {

        Ix<Integer> source = Ix.zip(
                Ix.range(1, 1), Ix.range(10, 2), Ix.range(100, 2), Ix.range(1000, 2), sum);

        IxTestHelper.assertValues(source, 1111);
    }

    @Test
    public void secondShorter() {

        Ix<Integer> source = Ix.zip(
                Ix.range(1, 3), Ix.range(10, 2), Ix.range(100, 3), Ix.range(1000, 3), sum);

        IxTestHelper.assertValues(source, 1111, 1115);
    }

    @Test
    public void thirdShorter() {

        Ix<Integer> source = Ix.zip(
                Ix.range(1, 3), Ix.range(10, 3), Ix.range(100, 2), Ix.range(1000, 3), sum);

        IxTestHelper.assertValues(source, 1111, 1115);
    }

    @Test
    public void fourthShorter() {

        Ix<Integer> source = Ix.zip(
                Ix.range(1, 3), Ix.range(10, 3), Ix.range(100, 3), Ix.range(1000, 2), sum);

        IxTestHelper.assertValues(source, 1111, 1115);
    }

    @Test
    public void allEmpty() {

        Ix<Integer> source = Ix.zip(
                Ix.<Integer>empty(), Ix.<Integer>empty(), Ix.<Integer>empty(), Ix.<Integer>empty(), sum);

        IxTestHelper.assertValues(source);
    }

}
