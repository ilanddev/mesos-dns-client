package com.iland.dns;

public class DefaultDnsRecordFactory implements DnsRecordFactory {

	@Override
	public DnsRecord createDnsRecord(final RecordType type, final String name,
			final String value) {
		return type == RecordType.SRV ?
				SrvDnsRecord.create(type, name, value) :
				new DnsRecord(type, name, value);
	}

}
