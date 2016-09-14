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

public class ConcatArrayTest {

    @SuppressWarnings("unchecked")
    @Test
    public void normal() {
        Ix<Integer> source = Ix.concatArray(Ix.range(1, 5), Ix.range(6, 5));


        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void normalViaMerge() {
        Ix<Integer> source = Ix.mergeArray(Ix.range(1, 5), Ix.range(6, 5));


        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void just() {
        Ix<Integer> source = Ix.concatArray(Ix.just(1), Ix.just(2));


        IxTestHelper.assertValues(source, 1, 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void empty() {
        Ix<Integer> source = Ix.concatArray(Ix.<Integer>empty(), Ix.<Integer>empty());


        IxTestHelper.assertValues(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mixed() {
        Ix<Integer> source = Ix.concatArray(Ix.<Integer>empty(), Ix.range(1, 5),
                Ix.<Integer>empty(), Ix.just(6), Ix.<Integer>empty());


        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mixedTwo() {
        Ix<Integer> source = Ix.concatArray(Ix.range(1, 5),
                Ix.<Integer>empty(), Ix.just(6));


        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6);
    }

    @Test
    public void emptyArray() {
        @SuppressWarnings("unchecked")
        Ix<Integer> source = Ix.concatArray();

        Assert.assertSame(source.getClass().toString(), source, Ix.empty());
    }

    @Test
    public void justArray() {
        @SuppressWarnings("unchecked")
        Ix<Integer> source = Ix.concatArray(Ix.just(1));

        Assert.assertTrue(source.getClass().toString(), source instanceof IxScalarCallable);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void concatIterable() {
        Ix<Integer> source = Ix.concat(Arrays.asList(Ix.range(1, 5), Ix.range(6, 5)));


        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void concatIterableViaMerge() {
        Ix<Integer> source = Ix.merge(Arrays.asList(Ix.range(1, 5), Ix.range(6, 5)));


        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void startWith() {
        Ix<Integer> source = Ix.range(1, 5).startWith(5, 4, 3, 2, 1);

        IxTestHelper.assertValues(source, 5, 4, 3, 2, 1, 1, 2, 3, 4, 5);
    }

    @Test
    public void startWithEmpty() {
        Ix<Integer> source = Ix.range(1, 5).startWith();

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
    }

    @Test
    public void endWith() {
        Ix<Integer> source = Ix.range(1, 5).endWith(5, 4, 3, 2, 1);

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 5, 4, 3, 2, 1);
    }

    @Test
    public void endWithEmpty() {
        Ix<Integer> source = Ix.range(1, 5).endWith();

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
    }

    @Test
    public void concatWith() {
        Ix<Integer> source = Ix.range(1, 5).concatWith(Ix.range(6, 5));

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void concatWithEmpty() {
        Ix<Integer> source = Ix.range(1, 5).concatWith(Ix.<Integer>empty());

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
    }

    @Test
    public void mergeWith() {
        Ix<Integer> source = Ix.range(1, 5).mergeWith(Ix.range(6, 5));

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void mergeWithEmpty() {
        Ix<Integer> source = Ix.range(1, 5).mergeWith(Ix.<Integer>empty());

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
    }

    @Test
    public void concat2() {
        Ix<Integer> source = Ix.concat(Ix.just(1), Ix.just(2));

        IxTestHelper.assertValues(source, 1, 2);
    }

    @Test
    public void concat3() {
        Ix<Integer> source = Ix.concat(Ix.just(1), Ix.just(2), Ix.just(3));

        IxTestHelper.assertValues(source, 1, 2, 3);
    }

    @Test
    public void concat4() {
        Ix<Integer> source = Ix.concat(Ix.just(1), Ix.just(2), Ix.just(3), Ix.just(4));

        IxTestHelper.assertValues(source, 1, 2, 3, 4);
    }
}
