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

import rx.functions.*;

public class CollectTest {

    @Test
    public void single() {
        
        Object[] a = Ix.just(1).collect(new Func0<Collection<Integer>>() {
            @Override
            public Collection<Integer> call() {
                return new ArrayList<Integer>();
            }
        }, new Action2<Collection<Integer>, Integer>() {
            @Override
            public void call(Collection<Integer> a, Integer b) {
                a.add(b);
            }
        }).toArray();
        
        Assert.assertArrayEquals(new Object[] { Collections.singletonList(1) }, a);
    }

    @Test
    public void range() {
        
        Object[] a = Ix.range(1, 3).collect(new Func0<Collection<Integer>>() {
            @Override
            public Collection<Integer> call() {
                return new ArrayList<Integer>();
            }
        }, new Action2<Collection<Integer>, Integer>() {
            @Override
            public void call(Collection<Integer> a, Integer b) {
                a.add(b);
            }
        }).toArray();
        
        Assert.assertArrayEquals(new Object[] { Arrays.asList(1, 2, 3) }, a);
    }

    @Test
    public void empty() {
        
        Object[] a = Ix.<Integer>empty().collect(new Func0<Collection<Integer>>() {
            @Override
            public Collection<Integer> call() {
                return new ArrayList<Integer>();
            }
        }, new Action2<Collection<Integer>, Integer>() {
            @Override
            public void call(Collection<Integer> a, Integer b) {
                a.add(b);
            }
        }).toArray();
        
        Assert.assertArrayEquals(new Object[] { Collections.emptyList() }, a);
    }
}
