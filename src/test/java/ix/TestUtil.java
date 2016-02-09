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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;

/**
 * Test utility methods.
 */
public final class TestUtil {
	/** Utility class. */
	private TestUtil() { }
	
	/**
	 * Returns a user-friendly textual representation of the given sequence.
	 * @param source the source sequence
	 * @return the output text
	 */
	public static String makeString(
			Iterable<?> source) {
		Iterator<String> iterator = Ix.from(source).join(", ").iterator();
		return iterator.hasNext() ? iterator.next() : "";
	}
	/**
	 * Assert the equivalence of two sequences.
	 * @param <T> the element type
	 * @param expected the expected sequence
	 * @param actual the actual sequence
	 */
	public static <T> void assertEqual(
			Iterable<? extends T> expected, 
			Iterable<? extends T> actual) {
		assertCompare(expected, actual, true);
	}
	/**
	 * Assert the inequivalence of two sequences.
	 * @param <T> the element type
	 * @param expected the expected sequence
	 * @param actual the actual sequence
	 */
	public static <T> void assertNotEqual(
			Iterable<? extends T> expected, 
			Iterable<? extends T> actual) {
		assertCompare(expected, actual, false);
	}
	/**
	 * Creates a new ArrayList from the elements.
	 * @param <T> the element type
	 * @param elements the elements
	 * @return the list
	 */
	public static <T> List<T> newList(T... elements) {
		return new ArrayList<T>(Arrays.asList(elements));
	}
	/**
	 * Compare two sequences and assert their equivalence.
	 * @param <T> the element type
	 * @param expected the expected sequence
	 * @param actual the actual sequence
	 * @param eq should they equal?
	 */
	public static <T> void assertCompare(
			Iterable<? extends T> expected, 
			Iterable<? extends T> actual, boolean eq) {
		List<? extends T> expectedList = Ix.from(expected).toList();
		List<? extends T> actualList = Ix.from(actual).toList();
		if (eq != expectedList.equals(actualList)) {
			fail(expectedList, actualList);
		}
	}
	/**
	 * Calls the Assert.fail with a message that displays the expected and actual values as strings.
	 * @param expected the expected sequence
	 * @param actual the actual sequence
	 */
	public static void fail(
			Iterable<?> expected, 
			Iterable<?> actual) {
		Assert.fail("Sequences mismatch: expected = " + makeString(expected) + ", actual = " + makeString(actual));
	}
	/**
	 * Test if the actual object is assignable to the expected class.
	 * @param expectedClass the expected class
	 * @param actual the actual object
	 */
	public static void assertInstanceof(Class<?> expectedClass, Object actual) {
		if (!expectedClass.isInstance(actual)) {
			Assert.fail("Not instance, expected = " + expectedClass + ", actual = " + (actual != null ? actual.getClass() : "null"));
		}
	}
	/**
	 * Test if the actual object is assignable to the expected class.
	 * @param expectedClass the expected class
	 * @param actual the actual object
	 */
	public static void assertNotInstanceof(Class<?> expectedClass, Object actual) {
		if (expectedClass.isInstance(actual)) {
			Assert.fail("Shouldn't be an instance, expected = " + expectedClass + ", actual = " + (actual != null ? actual.getClass() : "null"));
		}
	}
}
