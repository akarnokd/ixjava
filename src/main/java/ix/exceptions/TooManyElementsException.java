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
package ix.exceptions;

/**
 * Exception for cases when too many elements are contained
 * within a collection or observable. Its the dual
 * of NoSuchElementException.
 */
public class TooManyElementsException extends RuntimeException {
	/** */
	private static final long serialVersionUID = 3390531861721818769L;
	/**
	 * A message and cause-less constructor.
	 */
	public TooManyElementsException() {
		super();
	}

	/**
	 * Construct the exception by using a message and a cause.
	 * @param message the message
	 * @param cause the cause
	 */
	public TooManyElementsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Construct the exception by using a message only.
	 * @param message the message
	 */
	public TooManyElementsException(String message) {
		super(message);
	}

	/**
	 * Construct the exception by using a cause only.
	 * @param cause the cause
	 */
	public TooManyElementsException(Throwable cause) {
		super(cause);
	}

}
