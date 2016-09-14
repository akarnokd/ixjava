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

public class ContainsTest {

    @Test
    public void normal() {
        Ix<Boolean> source = Ix.range(1, 5).contains(3);

        IxTestHelper.assertValues(source, true);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalNoReferenceEquals() {
        Ix<Boolean> source = Ix.range(1, 5).contains(new Integer(3));

        IxTestHelper.assertValues(source, true);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void doesntContain() {
        Ix<Boolean> source = Ix.range(1, 5).contains(6);

        IxTestHelper.assertValues(source, false);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void doesntContainNull() {
        Ix<Boolean> source = Ix.range(1, 5).contains(null);

        IxTestHelper.assertValues(source, false);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void empty() {
        Ix<Boolean> source = Ix.empty().contains(6);

        IxTestHelper.assertValues(source, false);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void justNull() {
        Ix<Boolean> source = Ix.just(null).contains(6);

        IxTestHelper.assertValues(source, false);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void justNullContains() {
        Ix<Boolean> source = Ix.just(null).contains(null);

        IxTestHelper.assertValues(source, true);

        IxTestHelper.assertNoRemove(source);
    }
}
