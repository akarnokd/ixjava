package ix.internal.operators;

import rx.functions.Action1;

public final class PrintAction1<T> implements Action1<T> {
	private final String separator;
	private final int maxLineLength;
	/** Indicator for the first element. */
	boolean first = true;
	/** The current line length. */
	int len;

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