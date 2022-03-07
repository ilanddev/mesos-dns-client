package com.iland.dns;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SrvDnsRecordTest {

	@Test
	void createThrowsIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {
			SrvDnsRecord.create(null, null, null);
		}, "type must be SRV");
	}

	@Test
	void createParsesValue() {
		final SrvDnsRecord record =
				SrvDnsRecord.create(RecordType.SRV, "_framework._tcp.marathon.mesos",
						"0 1 41569 marathon.mesos.");
		assertThat(record.getPriority(), equalTo(0));
		assertThat(record.getWeight(), equalTo(1));
		assertThat(record.getPort(), equalTo(41569));
		assertThat(record.getTarget(), equalTo("marathon.mesos."));
	}

}