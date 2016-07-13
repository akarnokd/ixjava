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

import org.junit.Test;

import rx.functions.Func1;

public class ForloopTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.forloop(1, new Pred<Integer>() {
            @Override
            public boolean test(Integer i) {
                return i <= 5;
            }
        }, new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer i) {
                return i + 1;
            }
        }, new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer i) {
                return i * i;
            }
        });
        
        IxTestHelper.assertValues(source, 1, 4, 9, 16, 25);
        
        IxTestHelper.assertNoRemove(source);
    }
    
    @Test
    public void empty() {
        Ix<Integer> source = Ix.forloop(1, new Pred<Integer>() {
            @Override
            public boolean test(Integer i) {
                return false;
            }
        }, new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer i) {
                return i + 1;
            }
        }, new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer i) {
                return i * i;
            }
        });
        
        IxTestHelper.assertValues(source);
        
        IxTestHelper.assertNoRemove(source);
    }
}
