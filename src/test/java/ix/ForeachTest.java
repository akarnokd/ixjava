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

public class ForeachTest implements IxConsumer<Integer>, IxPredicate<Integer> {

    List<Integer> list;

    @Before
    public void before() {
        list = new ArrayList<Integer>();
    }

    @Override
    public void accept(Integer t) {
        list.add(t);
    }

    @Override
    public boolean test(Integer t) {
        list.add(t);
        return list.size() < 5;
    }

    @Test
    public void normal() {
        Ix<Integer> source = Ix.range(1, 10);

        source.foreach(this);

        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), list);
    }

    @Test
    public void normalWhile() {
        Ix<Integer> source = Ix.range(1, 10);

        source.foreachWhile(this);

        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
    }

    @Test
    public void normalWhileAll() {
        Ix<Integer> source = Ix.range(1, 5);

        source.foreachWhile(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return true;
            }
        });

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
    }
}
