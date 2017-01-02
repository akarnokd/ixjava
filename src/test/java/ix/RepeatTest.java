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

import java.util.concurrent.Callable;

import org.junit.Test;

public class RepeatTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.repeatValue(1).take(5);

        IxTestHelper.assertValues(source, 1, 1, 1, 1, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalLimited() {
        Ix<Integer> source = Ix.repeatValue(1, 5);

        IxTestHelper.assertValues(source, 1, 1, 1, 1, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void neverRepeat() {
        Ix<Integer> source = Ix.repeatValue(1, 0);

        IxTestHelper.assertValues(source);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void repeatOnce() {
        Ix<Integer> source = Ix.repeatValue(1, 1);

        IxTestHelper.assertValues(source, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void predicateLimited() {
        Ix<Integer> source = Ix.repeatValue(1, new IxBooleanSupplier() {
            int count = 5;
            @Override
            public boolean getAsBoolean() {
                return count-- == 0;
            }
        });

        IxTestHelper.assertValues(source, 1, 1, 1, 1, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void predicateNeverRepeat() {
        Ix<Integer> source = Ix.repeatValue(1, new IxBooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return true;
            }
        });

        IxTestHelper.assertValues(source);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void predicateRepeatOnce() {
        Ix<Integer> source = Ix.repeatValue(1, new IxBooleanSupplier() {
            int count = 1;
            @Override
            public boolean getAsBoolean() {
                return count-- == 0;
            }
        });

        IxTestHelper.assertValues(source, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void predicateInfinite() {
        Ix<Integer> source = Ix.repeatValue(1, new IxBooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return false;
            }
        }).take(5);

        IxTestHelper.assertValues(source, 1, 1, 1, 1, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void predicateCountedLimited() {
        Ix<Integer> source = Ix.repeatValue(1, 3L, new IxBooleanSupplier() {
            int count = 5;
            @Override
            public boolean getAsBoolean() {
                return count-- == 0;
            }
        });

        IxTestHelper.assertValues(source, 1, 1, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalInstance() {
        Ix<Integer> source = Ix.just(1).repeat().take(5);

        IxTestHelper.assertValues(source, 1, 1, 1, 1, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalInstanceLimited() {
        Ix<Integer> source = Ix.just(1).repeat(5);

        IxTestHelper.assertValues(source, 1, 1, 1, 1, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void neverRepeatInstance() {
        Ix<Integer> source = Ix.just(1).repeat(0);

        IxTestHelper.assertValues(source);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void repeatOnceInstance() {
        Ix<Integer> source = Ix.just(1).repeat(1);

        IxTestHelper.assertValues(source, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void predicateLimitedInstance() {
        Ix<Integer> source = Ix.just(1).repeat(new IxBooleanSupplier() {
            int count = 5;
            @Override
            public boolean getAsBoolean() {
                return count-- == 0;
            }
        });

        IxTestHelper.assertValues(source, 1, 1, 1, 1, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void predicateNeverRepeatInstance() {
        Ix<Integer> source = Ix.just(1).repeat(new IxBooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return true;
            }
        });

        IxTestHelper.assertValues(source);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void predicateRepeatOnceInstance() {
        Ix<Integer> source = Ix.just(1).repeat(new IxBooleanSupplier() {
            int count = 1;
            @Override
            public boolean getAsBoolean() {
                return count-- == 0;
            }
        });

        IxTestHelper.assertValues(source, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void predicateInfiniteInstance() {
        Ix<Integer> source = Ix.just(1).repeat(new IxBooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return false;
            }
        }).take(5);

        IxTestHelper.assertValues(source, 1, 1, 1, 1, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void predicateCountedLimitedInstance() {
        Ix<Integer> source = Ix.just(1).repeat(3L, new IxBooleanSupplier() {
            int count = 5;
            @Override
            public boolean getAsBoolean() {
                return count-- == 0;
            }
        });

        IxTestHelper.assertValues(source, 1, 1, 1);

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void repeatCallable() {
        Ix<Integer> source = Ix.repeatCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 1;
            }
        })
        .take(5);

        IxTestHelper.assertValues(source, 1, 1, 1, 1, 1);

        IxTestHelper.assertNoRemove(source);
    }
}
