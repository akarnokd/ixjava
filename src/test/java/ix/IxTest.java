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
import java.util.concurrent.Callable;

import org.junit.*;

public class IxTest {

    @Test(expected = RuntimeException.class)
    public void checkedCallWraps() {

        Ix.checkedCall(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                throw new IOException();
            }
        });
    }

    @Test(expected = InternalError.class)
    public void checkedCallNoWrapsError() {

        Ix.checkedCall(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                throw new InternalError();
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkedCallNoWrapping() {

        Ix.checkedCall(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                throw new IllegalArgumentException();
            }
        });
    }

    @Test
    public void nullCheck() {
        Ix.nullCheck(1, "Should not fail");
        try {
            Ix.nullCheck(null, "Failure");
            Assert.fail("Failed to throw NPE");
        } catch (NullPointerException ex) {
            Assert.assertEquals("Failure", ex.getMessage());
        }
    }

    @Test
    public void nonNegativeLong() {
        Ix.nonNegative(0L, "n");
        Ix.nonNegative(1L, "n");

        try {
            Ix.nonNegative(-99L, "n");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("n >= 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void nonNegativeInt() {
        Ix.nonNegative(0, "n");
        Ix.nonNegative(1, "n");

        try {
            Ix.nonNegative(-99, "n");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("n >= 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void positiveInt() {
        Ix.nonNegative(1, "n");

        try {
            Ix.positive(0, "n");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("n > 0 required but it was 0", ex.getMessage());
        }

        try {
            Ix.positive(-99, "n");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("n > 0 required but it was -99", ex.getMessage());
        }
    }

}
