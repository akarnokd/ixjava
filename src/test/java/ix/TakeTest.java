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

import java.util.*;

import org.junit.*;

public class TakeTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.range(1, 10).take(5);

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
    }

    @Test
    public void all() {
        Ix<Integer> source = Ix.range(1, 10).take(10);

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void more() {
        Ix<Integer> source = Ix.range(1, 10).take(15);

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void none() {
        Ix<Integer> source = Ix.range(1, 10).take(0);

        IxTestHelper.assertValues(source);
    }

    @Test
    public void just() {
        Ix<Integer> source = Ix.just(1).take(5);

        IxTestHelper.assertValues(source, 1);
    }

    @Test
    public void empty() {
        Ix<Integer> source = Ix.<Integer>empty().take(5);

        IxTestHelper.assertValues(source);
    }

    @Test
    public void removeComposes() {
        List<Integer> list = Ix.range(1, 10).collectToList().first();

        Ix.from(list).take(5).removeAll();

        Assert.assertEquals(Arrays.asList(6, 7, 8, 9, 10), list);
    }

}
