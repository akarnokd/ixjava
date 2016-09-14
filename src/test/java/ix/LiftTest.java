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

import org.junit.Test;

public class LiftTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.just(1).lift(new IxFunction<Iterator<Integer>, Iterator<Integer>>() {
            @Override
            public Iterator<Integer> apply(final Iterator<Integer> it) {
                return new Iterator<Integer>() {
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public Integer next() {
                        return it.next() + 10;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        });

        IxTestHelper.assertValues(source, 11);

        IxTestHelper.assertNoRemove(source);
    }
}
