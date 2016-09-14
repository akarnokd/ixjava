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

import java.util.List;

import org.junit.Test;

public class SwitchIfEmptyTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.range(1, 10).switchIfEmpty(Ix.range(11, 10));

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void empty() {
        Ix<Integer> source = Ix.<Integer>empty().switchIfEmpty(Ix.range(11, 10));

        IxTestHelper.assertValues(source, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
    }

    @Test
    public void emptyOther() {
        Ix<Integer> source = Ix.<Integer>empty().switchIfEmpty(Ix.<Integer>empty());

        IxTestHelper.assertValues(source);
    }

    @Test
    public void defaultNormal() {
        Ix<Integer> source = Ix.range(1, 10).defaultIfEmpty(100);

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void defaultEmpty() {
        Ix<Integer> source = Ix.<Integer>empty().defaultIfEmpty(100);

        IxTestHelper.assertValues(source, 100);
    }

    @Test
    public void removeNonEmpty() {
        List<Integer> list = IxTestHelper.range(1, 10);
        List<Integer> list2 = IxTestHelper.range(11, 10);

        Ix.from(list).switchIfEmpty(Ix.from(list2)).removeAll();

        IxTestHelper.assertValues(list);
        IxTestHelper.assertValues(list2, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
    }

    @Test
    public void removeEmpty() {
        List<Integer> list2 = IxTestHelper.range(11, 10);

        Ix.<Integer>empty().switchIfEmpty(Ix.from(list2)).removeAll();

        IxTestHelper.assertValues(list2);
    }
}
