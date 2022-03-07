package com.iland.dns;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.junit.jupiter.api.Test;


class DnsClientTest {

	@Test
	void lookupServiceRecordsOrderedByPriorityAndWeight() throws NamingException {
		final DnsClient client = (name, recordTypes) -> Arrays.asList(
				new SrvDnsRecord(RecordType.SRV, "name", "value", 1, 99, 0, "target"),
				new SrvDnsRecord(RecordType.SRV, "name", "value", 0, 0, 0, "target"),
				new SrvDnsRecord(RecordType.SRV, "name", "value", 0, 1, 0, "target"));

		final List<SrvDnsRecord> serviceRecords = client.lookupServiceRecords(null);
		assertThat(serviceRecords, hasSize(3));
		assertThat(serviceRecords.get(0), equalTo(
				new SrvDnsRecord(RecordType.SRV, "name", "value", 0, 1, 0, "target")));
		assertThat(serviceRecords.get(2), equalTo(
				new SrvDnsRecord(RecordType.SRV, "name", "value", 1, 99, 0, "target")));
	}

}