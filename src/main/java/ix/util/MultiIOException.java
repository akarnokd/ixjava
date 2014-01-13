/*
 * Copyright 2011-2013 David Karnok
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
package hu.akarnokd.reactive4java.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Exception that collects other IOException instances.
 * <p>Note: unfortunately, we can't use Java 7's addSuppressed for 
 * this purpose as Reactive4Java is aimed at Java 6.</p>
 * @author akarnokd, 2013.01.08.
 * @since 0.97
 */
public class MultiIOException extends IOException {
	/** */
	private static final long serialVersionUID = 7860115538280839361L;
	/** The inner exceptions. */
	protected final List<IOException> innerExceptions = new ArrayList<IOException>();
	/** Default constructor with no message or cause. */
	public MultiIOException() {
		super();
	}
	/**
	 * Constructor with the error message.
	 * @param message the message
	 */
	public MultiIOException(String message) {
		super(message);
	}
	/**
	 * Constructor with the cause.
	 * @param cause the cause
	 */
	public MultiIOException(Throwable cause) {
		super(cause);
	}
	/**
	 * Constructor with the message and cause.
	 * @param message the message
	 * @param cause the cause
	 */
	public MultiIOException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * Adds an exception to the inner exception list.
	 * @param ex the exception to add
	 */
	public void add(IOException ex) {
		innerExceptions.add(ex);
	}
	/**
	 * @return t Returns the list of inner exceptions which can be freely modified.
	 */
	public List<IOException> innerExceptions() {
		return new ArrayList<IOException>(innerExceptions);
	}
	/**
	 * Adds the exception to the given MultiIOException. If multi is null, a new
	 * messageless MultiIOException is created and returned.
	 * @param multi the target MultiIOException. If null, a new instance is created.
	 * @param ex the exception to add
	 * @return the multi value if non null or a new MultiIOException
	 */
	public static MultiIOException createOrAdd(MultiIOException multi, IOException ex) {
		if (multi == null) {
			multi = new MultiIOException();
		}
		multi.add(ex);
		return multi;
	}
}
