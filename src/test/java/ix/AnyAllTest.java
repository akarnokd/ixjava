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

public class AnyAllTest {

    @Test
    public void anyFound() {
        Ix<Boolean> source = Ix.range(1, 5).any(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return v == 3;
            }
        });

        IxTestHelper.assertValues(source, true);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void anyNotFound() {
        Ix<Boolean> source = Ix.range(1, 5).any(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return v == 0;
            }
        });

        IxTestHelper.assertValues(source, false);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void anyEmpty() {
        Ix<Boolean> source = Ix.<Integer>empty().any(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return v == 3;
            }
        });

        IxTestHelper.assertValues(source, false);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void allTrue() {
        Ix<Boolean> source = Ix.range(1, 5).all(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return v < 6;
            }
        });

        IxTestHelper.assertValues(source, true);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void allFalse() {
        Ix<Boolean> source = Ix.range(1, 5).all(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return false;
            }
        });

        IxTestHelper.assertValues(source, false);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void allEmpty() {
        Ix<Boolean> source = Ix.<Integer>empty().all(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return false;
            }
        });

        IxTestHelper.assertValues(source, true);

        IxTestHelper.assertNoRemove(source);
    }


}
