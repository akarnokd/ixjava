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

import ix.internal.operators.Interactive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test the concat operator.
 */
public class TestInteractiveConcat {
	/**
	 * Test if concat works correctly when one of the input sequences is empty. 
	 */
	@Test
	public void testConcat() {
		Iterable<Integer> one = Collections.singleton(1);
		Iterable<Integer> empty = Collections.emptySet();
		
		List<Iterable<Integer>> iterables = new ArrayList<Iterable<Integer>>();
		iterables.add(one);
		iterables.add(empty);
		iterables.add(one);
		
		Iterable<Integer> concat = Interactive.concat(iterables);

		List<Integer> result = new ArrayList<Integer>();
		for (Integer i : concat) {
			result.add(i);
		}
		TestUtil.assertEqual(Arrays.asList(1, 1), result);
	}
	/**
	 * Test if concat item removal works when one of the input sequences is empty.
	 */
	@Test
	public void testConcatRemove() {
		List<List<Integer>> iterables = new ArrayList<List<Integer>>();
		
		List<Integer> list1 = TestUtil.newList(1);
		List<Integer> list2 = TestUtil.newList(1);
		iterables.add(list1);
		iterables.add(Collections.<Integer>emptyList());
		iterables.add(list2);
		
		Iterable<Integer> concat = Interactive.concat(iterables);
		
		Iterator<Integer> it = concat.iterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
		
		Assert.assertEquals(0, list1.size());
		Assert.assertEquals(0, list2.size());
	}

}
