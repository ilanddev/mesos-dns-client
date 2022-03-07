package com.iland.dns.mesos;

public class MesosDnsException extends Exception {

	public MesosDnsException(final String message) {
		super(message);
	}

	public MesosDnsException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MesosDnsException(final Throwable cause) {
		super(cause);
	}

}
