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

public class ToMapTest {

    @SuppressWarnings("unchecked")
    @Test
    public void normal() {
        Ix<Map<Integer, Integer>> source = Ix.range(1, 5).collectToMap(new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v % 3;
            }
        });

        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(1, 4);
        map.put(0, 3);
        map.put(2, 5);

        IxTestHelper.assertValues(source, map);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void normalValueSelector() {
        Ix<Map<Integer, Integer>> source = Ix.range(1, 5).collectToMap(new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v % 3;
            }
        }, new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v * v;
            }
        });

        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(1, 16);
        map.put(0, 9);
        map.put(2, 25);

        IxTestHelper.assertValues(source, map);
    }
    @SuppressWarnings("unchecked")
    @Test
    public void empty() {
        Ix<Map<Integer, Integer>> source = Ix.<Integer>empty().collectToMap(new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v % 3;
            }
        });

        Map<Integer, Integer> map = new HashMap<Integer, Integer>();

        IxTestHelper.assertValues(source, map);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void multimap() {
        Ix<Map<Integer, Collection<Integer>>> source = Ix.range(1, 5).collectToMultimap(new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v % 3;
            }
        });

        Map<Integer, Collection<Integer>> map = new HashMap<Integer, Collection<Integer>>();
        map.put(1, Arrays.asList(1, 4));
        map.put(0, Arrays.asList(3));
        map.put(2, Arrays.asList(2, 5));

        IxTestHelper.assertValues(source, map);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void multimapValueSelector() {
        Ix<Map<Integer, Collection<Integer>>> source = Ix.range(1, 5).collectToMultimap(new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v % 3;
            }
        }, new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v * v;
            }
        });

        Map<Integer, Collection<Integer>> map = new HashMap<Integer, Collection<Integer>>();
        map.put(1, Arrays.asList(1, 16));
        map.put(0, Arrays.asList(9));
        map.put(2, Arrays.asList(4, 25));

        IxTestHelper.assertValues(source, map);
    }
    @SuppressWarnings("unchecked")
    @Test
    public void multimapEmpty() {
        Ix<Map<Integer, Collection<Integer>>> source = Ix.<Integer>empty().collectToMultimap(new IxFunction<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v % 3;
            }
        });

        Map<Integer, Collection<Integer>> map = new HashMap<Integer, Collection<Integer>>();

        IxTestHelper.assertValues(source, map);
    }
}
