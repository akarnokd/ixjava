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

import org.junit.*;

public class CharactersTest {

    @Test
    public void normal() {
        Ix<Integer> source = Ix.characters("Hello world!");

        IxTestHelper.assertValues(source,
                (int)'H', (int)'e', (int)'l', (int)'l',
                        (int)'o', (int)' ', (int)'w', (int)'o', (int)'r', (int)'l',
                        (int)'d', (int)'!');

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void normalRange() {
        Ix<Integer> source = Ix.characters("Hello world!", 2, 8);

        IxTestHelper.assertValues(source,
                (int)'l', (int)'l',
                        (int)'o', (int)' ', (int)'w', (int)'o');

        IxTestHelper.assertNoRemove(source);
    }

    @Test
    public void rangeChecks() {
        String s = "Hello world";

        try {
            Ix.characters(s, -1, 1);
            Assert.fail("Failed to throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            Assert.assertEquals("start=-1, end=1, length=11", ex.getMessage());
        }

        try {
            Ix.characters(s, 1, -1);
            Assert.fail("Failed to throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            Assert.assertEquals("start=1, end=-1, length=11", ex.getMessage());
        }

        try {
            Ix.characters(s, 12, -1);
            Assert.fail("Failed to throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            Assert.assertEquals("start=12, end=-1, length=11", ex.getMessage());
        }

        try {
            Ix.characters(s, 1, 12);
            Assert.fail("Failed to throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            Assert.assertEquals("start=1, end=12, length=11", ex.getMessage());
        }

        try {
            Ix.characters(s, 12, 12);
            Assert.fail("Failed to throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            Assert.assertEquals("start=12, end=12, length=11", ex.getMessage());
        }
    }
}
