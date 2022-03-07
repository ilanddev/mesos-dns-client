package com.iland.dns.mesos;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.naming.NamingException;

import com.iland.dns.DefaultDnsClient;
import com.iland.dns.DnsClient;
import com.iland.dns.DnsRecord;
import com.iland.dns.Protocol;
import com.iland.dns.RecordType;
import com.iland.dns.SrvDnsRecord;

public class MesosDnsClient {

	private final DnsClient dnsClient;

	/**
	 * Default constructor.
	 */
	public MesosDnsClient() {
		this(new DefaultDnsClient());
	}

	/**
	 * @param dnsClient A {@link DnsClient DNS client}
	 */
	public MesosDnsClient(final DnsClient dnsClient) {
		this.dnsClient =
				Objects.requireNonNull(dnsClient, "dnsClient must not be null");
	}

	/**
	 * Lookup the leading cluster records for a domain.
	 *
	 * @param domain e.g. "mesos"
	 * @return the A or AAAA record for the leading cluster
	 * @throws MesosDnsException if the lookup fails
	 */
	public List<? extends DnsRecord> lookupLeadingCluster(final String domain)
			throws MesosDnsException {
		Objects.requireNonNull(domain, "domain must not be null");

		final String name = String.format("leader.%s", domain);

		return lookup(name, RecordType.A, RecordType.AAAA);
	}

	/**
	 * Lookup the leading cluster Service records (SRV records) for a domain.
	 *
	 * @param domain   e.g. "mesos"
	 * @param protocol the protocol
	 * @return a {@link List list} of {@link SrvDnsRecord SRV records} sorted by priority and weight
	 * @throws MesosDnsException if the lookup fails
	 */
	public List<SrvDnsRecord> lookupLeadingClusterServiceRecords(
			final String domain, final Protocol protocol) throws MesosDnsException {
		Objects.requireNonNull(domain, "domain must not be null");
		Objects.requireNonNull(protocol, "protocol must not be null");

		final String name =
				String.format("_leader._%s.%s", protocol.name().toLowerCase(), domain);

		return lookupServiceRecords(name);
	}

	/**
	 * Lookup the records for a framework scheduler.
	 *
	 * @param domain    e.g. "mesos"
	 * @param framework e.g. "marathon"
	 * @return the A or AAAA records for all framework schedulers
	 * @throws MesosDnsException if the lookup fails
	 */
	public List<? extends DnsRecord> lookupFrameworkSchedulers(
			final String domain, final String framework) throws MesosDnsException {
		Objects.requireNonNull(domain, "domain must not be null");
		Objects.requireNonNull(framework, "framework must not be null");

		final String name = String.format("%s.%s", framework, domain);

		return lookup(name, RecordType.A, RecordType.AAAA);
	}

	/**
	 * Lookup the Service records (SRV records) for a framework scheduler.
	 *
	 * @param domain    e.g. "mesos"
	 * @param framework e.g. "marathon"
	 * @param protocol  the protocol
	 * @return a {@link List list} of {@link SrvDnsRecord SRV records} sorted by priority and weight
	 * @throws MesosDnsException if the lookup fails
	 */
	public List<SrvDnsRecord> lookupFrameworkSchedulersServiceRecords(
			final String domain, final String framework, final Protocol protocol)
			throws MesosDnsException {
		Objects.requireNonNull(domain, "domain must not be null");
		Objects.requireNonNull(framework, "framework must not be null");
		Objects.requireNonNull(protocol, "protocol must not be null");

		final String name =
				String.format("_framework._%s.%s.%s", protocol.name().toLowerCase(),
						framework, domain);

		return lookupServiceRecords(name);
	}

	/**
	 * Lookup cluster records for a domain.
	 *
	 * @param domain e.g. "mesos"
	 * @return the A or AAAA records for every known Mesos cluster
	 * @throws MesosDnsException if the lookup fails
	 */
	public List<? extends DnsRecord> lookupClusters(final String domain)
			throws MesosDnsException {
		Objects.requireNonNull(domain, "domain must not be null");

		final String name = String.format("master.%s", domain);

		return lookup(name, RecordType.A, RecordType.AAAA);
	}

	/**
	 * Lookup cluster Service records (SRV records) for a domain.
	 *
	 * @param domain   e.g. "mesos"
	 * @param protocol the protocol
	 * @return a {@link List list} of {@link SrvDnsRecord SRV records} sorted by priority and weight
	 * @throws MesosDnsException if the lookup fails
	 */
	public List<SrvDnsRecord> lookupClustersServiceRecords(final String domain,
			final Protocol protocol) throws MesosDnsException {
		Objects.requireNonNull(domain, "domain must not be null");
		Objects.requireNonNull(protocol, "protocol must not be null");

		final String name =
				String.format("_master._%s.%s", protocol.name().toLowerCase(), domain);

		return lookupServiceRecords(name);
	}

	/**
	 * Lookup a domain's agents records.
	 *
	 * @param domain e.g. "mesos"
	 * @return the A or AAAA records for every known Mesos agent
	 * @throws MesosDnsException if the lookup fails
	 */
	public List<? extends DnsRecord> lookupAgents(final String domain)
			throws MesosDnsException {
		Objects.requireNonNull(domain, "domain must not be null");

		final String name = String.format("slave.%s", domain);

		return lookup(name, RecordType.A, RecordType.AAAA);
	}

