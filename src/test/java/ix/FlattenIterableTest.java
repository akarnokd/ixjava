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

public class FlattenIterableTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.range(1, 5).flatMap(new IxFunction<Integer, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(Integer v) {
                return Ix.range(v, 2);
            }
        });

        IxTestHelper.assertValues(source, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalViaConcatMap() {
        Ix<Integer> source = Ix.range(1, 5).concatMap(new IxFunction<Integer, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(Integer v) {
                return Ix.range(v, 2);
            }
        });

        IxTestHelper.assertValues(source, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6);
    }

    @Test
    public void just() {
        Ix<Integer> source = Ix.just(1).flatMap(new IxFunction<Integer, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(Integer v) {
                return Ix.range(v, 2);
            }
        });

        IxTestHelper.assertValues(source, 1, 2);
    }

    @Test
    public void empty() {
        Ix<Integer> source = Ix.<Integer>empty().flatMap(new IxFunction<Integer, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(Integer v) {
                return Ix.range(v, 2);
            }
        });

        IxTestHelper.assertValues(source);
    }

    @Test
    public void rangeJust() {
        Ix<Integer> source = Ix.range(1, 2).flatMap(new IxFunction<Integer, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(Integer v) {
                return Ix.just(v);
            }
        });

        IxTestHelper.assertValues(source, 1, 2);
    }

    @Test
    public void rangeEmpty() {
        Ix<Integer> source = Ix.range(1, 2).flatMap(new IxFunction<Integer, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(Integer v) {
                return Ix.empty();
            }
        });

        IxTestHelper.assertValues(source);
    }
}
