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

public class SkipTest {
    @Test
    public void skipNegative() {
        List<Integer> list = Ix.range(1, 5).skip(-5).toList();
        
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
    }

    @Test
    public void skipZero() {
        List<Integer> list = Ix.range(1, 5).skip(0).toList();
        
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
    }

    @Test
    public void skipSome() {
        List<Integer> list = Ix.range(1, 5).skip(2).toList();
        
        Assert.assertEquals(Arrays.asList(3, 4, 5), list);
    }

    @Test
    public void skipAll() {
        List<Integer> list = Ix.range(1, 5).skip(5).toList();
        
        Assert.assertEquals(Arrays.asList(), list);
    }

    @Test
    public void skipMoreThanAll() {
        List<Integer> list = Ix.range(1, 5).skip(10).toList();
        
        Assert.assertEquals(Arrays.asList(), list);
    }

}