	/**
	 * Lookup a domain's agent's Service records (SRV records).
	 *
	 * @param domain   e.g. "mesos"
	 * @param protocol the protocol
	 * @return a {@link List list} of {@link SrvDnsRecord SRV records} sorted by priority and weight
	 * @throws MesosDnsException if the lookup fails
	 */
	public List<SrvDnsRecord> lookupAgentsServiceRecords(final String domain,
			final Protocol protocol) throws MesosDnsException {
		Objects.requireNonNull(domain, "domain must not be null");
		Objects.requireNonNull(protocol, "protocol must not be null");

		final String name =
				String.format("_slave._%s.%s", protocol.name().toLowerCase(), domain);

		return lookupServiceRecords(name);
	}

	/**
	 * Lookup a task's agents.
	 *
	 * @param domain    e.g. "mesos"
	 * @param framework e.g. "marathon"
	 * @param task      e.g. "mesos-dns"
	 * @return the IP address(es) of the agent node(s) upon which the task is running
	 * @throws MesosDnsException if the lookup fails
	 */
	public List<? extends DnsRecord> lookupAgentsFor(final String domain,
			final String framework, final String task) throws MesosDnsException {
		Objects.requireNonNull(domain, "domain must not be null");
		Objects.requireNonNull(framework, "framework must not be null");
		Objects.requireNonNull(task, "task must not be null");

		final String name =
				String.format("%s.%s.slave.%s", task, framework, domain);

		return lookup(name);
	}

	/**
	 * Lookup a task's DNS Service records (SRV records).
	 *
	 * @param domain    e.g. "mesos"
	 * @param framework e.g. "marathon"
	 * @param task      e.g. "mesos-dns"
	 * @param protocol  the protocol
	 * @return a {@link List list} of {@link SrvDnsRecord SRV records} sorted by priority and weight
	 * @throws MesosDnsException if the lookup fails
	 */
	public List<SrvDnsRecord> lookupTaskServiceRecords(final String domain,
			final String framework, final String task, final Protocol protocol)
			throws MesosDnsException {
		Objects.requireNonNull(domain, "domain must not be null");
		Objects.requireNonNull(framework, "framework must not be null");
		Objects.requireNonNull(task, "task must not be null");
		Objects.requireNonNull(protocol, "protocol must not be null");

		final String name =
				String.format("_%s._%s.%s.%s", task, protocol.name().toLowerCase(),
						framework, domain);

		return lookupServiceRecords(name);
	}

	/**
	 * Lookup and return the first of a task's DNS records.
	 *
	 * @param domain     e.g. "mesos"
	 * @param framework  e.g. "marathon"
	 * @param task       e.g. "mesos-dns"
	 * @param recordType A record type to lookup
	 * @return A list of {@link DnsRecord DNS records}
	 * @throws MesosDnsException if the lookup fails
	 */
	public Optional<? extends DnsRecord> lookupFirst(final String domain,
			final String framework, final String task, RecordType recordType)
			throws MesosDnsException {
		Objects.requireNonNull(domain, "domain must not be null");
		Objects.requireNonNull(framework, "framework must not be null");
		Objects.requireNonNull(task, "task must not be null");
		Objects.requireNonNull(recordType, "recordType must not be null");

		final List<? extends DnsRecord> records =
				lookup(domain, framework, task, recordType);

		return records.isEmpty() ? Optional.empty() : Optional.of(records.get(0));
	}

	/**
	 * Lookup a task's DNS records.
	 *
	 * @param domain      e.g. "mesos"
	 * @param framework   e.g. "marathon"
	 * @param task        e.g. "mesos-dns"
	 * @param recordTypes Zero or more record types to lookup (an empty array indicates that all attributes should be retrieved)
	 * @return A list of {@link DnsRecord DNS records}
	 * @throws MesosDnsException if the lookup fails
	 */
	public List<? extends DnsRecord> lookup(final String domain,
			final String framework, final String task, RecordType... recordTypes)
			throws MesosDnsException {
		Objects.requireNonNull(domain, "domain must not be null");
		Objects.requireNonNull(framework, "framework must not be null");
		Objects.requireNonNull(task, "task must not be null");

		final String name = String.format("%s.%s.%s", task, framework, domain);

		return lookup(name, recordTypes);
	}

	/**
	 * Lookup DNS records.
	 *
	 * @param name        e.g. "mesos.apache.org"
	 * @param recordTypes the record types
	 * @return A list of {@link DnsRecord DNS records}
	 * @throws MesosDnsException
	 */
	public List<? extends DnsRecord> lookup(final String name,
			RecordType... recordTypes) throws MesosDnsException {
		try {
			return dnsClient.lookup(name, recordTypes);
		} catch (NamingException e) {
			throw new MesosDnsException(e);
		}
	}

	/**
	 * Lookup service records (SRV records).
	 *
	 * @param name e.g. "mesos.apache.org"
	 * @return a {@link List list} of {@link SrvDnsRecord SRV records} sorted by priority and weight
	 * @throws MesosDnsException
	 */
	public List<SrvDnsRecord> lookupServiceRecords(final String name)
			throws MesosDnsException {
		try {
			return dnsClient.lookupServiceRecords(name);
		} catch (NamingException e) {
			throw new MesosDnsException(e);
		}
	}

}
