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

import java.util.Iterator;

import org.junit.*;

public class TransformTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.just(1).transform(new IxTransform<Integer, Integer>() {
            @Override
            public int moveNext(Iterator<Integer> it, IxConsumer<? super Integer> out) {
                if (it.hasNext()) {
                    out.accept(it.next() + 10);
                    return IxTransform.NEXT;
                }
                out.accept(12);
                return IxTransform.LAST;
            }
        });

        IxTestHelper.assertValues(source, 11, 12);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void stop() {
        Ix<Integer> source = Ix.just(1).transform(new IxTransform<Integer, Integer>() {
            @Override
            public int moveNext(Iterator<Integer> it, IxConsumer<? super Integer> out) {
                return IxTransform.STOP;
            }
        });

        IxTestHelper.assertValues(source);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void doubleNext() {
        try {
            Ix<Integer> source = Ix.just(1).transform(new IxTransform<Integer, Integer>() {
                @Override
                public int moveNext(Iterator<Integer> it, IxConsumer<? super Integer> out) {
                    out.accept(1);
                    out.accept(2);
                    return IxTransform.NEXT;
                }
            });

            IxTestHelper.assertValues(source);

            Assert.fail("Failed to throw IllegalStateException");
        } catch (IllegalStateException ex) {
            Assert.assertEquals("Value already set in this turn!", ex.getMessage());
        }
    }

    @Test
    public void returnNextWithoutValue() {
        try {
            Ix<Integer> source = Ix.just(1).transform(new IxTransform<Integer, Integer>() {
                @Override
                public int moveNext(Iterator<Integer> it, IxConsumer<? super Integer> out) {
                    return IxTransform.NEXT;
                }
            });

            IxTestHelper.assertValues(source);

            Assert.fail("Failed to throw IllegalStateException");
        } catch (IllegalStateException ex) {
            Assert.assertEquals("No value set!", ex.getMessage());
        }
    }
}
