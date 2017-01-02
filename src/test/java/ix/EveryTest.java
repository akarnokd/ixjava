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

public class EveryTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.range(1, 5).every(2);

        IxTestHelper.assertValues(source, 2, 4);
        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normal2() {
        Ix<Integer> source = Ix.range(1, 5).every(1);

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normal3() {
        Ix<Integer> source = Ix.range(1, 5).every(10);

        IxTestHelper.assertValues(source);
        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void remove() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        Ix.from(list).every(2).removeAll();

        Assert.assertEquals(Arrays.asList(1, 3, 5), list);
    }
}
