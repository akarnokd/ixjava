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

import java.util.Arrays;

import org.junit.Test;

public class ZipIterableTest {

    IxFunction<Object[], Integer> zipper = new IxFunction<Object[], Integer>() {
        @Override
        public Integer apply(Object[] a) {
            int s = 0;
            for (Object o : a) {
                s += (Integer)o;
            }
            return s;
        }
    };

    @SuppressWarnings("unchecked")
    @Test
    public void normal() {

        Ix<Integer> source = Ix.zip(Arrays.asList(
                Ix.range(1, 2), Ix.range(10, 2)),
                zipper);

        IxTestHelper.assertValues(source, 11, 13);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void normalMany() {

        Ix<Integer> source = Ix.zip(Arrays.asList(
                Ix.range(1, 2), Ix.range(1, 2),
                Ix.range(1, 2), Ix.range(1, 2),
                Ix.range(1, 2), Ix.range(1, 2),
                Ix.range(1, 2), Ix.range(1, 2),
                Ix.range(1, 2), Ix.range(1, 2),
                Ix.range(1, 2), Ix.range(1, 2)
                ),
                zipper);

        IxTestHelper.assertValues(source, 12, 24);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void firstShorter() {

        Ix<Integer> source = Ix.zip(Arrays.asList(
                Ix.range(1, 1), Ix.range(10, 2) ),
                zipper);

        IxTestHelper.assertValues(source, 11);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void secondShorter() {

        Ix<Integer> source = Ix.zip(Arrays.asList(
                Ix.range(1, 3), Ix.range(10, 2) ),
                zipper);

        IxTestHelper.assertValues(source, 11, 13);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void bothEmpty() {

        Ix<Integer> source = Ix.zip(Arrays.asList(
                Ix.empty(), Ix.empty() ),
                zipper);

        IxTestHelper.assertValues(source);
    }

}
