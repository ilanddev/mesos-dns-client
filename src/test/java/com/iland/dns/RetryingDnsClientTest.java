package com.iland.dns;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RetryingDnsClientTest {

	@Mock
	private DnsClient dnsClient;
	private DnsClient client;

	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		this.client = new RetryingDnsClient(dnsClient,
				RetryerBuilder.<List<? extends DnsRecord>>newBuilder()
						.retryIfResult(List::isEmpty)
						.withWaitStrategy(WaitStrategies.fibonacciWait())
						.withStopStrategy(StopStrategies.stopAfterAttempt(3)).build());
	}

	@Test
	void lookup() throws NamingException {
		when(dnsClient.lookup("foo", RecordType.A)).thenAnswer(i -> Arrays.asList())
				.thenAnswer(i -> Arrays.asList()).thenAnswer(
						i -> Arrays.asList(new DnsRecord(RecordType.A, "name", "value")));

		final List<? extends DnsRecord> records =
				client.lookup("foo", RecordType.A);
		assertThat(records, hasSize(1));
	}

	@Test
	void lookupReturnsAnEmptyList() throws NamingException {
		when(dnsClient.lookup("foo", RecordType.A)).thenAnswer(i -> Arrays.asList())
				.thenAnswer(i -> Arrays.asList()).thenAnswer(i -> Arrays.asList());

		final List<? extends DnsRecord> records =
				client.lookup("foo", RecordType.A);
		assertThat(records, hasSize(0));
	}

	@Test
	void lookupForwardsNamingException() throws NamingException {
		when(dnsClient.lookup("foo", RecordType.A)).thenAnswer(i -> Arrays.asList())
				.thenThrow(new NamingException("foo"));

		assertThrows(NamingException.class,
				() -> client.lookup("foo", RecordType.A));
	}

}