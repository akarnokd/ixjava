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

import java.util.List;

import org.junit.Test;

public class RemoveRetainTest {

    @Test
    public void removeNormal() {
        List<Integer> list = IxTestHelper.range(1, 10);

        Ix<Integer> source = Ix.from(list).remove(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return (v & 1) != 0;
            }
        });

        IxTestHelper.assertValues(source, 2, 4, 6, 8, 10);
        IxTestHelper.assertValues(list, 2, 4, 6, 8, 10);
    }

    @Test
    public void removeAll() {
        List<Integer> list = IxTestHelper.range(1, 10);

        Ix<Integer> source = Ix.from(list).remove(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return true;
            }
        });

        IxTestHelper.assertValues(source);
        IxTestHelper.assertValues(list);
    }

    @Test
    public void removeNone() {
        List<Integer> list = IxTestHelper.range(1, 10);

        Ix<Integer> source = Ix.from(list).remove(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return false;
            }
        });

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        IxTestHelper.assertValues(list, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void removeEmpty() {
        List<Integer> list = IxTestHelper.range(1, 0);

        Ix<Integer> source = Ix.from(list).remove(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return true;
            }
        });

        IxTestHelper.assertValues(source);
        IxTestHelper.assertValues(list);
    }

    @Test
    public void retainNormal() {
        List<Integer> list = IxTestHelper.range(1, 10);

        Ix<Integer> source = Ix.from(list).retain(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return (v & 1) != 0;
            }
        });

        IxTestHelper.assertValues(source, 1, 3, 5, 7, 9);
        IxTestHelper.assertValues(list, 1, 3, 5, 7, 9);
    }

    @Test
    public void retainAll() {
        List<Integer> list = IxTestHelper.range(1, 10);

        Ix<Integer> source = Ix.from(list).retain(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return true;
            }
        });

        IxTestHelper.assertValues(source, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        IxTestHelper.assertValues(list, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void retainNone() {
        List<Integer> list = IxTestHelper.range(1, 10);

        Ix<Integer> source = Ix.from(list).retain(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return false;
            }
        });

        IxTestHelper.assertValues(source);
        IxTestHelper.assertValues(list);
    }

    @Test
    public void retainEmpty() {
        List<Integer> list = IxTestHelper.range(1, 0);

        Ix<Integer> source = Ix.from(list).retain(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return false;
            }
        });

        IxTestHelper.assertValues(source);
        IxTestHelper.assertValues(list);
    }

    @Test
    public void removeAllDouble() {
        List<Integer> list = IxTestHelper.range(1, 10);

        Ix<Integer> source = Ix.from(list).remove(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return (v & 1) != 0;
            }
        }).remove(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return (v & 1) == 0;
            }
        });

        IxTestHelper.assertValues(source);
        IxTestHelper.assertValues(list);
    }

    @Test
    public void retainNoneDouble() {
        List<Integer> list = IxTestHelper.range(1, 10);

        Ix<Integer> source = Ix.from(list).retain(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return (v & 1) != 0;
            }
        }).retain(new IxPredicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return (v & 1) == 0;
            }
        });

        IxTestHelper.assertValues(source);
        IxTestHelper.assertValues(list);
    }
}
