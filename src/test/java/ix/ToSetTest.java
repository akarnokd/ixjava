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

import org.junit.Test;

public class ToSetTest {

    @SuppressWarnings("unchecked")
    @Test
    public void normal() {
        Ix<Set<Integer>> source = Ix.range(1, 5).collectToSet();

        IxTestHelper.assertValues(source, new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5)));

        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void empty() {
        Ix<Set<Integer>> source = Ix.<Integer>empty().collectToSet();

        IxTestHelper.assertValues(source, new HashSet<Integer>());

        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void duplicates() {
        Ix<Set<Integer>> source = Ix.fromArray(1, 2, 2, 3, 2, 4, 5, 1, 5).collectToSet();

        IxTestHelper.assertValues(source, new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5)));

        IxTestHelper.assertNoRemove(source);
    }

}
