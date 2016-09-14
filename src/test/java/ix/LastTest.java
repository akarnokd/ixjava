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

import java.util.NoSuchElementException;

import org.junit.*;

public class LastTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.range(1, 10);

        Assert.assertEquals(10, source.last().intValue());
    }

    @Test
    public void just() {
        Ix<Integer> source = Ix.just(1);

        Assert.assertEquals(1, source.last().intValue());
    }

    @Test(expected = NoSuchElementException.class)
    public void empty() {
        Ix<Integer> source = Ix.empty();

        Assert.assertEquals(1, source.last().intValue());
    }

    @Test
    public void emptyDefault() {
        Ix<Integer> source = Ix.empty();

        Assert.assertEquals(100, source.last(100).intValue());
    }

    @Test
    public void justDefault() {
        Ix<Integer> source = Ix.just(1);

        Assert.assertEquals(1, source.last(100).intValue());
    }

    @Test
    public void rangeDefault() {
        Ix<Integer> source = Ix.range(1, 10);

        Assert.assertEquals(10, source.last(100).intValue());
    }

}
