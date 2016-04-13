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

import rx.functions.Func2;

public class ReduceTest {

    @Test
    public void single() {
        
        Object[] a = Ix.just(1).reduce(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer a, Integer b) {
                return a + b;
            }
        }).toArray();
        
        Assert.assertArrayEquals(new Object[] { 1 }, a);
    }

    @Test
    public void range() {
        
        Object[] a = Ix.range(1, 3).reduce(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer a, Integer b) {
                return a + b;
            }
        }).toArray();
        
        Assert.assertArrayEquals(new Object[] { 6 }, a);
    }

    @Test
    public void empty() {
        
        Object[] a = Ix.<Integer>empty().reduce(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer a, Integer b) {
                return a + b;
            }
        }).toArray();
        
        Assert.assertArrayEquals(new Object[] { }, a);
    }
}
