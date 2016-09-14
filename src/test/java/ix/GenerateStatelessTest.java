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

public class GenerateStatelessTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.generate(new IxConsumer<IxEmitter<Integer>>() {
            int count;
            @Override
            public void accept(IxEmitter<Integer> t) {
                t.onNext(++count);
                if (count == 10) {
                    t.onComplete();
                }
            }
        });

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void empty() {
        Ix<Integer> source = Ix.generate(new IxConsumer<IxEmitter<Integer>>() {
            @Override
            public void accept(IxEmitter<Integer> t) {
                t.onComplete();
            }
        });

        IxTestHelper.assertValues(source);
    }

    @Test(expected = IllegalStateException.class)
    public void never() {
        Ix<Integer> source = Ix.generate(new IxConsumer<IxEmitter<Integer>>() {
            @Override
            public void accept(IxEmitter<Integer> t) {
            }
        });

        IxTestHelper.assertValues(source);
    }

    @Test(expected = IllegalArgumentException.class)
    public void runtimeError() {
        Ix<Integer> source = Ix.generate(new IxConsumer<IxEmitter<Integer>>() {
            @Override
            public void accept(IxEmitter<Integer> t) {
                throw new IllegalArgumentException();
            }
        });

        IxTestHelper.assertValues(source);
    }

    @Test(expected = InternalError.class)
    public void error() {
        Ix<Integer> source = Ix.generate(new IxConsumer<IxEmitter<Integer>>() {
            @Override
            public void accept(IxEmitter<Integer> t) {
                throw new InternalError();
            }
        });

        IxTestHelper.assertValues(source);
    }

    @Test(expected = RuntimeException.class)
    public void exceptionError() {
        Ix<Integer> source = Ix.generate(new IxConsumer<IxEmitter<Integer>>() {
            @Override
            public void accept(IxEmitter<Integer> t) {
                throw new RuntimeException(new IOException());
            }
        });

        IxTestHelper.assertValues(source);
    }

}
