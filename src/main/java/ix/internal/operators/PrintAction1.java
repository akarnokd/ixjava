/*
 * Copyright 2011-2014 David Karnok
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
package ix.internal.operators;

import rx.functions.Action1;
/**
 * Helper action that prints elements to the console.
 *
 * @param <T> the source value type
 */
public final class PrintAction1<T> implements Action1<T> {
	/** The separator between elements. */
	private final String separator;
	/** The maximum length of a line. */
	private final int maxLineLength;
	/** Indicator for the first element. */
	boolean first = true;
	/** The current line length. */
	int len;

	/**
	 * Constructor, initializes the fields.
	 * @param separator the separator between elements
	 * @param maxLineLength the maximum lenght of a line
	 */
	public PrintAction1(String separator, int maxLineLength) {
		this.separator = separator;
		this.maxLineLength = maxLineLength;
	}

	@Override
	public void call(T value) {
	    String s = String.valueOf(value);
	    if (first) {
	        first = false;
	        System.out.print(s);
	        len = s.length();
	    } else {
	        if (len + separator.length() + s.length() > maxLineLength) {
	            if (len == 0) {
	                System.out.print(separator);
	                System.out.print(s);
	                len = s.length() + separator.length();
	            } else {
	                System.out.println(separator);
	                System.out.print(s);
	                len = s.length();
	            }
	        } else {
	            System.out.print(separator);
	            System.out.print(s);
	            len += s.length() + separator.length();
	        }
	    }
	}
}