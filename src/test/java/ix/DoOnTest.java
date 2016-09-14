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

public class DoOnTest {

    @Test
    public void normal() {
        final List<Integer> values = new ArrayList<Integer>();
        Ix<Integer> source = Ix.range(1, 5)
                .doOnNext(new IxConsumer<Integer>() {
                    @Override
                    public void accept(Integer v) {
                        values.add(v);
                    }
                })
                .doOnCompleted(new Runnable() {
                    @Override
                    public void run() {
                        values.add(100);
                    }
                })
                ;
        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 100), values);
    }

    @Test
    public void empty() {
        final List<Integer> values = new ArrayList<Integer>();
        Ix<Integer> source = Ix.<Integer>empty()
                .doOnNext(new IxConsumer<Integer>() {
                    @Override
                    public void accept(Integer v) {
                        values.add(v);
                    }
                })
                .doOnCompleted(new Runnable() {
                    @Override
                    public void run() {
                        values.add(100);
                    }
                })
                ;
        IxTestHelper.assertValues(source);
        Assert.assertEquals(Arrays.asList(100), values);
    }
}
