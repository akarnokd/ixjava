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

import static ix.SelfComparator.INSTANCE;
import static org.junit.Assert.*;

import org.junit.Test;

public class SelfComparatorTest {

    @Test
    public void normal() {
        assertEquals(0, INSTANCE.compare(1, 1));
        assertEquals(0, INSTANCE.compare(1, new Integer(1)));

        assertTrue(INSTANCE.compare(1, 2) < 0);

        assertTrue(INSTANCE.compare(2, 1) > 0);

        assertNotNull(SelfComparator.valueOf("INSTANCE"));

        assertEquals(1, SelfComparator.values().length);
    }
}
