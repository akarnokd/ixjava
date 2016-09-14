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

public class HelperTest {

    @Test
    public void toListHelper() {
        Assert.assertEquals(1, ToListHelper.values().length);

        Assert.assertNotNull(ToListHelper.valueOf("INSTANCE"));
    }

    @Test
    public void identityHelper() {
        Assert.assertEquals(1, IdentityHelper.values().length);

        Assert.assertNotNull(IdentityHelper.valueOf("INSTANCE"));
    }

    @Test
    public void numberToLongHelper() {
        Assert.assertEquals(1, NumberToLongHelper.values().length);

        Assert.assertNotNull(NumberToLongHelper.valueOf("INSTANCE"));
    }
}
