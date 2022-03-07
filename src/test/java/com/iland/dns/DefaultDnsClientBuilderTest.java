package com.iland.dns;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;


class DefaultDnsClientBuilderTest {

	@Test
	void testDomainMustNotBeNull() {
		assertThrows(NullPointerException.class,
				() -> DefaultDnsClient.builder().withDomain(null),
				"domain must not be null");
	}

	@Test
	void testAtLeastOneHost() {
		assertThrows(IllegalArgumentException.class,
				() -> DefaultDnsClient.builder().withDomain("mesos"),
				"at least one host must be provided");
	}

	@Test
	void testRecordFactoryMustNotBeNull() {
		assertThrows(NullPointerException.class,
				() -> DefaultDnsClient.builder().withDnsRecordFactory(null),
				"recordFactory must not be null");
	}

	@Test
	void testBuild() {
		DefaultDnsClient.builder().authoritative().withDefaultDomain("")
				.withDnsRecordFactory(new DefaultDnsRecordFactory()).build();
	}

}