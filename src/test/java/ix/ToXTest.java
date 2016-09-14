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

public class ToXTest {

    @Test
    public void cast() {
        Ix<Object> source = Ix.just(1).cast(Object.class);

        IxTestHelper.assertValues(source, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test(expected = ClassCastException.class)
    public void castInvalid() {
        Ix<String> source = Ix.just(1).cast(String.class);

        String s = source.first();

        Assert.assertEquals("1", s);
    }

    @Test
    public void toArray() {

        Assert.assertArrayEquals(new Object[] {1, 2, 3, 4, 5}, Ix.range(1, 5).toArray());

    }

    @Test
    public void toArrayTemplate() {

        Assert.assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, Ix.range(1, 5).toArray(new Integer[5]));

    }

    @Test
    public void toList() {
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), Ix.range(1, 5).toList());

    }

    @Test
    public void toSet() {
        Assert.assertEquals(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5)), Ix.range(1, 5).toSet());

    }

    @Test
    public void toMap() {
        Map<Integer, Integer> map = Ix.range(1, 5).toMap(IdentityHelper.<Integer>instance());

        Map<Integer, Integer> expected = new HashMap<Integer, Integer>();
        expected.put(1, 1);
        expected.put(2, 2);
        expected.put(3, 3);
        expected.put(4, 4);
        expected.put(5, 5);

        Assert.assertEquals(expected, map);
    }


    @Test
    public void toMapCustomValues() {
        Map<Integer, Integer> map = Ix.range(1, 5).toMap(IdentityHelper.<Integer>instance(), new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v * v;
            }
        });

        Map<Integer, Integer> expected = new HashMap<Integer, Integer>();
        expected.put(1, 1);
        expected.put(2, 4);
        expected.put(3, 9);
        expected.put(4, 16);
        expected.put(5, 25);

        Assert.assertEquals(expected, map);
    }

    @Test
    public void toMultimap() {
        Map<Integer, Collection<Integer>> map = Ix.range(1, 5).toMultimap(IdentityHelper.<Integer>instance());

        Map<Integer, Collection<Integer>> expected = new HashMap<Integer, Collection<Integer>>();
        expected.put(1, Collections.singletonList(1));
        expected.put(2, Collections.singletonList(2));
        expected.put(3, Collections.singletonList(3));
        expected.put(4, Collections.singletonList(4));
        expected.put(5, Collections.singletonList(5));

        Assert.assertEquals(expected, map);
    }


    @Test
    public void toMultimapCustomValues() {
        Map<Integer, Collection<Integer>> map = Ix.range(1, 5).toMultimap(IdentityHelper.<Integer>instance(), new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v * v;
            }
        });

        Map<Integer, Collection<Integer>> expected = new HashMap<Integer, Collection<Integer>>();
        expected.put(1, Collections.singletonList(1));
        expected.put(2, Collections.singletonList(4));
        expected.put(3, Collections.singletonList(9));
        expected.put(4, Collections.singletonList(16));
        expected.put(5, Collections.singletonList(25));

        Assert.assertEquals(expected, map);
    }
}
