package com.tamboot.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {

	public static RuntimeException unchecked(Throwable ex) {
		if (ex instanceof RuntimeException) {
			return (RuntimeException) ex;
		} else {
			return new RuntimeException(ex);
		}
	}
	
	public static RuntimeException unchecked(String message ,Throwable ex) {
		return new RuntimeException(message,ex);
	}

	public static String getStackTraceAsString(Throwable ex) {
		StringWriter stringWriter = new StringWriter();
		ex.printStackTrace(new PrintWriter(stringWriter));
		stringWriter.append(ex.getMessage()==null?"":ex.getMessage());
		return stringWriter.toString();
	}

	public static String getErrorMessageWithNestedException(Throwable ex) {
		Throwable nestedException = ex.getCause();
		return new StringBuilder().append(ex.getMessage()).append(" nested exception is ")
				.append(nestedException.getClass().getName()).append(":").append(nestedException.getMessage())
				.toString();
	}

	public static Throwable getRootCause(Throwable ex) {
		Throwable cause;
		while ((cause = ex.getCause()) != null) {
			ex = cause;
		}
		return ex;
	}

	public static boolean isCausedBy(Exception ex, Class<? extends Exception>[] causeExceptionClasses) {
		Throwable cause = ex;
		while (cause != null) {
			for (Class<? extends Exception> causeClass : causeExceptionClasses) {
				if (causeClass.isInstance(cause)) {
					return true;
				}
			}
			cause = cause.getCause();
		}
		return false;
	}
}
