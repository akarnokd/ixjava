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

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.*;

import rx.Observer;
import rx.functions.*;

public class GenerateTest {

    Func0<Integer> stateFactory = new Func0<Integer>() {
        @Override
        public Integer call() {
            return 0;
        }
    };
    
    @Test
    public void normal() {
        Ix<Integer> source = Ix.generate(stateFactory, new Func2<Integer, Observer<Integer>, Integer>() {
            @Override
            public Integer call(Integer s, Observer<Integer> t) {
                int i = ++s;
                t.onNext(i);
                if (i == 10) {
                    t.onCompleted();
                }
                return i;
            }
        });
        
        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }
    
    @Test
    public void normalState() {
        final AtomicInteger value = new AtomicInteger();
        
        Ix<Integer> source = Ix.generate(stateFactory, new Func2<Integer, Observer<Integer>, Integer>() {
            @Override
            public Integer call(Integer s, Observer<Integer> t) {
                int i = ++s;
                t.onNext(i);
                if (i == 10) {
                    t.onCompleted();
                }
                return i;
            }
        }, new Action1<Integer>() {
            @Override
            public void call(Integer t) {
                value.set(t);
            }
        });
        
        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        Assert.assertEquals(10, value.get());
    }
    
    @Test
    public void empty() {
        Ix<Integer> source = Ix.generate(stateFactory, new Func2<Integer, Observer<Integer>, Integer>() {
            @Override
            public Integer call(Integer s, Observer<Integer> t) {
                t.onCompleted();
                return s;
            }
        });
        
        IxTestHelper.assertValues(source);
    }
    
    @Test(expected = IllegalStateException.class)
    public void never() {
        Ix<Integer> source = Ix.generate(stateFactory, new Func2<Integer, Observer<Integer>, Integer>() {
            @Override
            public Integer call(Integer s, Observer<Integer> t) {
                return s;
            }
        });
        
        IxTestHelper.assertValues(source);
    }

    @Test(expected = IllegalArgumentException.class)
    public void runtimeError() {
        Ix<Integer> source = Ix.generate(stateFactory, new Func2<Integer, Observer<Integer>, Integer>() {
            @Override
            public Integer call(Integer s, Observer<Integer> t) {
                t.onError(new IllegalArgumentException());
                return s;
            }
        });
        
        IxTestHelper.assertValues(source);
    }

    @Test(expected = InternalError.class)
    public void error() {
        Ix<Integer> source = Ix.generate(stateFactory, new Func2<Integer, Observer<Integer>, Integer>() {
            @Override
            public Integer call(Integer s, Observer<Integer> t) {
                t.onError(new InternalError());
                return s;
            }
        });
        
        IxTestHelper.assertValues(source);
    }

    @Test(expected = RuntimeException.class)
    public void exceptionError() {
        Ix<Integer> source = Ix.generate(stateFactory, new Func2<Integer, Observer<Integer>, Integer>() {
            @Override
            public Integer call(Integer s, Observer<Integer> t) {
                t.onError(new IOException());
                return s;
            }
        });
        
        IxTestHelper.assertValues(source);
    }

}
