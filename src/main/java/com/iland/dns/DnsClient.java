package com.iland.dns;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.NamingException;

import com.iland.dns.mesos.MesosDnsException;

public interface DnsClient {

	/**
	 * Lookup DNS records.
	 *
	 * @param name        e.g. "mesos.apache.org"
	 * @param recordTypes the record types
	 * @return A list of {@link DnsRecord DNS records}
	 * @throws MesosDnsException
	 */
	List<? extends DnsRecord> lookup(final String name,
		RecordType... recordTypes) throws NamingException;

	/**
	 * Lookup service records (SRV records).
	 *
	 * @param name e.g. "mesos.apache.org"
	 * @return a {@link List list} of {@link SrvDnsRecord SRV records} sorted by priority and weight
	 * @throws MesosDnsException
	 */
	default List<SrvDnsRecord> lookupServiceRecords(final String name)
		throws NamingException {
		final Comparator<SrvDnsRecord> byPriorityAndWeight = (r1, r2) -> {
			// the priority of the target host, lower value means more preferred
			final int priority =
				Integer.compare(r1.getPriority(), r2.getPriority());
			// a relative weight for records with the same priority, higher value means higher chance of getting picked
			return priority == 0 ?
				Integer.compare(r2.getWeight(), r1.getWeight()) :
				priority;
		};

		return lookup(name, RecordType.SRV).stream()
			.filter(r -> r instanceof SrvDnsRecord).map(r -> (SrvDnsRecord) r)
			.sorted(byPriorityAndWeight).collect(Collectors.toList());
	}

}
