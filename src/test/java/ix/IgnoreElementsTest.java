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

public class IgnoreElementsTest {

    @Test
    public void range() {
        Ix<Integer> source = Ix.range(1, 5).ignoreElements();

        IxTestHelper.assertValues(source);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void empty() {
        Ix<Integer> source = Ix.<Integer>empty().ignoreElements();

        IxTestHelper.assertValues(source);

        IxTestHelper.assertNoRemove(source);
    }

}
