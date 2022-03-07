package com.iland.dns;

import static com.iland.dns.Exceptions.lookupErrorMessage;
import static com.iland.dns.Exceptions.throwNamingException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingDnsClient implements DnsClient {

	private static final Logger logger =
			LoggerFactory.getLogger(CachingDnsClient.class);

	private final LoadingCache<DnsQuery, List<? extends DnsRecord>> cache;

	/**
	 * A {@link CachingDnsClient} with a TTL of 1 minute.
	 *
	 * @param delegatee the child {@link DnsClient}
	 */
	public CachingDnsClient(final DnsClient delegatee) {
		this(delegatee, 1, TimeUnit.MINUTES);
	}

	/**
	 * @param delegatee the child {@link DnsClient}
	 * @param duration  the duration
	 * @param unit      the {@link TimeUnit unit}
	 */
	public CachingDnsClient(final DnsClient delegatee, final long duration,
			final TimeUnit unit) {
		Objects.requireNonNull(delegatee, "delegatee must not be null");
		this.cache = CacheBuilder.newBuilder().expireAfterWrite(duration, unit)
				.removalListener(notification -> logger.debug("{} was {} (cause: {})",
						notification.getKey(),
						notification.wasEvicted() ? "evicted" : "removed",
						notification.getCause()))
				.build(new CacheLoader<DnsQuery, List<? extends DnsRecord>>() {
					@Override
					public List<? extends DnsRecord> load(final DnsQuery dnsQuery)
							throws Exception {
						return delegatee.lookup(dnsQuery.name, dnsQuery.recordTypes);
					}
				});
	}

	@Override
	public List<? extends DnsRecord> lookup(final String name,
			final RecordType... recordTypes) throws NamingException {
		final DnsQuery dnsQuery = new DnsQuery(name, recordTypes);
		try {
			synchronized (this) {
				final List<? extends DnsRecord> dnsRecords = cache.get(dnsQuery);
				if (dnsRecords.isEmpty()) {
					cache.invalidate(dnsQuery);
				}

				return dnsRecords;
			}
		} catch (final ExecutionException e) {
			throwNamingException(e);
			logger.error(lookupErrorMessage(name, recordTypes), e);
		}

		return Arrays.asList();
	}

	private static final class DnsQuery {

		private final String name;
		private final RecordType[] recordTypes;

		private DnsQuery(final String name, final RecordType[] recordTypes) {
			this.name = Objects.requireNonNull(name, "name must not be null");
			this.recordTypes =
					Objects.requireNonNull(recordTypes, "recordTypes must not be null");
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			final DnsQuery dnsQuery = (DnsQuery) o;
			return name.equals(dnsQuery.name) && Arrays.equals(recordTypes,
					dnsQuery.recordTypes);
		}

		@Override
		public int hashCode() {
			int result = Objects.hash(name);
			result = 31 * result + Arrays.hashCode(recordTypes);
			return result;
		}

	}

}
