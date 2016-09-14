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

public class WindowTest {

    @Test
    public void normal() {
        Ix<Ix<Integer>> source = Ix.range(1, 5).window(2);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(3, list.size());
        IxTestHelper.assertValues(list.get(0), 1, 2);
        IxTestHelper.assertValues(list.get(1), 3, 4);
        IxTestHelper.assertValues(list.get(2), 5);
    }

    @Test
    public void normalSizeSkipSame() {
        Ix<Ix<Integer>> source = Ix.range(1, 5).window(2, 2);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(3, list.size());
        IxTestHelper.assertValues(list.get(0), 1, 2);
        IxTestHelper.assertValues(list.get(1), 3, 4);
        IxTestHelper.assertValues(list.get(2), 5);
    }

    @Test
    public void normalOne() {
        Ix<Ix<Integer>> source = Ix.range(1, 5).window(1);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(5, list.size());
        IxTestHelper.assertValues(list.get(0), 1);
        IxTestHelper.assertValues(list.get(1), 2);
        IxTestHelper.assertValues(list.get(2), 3);
        IxTestHelper.assertValues(list.get(3), 4);
        IxTestHelper.assertValues(list.get(4), 5);
    }

    @Test
    public void normalAll() {
        Ix<Ix<Integer>> source = Ix.range(1, 5).window(5);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(1, list.size());
        IxTestHelper.assertValues(list.get(0), 1, 2, 3, 4, 5);
    }

    @Test
    public void normalMore() {
        Ix<Ix<Integer>> source = Ix.range(1, 5).window(10);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(1, list.size());
        IxTestHelper.assertValues(list.get(0), 1, 2, 3, 4, 5);
    }

    @Test
    public void innerMovesParent() {

        Ix<Ix<Integer>> source = Ix.range(1, 5).window(3);

        Iterator<Ix<Integer>> it0 = source.iterator();

        Ix<Integer> inner = it0.next();

        Iterator<Integer> it1 = inner.iterator();

        try {
            inner.iterator();
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            Assert.assertEquals("This Window Ix iterable can be consumed only once.", ex.getMessage());
        }
        Assert.assertEquals(1, it1.next().intValue());
        Assert.assertEquals(2, it1.next().intValue());
        Assert.assertEquals(3, it1.next().intValue());
        Assert.assertFalse(it1.hasNext());

    }

    @Test
    public void normalSkip() {
        Ix<Ix<Integer>> source = Ix.range(1, 5).window(2, 3);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(2, list.size());
        IxTestHelper.assertValues(list.get(0), 1, 2);
        IxTestHelper.assertValues(list.get(1), 4, 5);
    }

    @Test
    public void normalSkip2() {
        Ix<Ix<Integer>> source = Ix.range(1, 5).window(1, 2);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(3, list.size());
        IxTestHelper.assertValues(list.get(0), 1);
        IxTestHelper.assertValues(list.get(1), 3);
        IxTestHelper.assertValues(list.get(2), 5);
    }

    @Test
    public void normalSkip3() {
        Ix<Ix<Integer>> source = Ix.range(1, 5).window(1, 6);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(1, list.size());
        IxTestHelper.assertValues(list.get(0), 1);
    }

    @Test
    public void normalAllSkip() {
        Ix<Ix<Integer>> source = Ix.range(1, 5).window(5, 10);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(1, list.size());
        IxTestHelper.assertValues(list.get(0), 1, 2, 3, 4, 5);
    }

    @Test
    public void normalMoreSkip() {
        Ix<Ix<Integer>> source = Ix.range(1, 5).window(10, 15);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(1, list.size());
        IxTestHelper.assertValues(list.get(0), 1, 2, 3, 4, 5);
    }

    @Test
    public void justSkip() {
        Ix<Ix<Integer>> source = Ix.just(1).window(2, 3);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(1, list.size());
        IxTestHelper.assertValues(list.get(0), 1);
    }

    @Test
    public void emptySkip() {
        Ix<Ix<Integer>> source = Ix.<Integer>empty().window(2, 3);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(0, list.size());
    }

    @Test
    public void skipInnerMovesParent() {

        Ix<Ix<Integer>> source = Ix.range(1, 5).window(2, 3);

        Iterator<Ix<Integer>> it0 = source.iterator();

        Ix<Integer> inner = it0.next();

        Iterator<Integer> it1 = inner.iterator();

        try {
            inner.iterator();
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            Assert.assertEquals("This Window Ix iterable can be consumed only once.", ex.getMessage());
        }
        Assert.assertEquals(1, it1.next().intValue());
        Assert.assertEquals(2, it1.next().intValue());
        Assert.assertFalse(it1.hasNext());

        inner = it0.next();
        it1 = inner.iterator();

        Assert.assertEquals(4, it1.next().intValue());
        Assert.assertEquals(5, it1.next().intValue());
        Assert.assertFalse(it1.hasNext());

        Assert.assertFalse(it0.hasNext());
    }

    @Test
    public void normalOverlap() {
        Ix<Ix<Integer>> source = Ix.range(1, 5).window(2, 1);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(5, list.size());
        IxTestHelper.assertValues(list.get(0), 1, 2);
        IxTestHelper.assertValues(list.get(1), 2, 3);
        IxTestHelper.assertValues(list.get(2), 3, 4);
        IxTestHelper.assertValues(list.get(3), 4, 5);
        IxTestHelper.assertValues(list.get(4), 5);
    }

