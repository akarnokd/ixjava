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

public class JoinTest {

    @Test
    public void normal() {
        Ix<String> source = Ix.range(1, 5).join();

        IxTestHelper.assertValues(source, "1, 2, 3, 4, 5");

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void just() {
        Ix<String> source = Ix.just(1).join();

        IxTestHelper.assertValues(source, "1");

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void empty() {
        Ix<String> source = Ix.empty().join();

        IxTestHelper.assertValues(source, "");

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalSeparator() {
        Ix<String> source = Ix.range(1, 5).join("|");

        IxTestHelper.assertValues(source, "1|2|3|4|5");

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void justSeparator() {
        Ix<String> source = Ix.just(1).join("|");

        IxTestHelper.assertValues(source, "1");

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void emptySeparator() {
        Ix<String> source = Ix.empty().join("|");

        IxTestHelper.assertValues(source, "");

        IxTestHelper.assertNoRemove(source);
    }

}
