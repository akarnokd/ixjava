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

import org.junit.*;

public class SourceQueuedIteratorTest {

    IxSourceQueuedIterator<Integer, Integer, Integer> it;

    @Before
    public void before() {
        it = new IxSourceQueuedIterator<Integer, Integer, Integer>(Ix.<Integer>empty().iterator()) {

            @Override
            protected boolean moveNext() {
                return false;
            }
        };
    }

    @Test
    public void normal() {
        Assert.assertTrue(it.isEmpty());
        Assert.assertEquals(null, it.peek());

        it.offer(1);

        Assert.assertFalse(it.isEmpty());

        Assert.assertEquals(1, it.peek());
        Assert.assertEquals(1, it.poll());
        Assert.assertTrue(it.isEmpty());
        Assert.assertEquals(null, it.peek());

        Assert.assertNull(it.poll());
        Assert.assertTrue(it.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void nullOffer() {
        it.offer(null);
    }

    @Test
    public void clear() {
        Assert.assertTrue(it.isEmpty());

        it.offer(1);

        Assert.assertFalse(it.isEmpty());

        it.clear();

        Assert.assertNull(it.poll());
        Assert.assertTrue(it.isEmpty());
    }

    @Test
    public void nullWraps() {
        Assert.assertSame(IxSourceQueuedIterator.NULL,  it.toObject(null));
    }

    @Test
    public void nullUnwraps() {
        Assert.assertNull(it.fromObject(IxSourceQueuedIterator.NULL));
    }

    @Test
    public void foreach() {
        final int[] count = { 0 };
        it.<Object>foreach(new IxConsumer2<Integer, Object>() {
            @Override
            public void accept(Integer a, Object b) {
                count[0]++;
            }
        }, null);

        Assert.assertEquals(0, count[0]);
    }
}
