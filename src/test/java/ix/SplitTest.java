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

public class SplitTest {

    @Test
    public void normal() {
        Ix<String> source = Ix.split("a|b|c|d", "|");

        IxTestHelper.assertValues(source, "a", "b", "c", "d");

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normal2() {
        Ix<String> source = Ix.split("a|b|c|", "|");

        IxTestHelper.assertValues(source, "a", "b", "c", "");

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normal3() {
        Ix<String> source = Ix.split("a1|b2|c3", "|");

        IxTestHelper.assertValues(source, "a1", "b2", "c3");

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normal4() {
        Ix<String> source = Ix.split("a1|<b2|c3|<d4", "|<");

        IxTestHelper.assertValues(source, "a1", "b2|c3", "d4");

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void empty() {
        Ix<String> source = Ix.split("", "|");

        IxTestHelper.assertValues(source, "");

        IxTestHelper.assertNoRemove(source);
    }
}
