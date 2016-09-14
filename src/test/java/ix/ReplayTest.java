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

import java.util.Iterator;

import org.junit.*;

public class ReplayTest {

    @Test
    public void normal() {
        final int[] counter = { 0 };
        Ix<Integer> source = Ix.range(1, 5)
                .doOnNext(new IxConsumer<Integer>() {
                    @Override
                    public void accept(Integer v) {
                        counter[0]++;
                    }
                })
                .replay();

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);

        Assert.assertEquals(5, counter[0]);
    }

    @Test
    public void lockstep() {
        final int[] counter = { 0 };
        Ix<Integer> source = Ix.range(1, 5)
                .doOnNext(new IxConsumer<Integer>() {
                    @Override
                    public void accept(Integer v) {
                        counter[0]++;
                    }
                })
                .replay();

        Iterator<Integer> it1 = source.iterator();
        Iterator<Integer> it2 = source.iterator();

        Assert.assertEquals(1, it1.next().intValue());
        Assert.assertEquals(1, it2.next().intValue());

        Assert.assertEquals(2, it1.next().intValue());
        Assert.assertEquals(2, it2.next().intValue());

        Assert.assertEquals(3, it1.next().intValue());
        Assert.assertEquals(3, it2.next().intValue());

        Assert.assertEquals(4, it1.next().intValue());
        Assert.assertEquals(4, it2.next().intValue());

        Assert.assertEquals(5, it1.next().intValue());
        Assert.assertEquals(5, it2.next().intValue());

        Assert.assertFalse(it1.hasNext());
        Assert.assertFalse(it1.hasNext());

        Assert.assertEquals(5, counter[0]);

    }

    @Test
    public void empty() {
        Ix<Integer> source = Ix.<Integer>empty().replay();

        IxTestHelper.assertValues(source);
        IxTestHelper.assertValues(source);
        IxTestHelper.assertValues(source);
    }

    @Test
    public void replaySizeNormal() {
        final int[] counter = { 0 };
        Ix<Integer> source = Ix.range(1, 5)
                .doOnNext(new IxConsumer<Integer>() {
                    @Override
                    public void accept(Integer v) {
                        counter[0]++;
                    }
                })
                .replay(10);

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);

        Assert.assertEquals(5, counter[0]);
    }

    @Test
    public void replaySizeLimit() {
        final int[] counter = { 0 };
        Ix<Integer> source = Ix.range(1, 5)
                .doOnNext(new IxConsumer<Integer>() {
                    @Override
                    public void accept(Integer v) {
                        counter[0]++;
                    }
                })
                .replay(2);

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
        IxTestHelper.assertValues(source, 4, 5);
        IxTestHelper.assertValues(source, 4, 5);

        Assert.assertEquals(5, counter[0]);
    }

    @Test
    public void replaySelectorNormal() {

        Ix<Integer> source = Ix.range(1, 5).replay(new IxFunction<Ix<Integer>, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(Ix<Integer> o) {
                return Ix.zip(o, o.skip(1), new IxFunction2<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer t1, Integer t2) {
                        return t1 + t2;
                    }
                });
            }
        });

        IxTestHelper.assertValues(source, 3, 5, 7, 9);
        IxTestHelper.assertValues(source, 3, 5, 7, 9);
    }

    @Test
    public void replaySizeSelectorLarge() {

        Ix<Integer> source = Ix.range(1, 5).replay(10, new IxFunction<Ix<Integer>, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(Ix<Integer> o) {
                return Ix.zip(o, o.skip(1), new IxFunction2<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer t1, Integer t2) {
                        return t1 + t2;
                    }
                });
            }
        });

        IxTestHelper.assertValues(source, 3, 5, 7, 9);
        IxTestHelper.assertValues(source, 3, 5, 7, 9);
    }

    @Test
    public void replaySizeSelectorSmall() {

        Ix<Integer> source = Ix.range(1, 5).replay(2, new IxFunction<Ix<Integer>, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(Ix<Integer> o) {
                return Ix.zip(o, o.skip(1), new IxFunction2<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer t1, Integer t2) {
                        return t1 + t2;
                    }
                });
            }
        });

        IxTestHelper.assertValues(source, 3, 5, 7, 9);
        IxTestHelper.assertValues(source, 3, 5, 7, 9);
    }

    @Test
    public void replaySizeSelectorInnerEffect() {

        Ix<Integer> source = Ix.range(1, 5).replay(2, new IxFunction<Ix<Integer>, Iterable<Integer>>() {
            @SuppressWarnings("unchecked")
            @Override
            public Iterable<Integer> apply(Ix<Integer> o) {
                return Ix.concatArray(o, o);
            }
        });

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 4, 5);
        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 4, 5);
    }
}
