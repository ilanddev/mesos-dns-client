package com.iland.dns;

public interface DnsRecordFactory {

	DnsRecord createDnsRecord(final RecordType type, final String name,
			final String value);

}
