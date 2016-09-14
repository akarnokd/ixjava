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

public class AverageTest {

    @Test
    public void normalFloat() {
        Ix<Float> source = Ix.range(1, 5).averageFloat();

        IxTestHelper.assertValues(source, 3f);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void justFloat() {
        Ix<Float> source = Ix.just(1).averageFloat();

        IxTestHelper.assertValues(source, 1f);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void emptyFloat() {
        Ix<Float> source = Ix.<Integer>empty().averageFloat();

        IxTestHelper.assertValues(source);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalDouble() {
        Ix<Double> source = Ix.range(1, 5).averageDouble();

        IxTestHelper.assertValues(source, 3d);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void justDouble() {
        Ix<Double> source = Ix.just(1).averageDouble();

        IxTestHelper.assertValues(source, 1d);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void emptyDouble() {
        Ix<Double> source = Ix.<Integer>empty().averageDouble();

        IxTestHelper.assertValues(source);

        IxTestHelper.assertNoRemove(source);
    }

}
