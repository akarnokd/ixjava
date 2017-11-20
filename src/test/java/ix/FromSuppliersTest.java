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

public class FromSuppliersTest {

	@Test
	public void normal() {
		IxSupplier<Integer> _1 = new IxSupplier<Integer>() {
			@Override
			public Integer get() {
				return 1;
			}
		};
		IxSupplier<Integer> _2 = new IxSupplier<Integer>() {
			@Override
			public Integer get() {
				return 2;
			}
		};
		IxSupplier<Integer> _3 = new IxSupplier<Integer>() {
			@Override
			public Integer get() {
				return 3;
			}
		};
		Ix<Integer> source = Ix.fromSuppliers(_1,_2,_3);

		IxTestHelper.assertValues(source, 1, 2, 3);

		IxTestHelper.assertNoRemove(source);
	}

	@Test
	public void empty() {
		Ix<Integer> source = Ix.fromArray();

		Assert.assertSame(source.getClass().toString(), source, Ix.empty());
	}

}
