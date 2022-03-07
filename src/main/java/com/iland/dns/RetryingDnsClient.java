package com.iland.dns;

import static com.iland.dns.Exceptions.lookupErrorMessage;
import static com.iland.dns.Exceptions.throwNamingException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryingDnsClient implements DnsClient {

	private static final Logger logger =
			LoggerFactory.getLogger(RetryingDnsClient.class);

	private final DnsClient delegatee;
	private final Retryer<List<? extends DnsRecord>> retryer;

	/**
	 * @param delegatee the child {@link DnsClient}
	 */
	public RetryingDnsClient(final DnsClient delegatee) {
		this(delegatee, createDefaultRetryer());
	}

	/**
	 * @param delegatee the child {@link DnsClient}
	 * @param retryer   the {@link Retryer retryer}
	 */
	public RetryingDnsClient(final DnsClient delegatee,
			final Retryer<List<? extends DnsRecord>> retryer) {
		this.delegatee =
				Objects.requireNonNull(delegatee, "delegatee must not be null");
		this.retryer = Objects.requireNonNull(retryer, "retryer must not be null");
	}

	@Override
	public List<? extends DnsRecord> lookup(final String name,
			final RecordType... recordTypes) throws NamingException {
		try {
			return retryer.call(() -> delegatee.lookup(name, recordTypes));
		} catch (final RetryException e) {
			logger.warn(lookupErrorMessage(name, recordTypes));
		} catch (final ExecutionException e) {
			throwNamingException(e);
			logger.error(lookupErrorMessage(name, recordTypes), e);
		}

		return Arrays.asList();
	}

	static Retryer<List<? extends DnsRecord>> createDefaultRetryer() {
		return RetryerBuilder.<List<? extends DnsRecord>>newBuilder()
				.retryIfResult(List::isEmpty)
				.withWaitStrategy(WaitStrategies.fibonacciWait())
				.withStopStrategy(StopStrategies.stopAfterDelay(1, TimeUnit.MINUTES))
				.build();
	}

}
