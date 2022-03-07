package com.iland.dns;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.naming.NamingException;

public class Exceptions {

	private Exceptions() {
	}

	public static final void throwNamingException(final ExecutionException e)
			throws NamingException {
		Throwable rootCause = e;
		while ((rootCause = rootCause.getCause()) != null) {
			if (rootCause instanceof NamingException) {
				throw (NamingException) rootCause;
			}
		}
	}

	public static final String lookupErrorMessage(final String name,
			final RecordType... recordTypes) {
		final String recordTypeCsv =
				Arrays.stream(recordTypes).map(RecordType::toString)
						.collect(Collectors.joining(", "));

		return String.format("lookup of '%s' (%s) failed", name, recordTypeCsv);
	}

}
