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

import org.junit.Test;

import rx.Observer;
import rx.functions.Action1;

public class GenerateStatelessTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.generate(new Action1<Observer<Integer>>() {
            int count;
            @Override
            public void call(Observer<Integer> t) {
                t.onNext(++count);
                if (count == 10) {
                    t.onCompleted();
                }
            }
        });
        
        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }
    
    @Test
    public void empty() {
        Ix<Integer> source = Ix.generate(new Action1<Observer<Integer>>() {
            @Override
            public void call(Observer<Integer> t) {
                t.onCompleted();
            }
        });
        
        IxTestHelper.assertValues(source);
    }
    
    @Test(expected = IllegalStateException.class)
    public void never() {
        Ix<Integer> source = Ix.generate(new Action1<Observer<Integer>>() {
            @Override
            public void call(Observer<Integer> t) {
            }
        });
        
        IxTestHelper.assertValues(source);
    }

    @Test(expected = IllegalArgumentException.class)
    public void runtimeError() {
        Ix<Integer> source = Ix.generate(new Action1<Observer<Integer>>() {
            @Override
            public void call(Observer<Integer> t) {
                t.onError(new IllegalArgumentException());
            }
        });
        
        IxTestHelper.assertValues(source);
    }

    @Test(expected = InternalError.class)
    public void error() {
        Ix<Integer> source = Ix.generate(new Action1<Observer<Integer>>() {
            @Override
            public void call(Observer<Integer> t) {
                t.onError(new InternalError());
            }
        });
        
        IxTestHelper.assertValues(source);
    }

    @Test(expected = RuntimeException.class)
    public void exceptionError() {
        Ix<Integer> source = Ix.generate(new Action1<Observer<Integer>>() {
            @Override
            public void call(Observer<Integer> t) {
                t.onError(new IOException());
            }
        });
        
        IxTestHelper.assertValues(source);
    }

}
