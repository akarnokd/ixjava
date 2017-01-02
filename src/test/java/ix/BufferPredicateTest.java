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

public class BufferPredicateTest {

    @SuppressWarnings("unchecked")
    @Test
    public void splitNormal() {
        Ix<List<Integer>> source = Ix.fromArray(1, 2, 10, 3, 10, 4)
                .bufferSplit(new IxPredicate<Integer>() {
                    @Override
                    public boolean test(Integer v) {
                        return v == 10;
                    }
                });

        IxTestHelper.assertValues(source, Arrays.asList(1, 2), Arrays.asList(3), Arrays.asList(4));
        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void splitNormalEmpty() {
        Ix<List<Integer>> source = Ix.fromArray(1, 2, 10, 10)
                .bufferSplit(new IxPredicate<Integer>() {
                    @Override
                    public boolean test(Integer v) {
                        return v == 10;
                    }
                });

        IxTestHelper.assertValues(source, Arrays.asList(1, 2), Arrays.<Integer>asList());
        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void whileNormal() {
        Ix<List<Integer>> source = Ix.fromArray(1, 2, 10, 3, 10, 4)
                .bufferWhile(new IxPredicate<Integer>() {
                    @Override
                    public boolean test(Integer v) {
                        return v != 10;
                    }
                });

        IxTestHelper.assertValues(source, Arrays.asList(1, 2), Arrays.asList(10, 3), Arrays.asList(10, 4));
        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void whileNormalEmpty() {
        Ix<List<Integer>> source = Ix.fromArray(1, 2, 10, 10)
                .bufferWhile(new IxPredicate<Integer>() {
                    @Override
                    public boolean test(Integer v) {
                        return v != 10;
                    }
                });

        IxTestHelper.assertValues(source, Arrays.asList(1, 2), Arrays.asList(10), Arrays.asList(10));
        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void untilNormal() {
        Ix<List<Integer>> source = Ix.fromArray(1, 2, 10, 3, 10, 4)
                .bufferUntil(new IxPredicate<Integer>() {
                    @Override
                    public boolean test(Integer v) {
                        return v == 10;
                    }
                });

        IxTestHelper.assertValues(source, Arrays.asList(1, 2, 10), Arrays.asList(3, 10), Arrays.asList(4));
        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void untilNormalEmpty() {
        Ix<List<Integer>> source = Ix.fromArray(1, 2, 10, 10)
                .bufferUntil(new IxPredicate<Integer>() {
                    @Override
                    public boolean test(Integer v) {
                        return v == 10;
                    }
                });

        IxTestHelper.assertValues(source, Arrays.asList(1, 2, 10), Arrays.asList(10));
        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void splitEmpty() {
        Ix<List<Integer>> source = Ix.<Integer>empty().bufferSplit(new IxPredicate<Integer>() {
                    @Override
                    public boolean test(Integer v) {
                        return v == 10;
                    }
                });

        IxTestHelper.assertValues(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void whileEmpty() {
        Ix<List<Integer>> source = Ix.<Integer>empty().bufferWhile(new IxPredicate<Integer>() {
                    @Override
                    public boolean test(Integer v) {
                        return v != 10;
                    }
                });

        IxTestHelper.assertValues(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void untilEmpty() {
        Ix<List<Integer>> source = Ix.<Integer>empty().bufferUntil(new IxPredicate<Integer>() {
                    @Override
                    public boolean test(Integer v) {
                        return v == 10;
                    }
                });

        IxTestHelper.assertValues(source);
    }
}
