package com.iland.dns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.InitialDirContext;

import com.iland.dns.mesos.MesosDnsException;

public class DefaultDnsClient implements DnsClient {

	private final Hashtable<String, Object> environment;
	private InitialDirContext context;
	private final DnsRecordFactory recordFactory;

	/**
	 * Default constructor
	 */
	public DefaultDnsClient() {
		this(new Hashtable<>(), new DefaultDnsRecordFactory());
	}

	/**
	 * @param environment the environment
	 */
	public DefaultDnsClient(final Map<String, Object> environment,
			final DnsRecordFactory recordFactory) {
		this.environment = new Hashtable<>(environment);
		this.recordFactory =
				Objects.requireNonNull(recordFactory, "recordFactory must not be null");
	}

	/**
	 * Lookup Mesos-DNS.
	 *
	 * @param name        e.g. "mesos.apache.org"
	 * @param recordTypes the record types
	 * @return A list of {@link DnsRecord DNS records}
	 * @throws MesosDnsException
	 */
	public List<? extends DnsRecord> lookup(final String name,
			RecordType... recordTypes) throws NamingException {
		initialize();

		final List<DnsRecord> records = new ArrayList<>();

		final String dnsName = String.format("dns:%s", name);
		final String[] attributeIds = recordTypes.length == 0 ?
				null :
				Arrays.stream(recordTypes).map(RecordType::toString)
						.collect(Collectors.toList())
						.toArray(new String[recordTypes.length]);
		final Attributes attributes = context.getAttributes(dnsName, attributeIds);
		for (Enumeration<? extends Attribute> e =
		     attributes.getAll(); e.hasMoreElements(); ) {
			final BasicAttribute attribute = (BasicAttribute) e.nextElement();
			final RecordType recordType = RecordType.valueOf(attribute.getID());
			final String value = attribute.get().toString();
			final DnsRecord record =
					recordFactory.createDnsRecord(recordType, name, value);

			records.add(record);
		}

		return records;
	}

	private void initialize() throws NamingException {
		if (this.context == null) {
			this.context = new InitialDirContext(environment);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private static final String DEFAULT_DOMAIN = "mesos";

		private final Map<String, Object> environment;

		private DnsRecordFactory recordFactory;

		private Builder() {
			environment = new HashMap<>();
		}

		/**
		 * Specify that all responses must be authoritative.
		 *
		 * @return {@link Builder this}
		 */
		public Builder authoritative() {
			return withEnvironment(Context.AUTHORITATIVE, "true");
		}

		/**
		 * Uses <code>"mesos"</code> as the domain.
		 *
		 * @param hosts An array of DNS servers taking the form "host[:port]"
		 * @return {@link Builder this}
		 */
		public Builder withDefaultDomain(final String... hosts) {
			return withDomain(DEFAULT_DOMAIN, hosts);
		}

		/**
		 * @param domain the domain name for the Mesos cluster
		 * @param hosts  An array of DNS servers taking the form "host[:port]"
		 * @return {@link Builder this}
		 */
		public Builder withDomain(final String domain, final String... hosts) {
			Objects.requireNonNull(domain, "domain must not be null");
			if (hosts.length == 0) {
				throw new IllegalArgumentException(
						"at least one host must be provided");
			}

			final String urls = Arrays.stream(hosts)
					.map(host -> String.format("dns://%s/%s", host, domain))
					.collect(Collectors.joining(" "));

			return withEnvironment(Context.PROVIDER_URL, urls);
		}

		/**
		 * Specify an attribute not exposed by this configuration.
		 *
		 * @param key   the key
		 * @param value the value
		 * @return {@link Builder this}
		 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/jndi/jndi-dns.html#PROP">Environment Properties</a>
		 */
		public Builder withEnvironment(final String key, final Object value) {
			environment.put(key, value);

			return this;
		}

		/**
		 * Specify a {@link DnsRecordFactory}.
		 *
		 * @param factory a {@link DnsRecordFactory}
		 * @return {@link Builder this}
		 */
		public Builder withDnsRecordFactory(final DnsRecordFactory factory) {
			this.recordFactory =
					Objects.requireNonNull(factory, "factory must not be null");

			return this;
		}

		public DnsClient build() {
			return new DefaultDnsClient(environment, recordFactory == null ?
					new DefaultDnsRecordFactory() :
					recordFactory);
		}

	}

}
