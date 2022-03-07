package com.iland.dns;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.naming.NamingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CachingDnsClientTest {

	private static final String NAME = "foo";

	@Mock
	private DnsClient dnsClient;
	private CachingDnsClient client;

	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		this.client = new CachingDnsClient(dnsClient);
	}

	@Test
	void lookupIsCached() throws NamingException {
		when(dnsClient.lookup(NAME, RecordType.A)).thenAnswer(
						i -> Arrays.asList(new DnsRecord(RecordType.A, "name", "value")))
				.thenThrow(new RuntimeException());

		assertThat(
				client.lookup(NAME, RecordType.A).stream().findFirst().isPresent(),
				is(true));

		assertThat(
				client.lookup(NAME, RecordType.A).stream().findFirst().isPresent(),
				is(true));
	}

	@Test
	void lookupEmptyListIsNotCached() throws NamingException {
		when(dnsClient.lookup(NAME, RecordType.A)).thenAnswer(i -> Arrays.asList())
				.thenAnswer(
						i -> Arrays.asList(new DnsRecord(RecordType.A, "name", "value")));

		assertThat(
				client.lookup(NAME, RecordType.A).stream().findFirst().isPresent(),
				is(false));

		assertThat(
				client.lookup(NAME, RecordType.A).stream().findFirst().isPresent(),
				is(true));
	}

	@Test
	void lookupForwardsNamingException() throws NamingException {
		when(dnsClient.lookup(NAME, RecordType.A)).thenThrow(
				new NamingException(NAME));

		assertThrows(NamingException.class,
				() -> client.lookup(NAME, RecordType.A));
	}

}