    @Test
    public void normalOverlap2() {
        Ix<Ix<Integer>> source = Ix.range(1, 5).window(3, 1);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(5, list.size());
        IxTestHelper.assertValues(list.get(0), 1, 2, 3);
        IxTestHelper.assertValues(list.get(1), 2, 3, 4);
        IxTestHelper.assertValues(list.get(2), 3, 4, 5);
        IxTestHelper.assertValues(list.get(3), 4, 5);
        IxTestHelper.assertValues(list.get(4), 5);
    }

    @Test
    public void normalOverlap3() {
        Ix<Ix<Integer>> source = Ix.range(1, 5).window(3, 2);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(3, list.size());
        IxTestHelper.assertValues(list.get(0), 1, 2, 3);
        IxTestHelper.assertValues(list.get(1), 3, 4, 5);
        IxTestHelper.assertValues(list.get(2), 5);
    }

    @Test
    public void nullExact() {
        Ix<Ix<Integer>> source = Ix.<Integer>fromArray(null, null, null, null, null).window(2);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(3, list.size());
        IxTestHelper.assertValues(list.get(0), null, null);
        IxTestHelper.assertValues(list.get(1), null, null);
        IxTestHelper.assertValues(list.get(2), (Integer)null);
    }

    @Test
    public void nullExact2() {
        Ix<Ix<Integer>> source = Ix.<Integer>fromArray(null, null, null, null, null).window(6);

        Iterator<Integer> list = source.iterator().next().iterator();

        Assert.assertNull(list.next());
        Assert.assertNull(list.next());
        Assert.assertNull(list.next());
        Assert.assertNull(list.next());
        Assert.assertNull(list.next());
        Assert.assertFalse(list.hasNext());
    }

    @Test
    public void nullSkip() {
        Ix<Ix<Integer>> source = Ix.<Integer>fromArray(null, null, null, null, null).window(2, 3);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(2, list.size());
        IxTestHelper.assertValues(list.get(0), null, null);
        IxTestHelper.assertValues(list.get(1), null, null);
    }

    @Test
    public void nullOverlap() {
        Ix<Ix<Integer>> source = Ix.<Integer>fromArray(null, null, null, null, null).window(2, 1);

        List<Ix<Integer>> list = source.collectToList().first();

        Assert.assertEquals(5, list.size());
        IxTestHelper.assertValues(list.get(0), null, null);
        IxTestHelper.assertValues(list.get(1), null, null);
        IxTestHelper.assertValues(list.get(2), null, null);
        IxTestHelper.assertValues(list.get(3), null, null);
        IxTestHelper.assertValues(list.get(4), (Integer)null);
    }

    @Test
    public void overlapInnerMovesParent() {

        Ix<Ix<Integer>> source = Ix.range(1, 5).window(2, 1);

        Iterator<Ix<Integer>> it0 = source.iterator();

        Ix<Integer> inner = it0.next();

        Iterator<Integer> it1 = inner.iterator();

        try {
            inner.iterator();
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            Assert.assertEquals("This Window Ix iterable can be consumed only once.", ex.getMessage());
        }
        Assert.assertEquals(1, it1.next().intValue());
        Assert.assertEquals(2, it1.next().intValue());
        Assert.assertFalse(it1.hasNext());

        inner = it0.next();
        it1 = inner.iterator();

        Assert.assertEquals(2, it1.next().intValue());
        Assert.assertEquals(3, it1.next().intValue());
        Assert.assertFalse(it1.hasNext());

        Assert.assertTrue(it0.hasNext());
    }

    @Test
    public void overlapInnerMovesParent2() {

        Ix<Ix<Integer>> source = Ix.range(1, 5).window(3, 2);

        Iterator<Ix<Integer>> it0 = source.iterator();

        Ix<Integer> inner = it0.next();

        Iterator<Integer> it1 = inner.iterator();

        try {
            inner.iterator();
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            Assert.assertEquals("This Window Ix iterable can be consumed only once.", ex.getMessage());
        }
        Assert.assertEquals(1, it1.next().intValue());
        Assert.assertEquals(2, it1.next().intValue());
        Assert.assertEquals(3, it1.next().intValue());
        Assert.assertFalse(it1.hasNext());

        inner = it0.next();
        it1 = inner.iterator();

        Assert.assertEquals(3, it1.next().intValue());
        Assert.assertEquals(4, it1.next().intValue());
        Assert.assertEquals(5, it1.next().intValue());
        Assert.assertFalse(it1.hasNext());

        Assert.assertTrue(it0.hasNext());
    }

    @Test
    public void overlapParentMoved() {

        Ix<Ix<Integer>> source = Ix.range(1, 5).window(4, 3);

        Iterator<Ix<Integer>> it0 = source.iterator();

        Ix<Integer> inner1 = it0.next();
        Ix<Integer> inner2 = it0.next();

        Assert.assertFalse(it0.hasNext());

        IxTestHelper.assertValues(inner1, 1, 2, 3, 4);
        IxTestHelper.assertValues(inner2, 4, 5);
    }

}
