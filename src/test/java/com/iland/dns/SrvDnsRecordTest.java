package com.iland.dns;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class SrvDnsRecordTest {

	@Test
	void createParsesValue() {
		final SrvDnsRecord record =
				SrvDnsRecord.create("_framework._tcp.marathon.mesos",
						"0 1 41569 marathon.mesos.");
		assertThat(record.getPriority(), equalTo(0));
		assertThat(record.getWeight(), equalTo(1));
		assertThat(record.getPort(), equalTo(41569));
		assertThat(record.getTarget(), equalTo("marathon.mesos."));
	}

}