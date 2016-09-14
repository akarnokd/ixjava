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

import org.junit.*;

public class GroupByTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.range(1, 10).groupBy(new IxFunction<Integer, Object>() {
            @Override
            public Object apply(Integer v) {
                return v % 3;
            }
        }).flatMap(new IxFunction<GroupedIx<Object, Integer>, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(GroupedIx<Object, Integer> v) {
                return v;
            }
        });

        IxTestHelper.assertValues(source, 1, 4, 7, 10, 2, 5, 8, 3, 6, 9);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void uniqueGroups() {
        Ix<Integer> source = Ix.range(1, 10).groupBy(new IxFunction<Integer, Object>() {
            @Override
            public Object apply(Integer v) {
                return v;
            }
        },
        new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v * 10;
            }
        }
        ).flatMap(new IxFunction<GroupedIx<Object, Integer>, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(GroupedIx<Object, Integer> v) {
                return v;
            }
        });

        IxTestHelper.assertValues(source, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void just() {
        Ix<Integer> source = Ix.just(1).groupBy(new IxFunction<Integer, Object>() {
            @Override
            public Object apply(Integer v) {
                return v % 3;
            }
        }).flatMap(new IxFunction<GroupedIx<Object, Integer>, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(GroupedIx<Object, Integer> v) {
                return v;
            }
        });

        IxTestHelper.assertValues(source, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void justNull() {
        Ix<Integer> source = Ix.<Integer>just(null).groupBy(new IxFunction<Integer, Object>() {
            @Override
            public Object apply(Integer v) {
                return 1;
            }
        }).flatMap(new IxFunction<GroupedIx<Object, Integer>, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(GroupedIx<Object, Integer> v) {
                return v;
            }
        });

        IxTestHelper.assertValues(source, (Integer)null);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void empty() {
        Ix<Integer> source = Ix.<Integer>empty().groupBy(new IxFunction<Integer, Object>() {
            @Override
            public Object apply(Integer v) {
                return v % 3;
            }
        }).flatMap(new IxFunction<GroupedIx<Object, Integer>, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(GroupedIx<Object, Integer> v) {
                return v;
            }
        });

        IxTestHelper.assertValues(source);

        IxTestHelper.assertNoRemove(source);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void lockstep() {
        final Iterator<Integer>[] its = new Iterator[3];

        Iterator<?> main = Ix.range(1, 10).groupBy(new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v % 3;
            }
        }).doOnNext(new IxConsumer<GroupedIx<Integer, Integer>>() {
            @Override
            public void accept(GroupedIx<Integer, Integer> g) {
                its[g.key()] = g.iterator();
            }
        })
        .iterator();

        Assert.assertNotNull(main.next());

        Assert.assertEquals(1, its[1].next().intValue());

        Assert.assertNotNull(main.next());

        Assert.assertEquals(2, its[2].next().intValue());

        Assert.assertTrue(its[2].hasNext());
        Assert.assertNotNull(main.next());

        Assert.assertEquals(3, its[0].next().intValue());
        Assert.assertEquals(4, its[1].next().intValue());
        Assert.assertEquals(5, its[2].next().intValue());
        Assert.assertEquals(6, its[0].next().intValue());

        Assert.assertEquals(7, its[1].next().intValue());
        Assert.assertEquals(8, its[2].next().intValue());
        Assert.assertEquals(9, its[0].next().intValue());
        Assert.assertEquals(10, its[1].next().intValue());

        Assert.assertFalse(main.hasNext());
        Assert.assertFalse(its[0].hasNext());
        Assert.assertFalse(its[1].hasNext());
        Assert.assertFalse(its[2].hasNext());
    }


    @SuppressWarnings("unchecked")
    @Test
    public void lockstepNull() {
        final Iterator<Integer>[] its = new Iterator[3];

        Iterator<?> main = Ix.range(1, 10).groupBy(new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v % 3;
            }
        }, new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return null;
            }
        }).doOnNext(new IxConsumer<GroupedIx<Integer, Integer>>() {
            @Override
            public void accept(GroupedIx<Integer, Integer> g) {
                its[g.key()] = g.iterator();
            }
        })
        .iterator();

        Assert.assertNotNull(main.next());

        Assert.assertNull(its[1].next());

        Assert.assertNotNull(main.next());

        Assert.assertNull(its[2].next());

        Assert.assertTrue(its[2].hasNext());
        Assert.assertNotNull(main.next());

        Assert.assertNull(its[0].next());
        Assert.assertNull(its[1].next());
        Assert.assertNull(its[2].next());
        Assert.assertNull(its[0].next());

        Assert.assertNull(its[1].next());
        Assert.assertNull(its[2].next());
        Assert.assertNull(its[0].next());
        Assert.assertNull(its[1].next());

        Assert.assertFalse(main.hasNext());
        Assert.assertFalse(its[0].hasNext());
        Assert.assertFalse(its[1].hasNext());
        Assert.assertFalse(its[2].hasNext());
    }
    @Test
    public void sameGroupSubsequently() {
        Ix<GroupedIx<Integer, Integer>> source = Ix.range(1, 5).groupBy(new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return 1;
            }
        });

        List<GroupedIx<Integer, Integer>> list = source.collectToList().first();

        IxTestHelper.assertValues(Ix.concat(list), 1, 2, 3, 4, 5);
    }

    @Test
    public void sameGroupSubsequentlyNullValue() {
        Ix<GroupedIx<Integer, Integer>> source = Ix.range(1, 5).groupBy(new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return 1;
            }
        }, new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return null;
            }
        });

        List<GroupedIx<Integer, Integer>> list = source.collectToList().first();

        IxTestHelper.assertValues(Ix.concat(list), null, null, null, null, null);
    }

    @Test(expected = IllegalStateException.class)
    public void iteratorOnce() {
        Ix<GroupedIx<Integer, Integer>> source = Ix.range(1, 5).groupBy(new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return 1;
            }
        }, new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return null;
            }
        });

        List<GroupedIx<Integer, Integer>> list = source.collectToList().first();

        Assert.assertEquals("GroupedIterable[key=1, queue=5]", list.get(0).toString());

        IxTestHelper.assertValues(list.get(0), null, null, null, null, null);
        IxTestHelper.assertValues(list.get(0), null, null, null, null, null);
    }
}
