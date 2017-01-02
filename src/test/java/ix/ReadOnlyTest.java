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

public class ReadOnlyTest {

    @Test
    public void normal() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);

        Ix<Integer> source = Ix.from(list).readOnly();

        IxTestHelper.assertValues(source, 1, 2, 3);
        IxTestHelper.assertNoRemove(source);

        Assert.assertEquals(3, list.size());
    }

    @Test
    public void normalSilent() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);

        Ix<Integer> source = Ix.from(list).readOnly(true);

        IxTestHelper.assertValues(source, 1, 2, 3);

        source.removeAll();

        Assert.assertEquals(3, list.size());

        IxTestHelper.assertValues(source, 1, 2, 3);
    }
}
