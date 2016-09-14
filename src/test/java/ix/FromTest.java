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

import org.junit.*;

public class FromTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.from(Arrays.asList(1, 2, 3, 4, 5));

        Assert.assertTrue(source.getClass().toString(), source instanceof IxWrapper);

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
    }

    @Test
    public void noRewrapping() {
        Ix<Integer> source = Ix.from(Ix.range(1, 5));

        Assert.assertFalse(source.getClass().toString(), source instanceof IxWrapper);

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
    }

}
