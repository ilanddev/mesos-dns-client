package com.iland.dns.mesos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iland.dns.DnsClient;
import com.iland.dns.DnsRecord;
import com.iland.dns.Protocol;
import com.iland.dns.RecordType;
import com.iland.dns.SrvDnsRecord;

class MesosDnsClientTest {

	@Mock
	private DnsClient dnsClient;
	private MesosDnsClient client;

	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		this.client = new MesosDnsClient(dnsClient);
	}

	// smoke test
	@Test
	void lookupLeadingCluster() throws NamingException, MesosDnsException {
		when(dnsClient.lookup("leader.mesos", RecordType.A,
				RecordType.AAAA)).thenAnswer(i -> Arrays.asList(
				new DnsRecord(RecordType.A, "leader.mesos", "127.0.0.1")));

		final List<? extends DnsRecord> records =
				client.lookupLeadingCluster("mesos");
		assertThat(records, hasSize(1));
	}

	// smoke test
	@Test
	void lookupLeadingClusterServiceRecords()
			throws NamingException, MesosDnsException {
		when(dnsClient.lookupServiceRecords("_leader._tcp.mesos")).thenAnswer(
				i -> Arrays.asList(SrvDnsRecord.create(RecordType.SRV, "leader.mesos",
						"0 1 5050 leader.mesos.")));

		final List<SrvDnsRecord> serviceRecords =
				client.lookupLeadingClusterServiceRecords("mesos", Protocol.TCP);
		assertThat(serviceRecords, hasSize(1));
	}

	@Test
	@Disabled("run locally (required additional configuration)")
	void printAll() throws MesosDnsException {
		final MesosDnsClient client = new MesosDnsClient();

		System.out.println("leading master:");
		client.lookupLeadingCluster("mesos").forEach(System.out::println);
		System.out.println("leading master TCP:");
		client.lookupLeadingClusterServiceRecords("mesos", Protocol.TCP)
				.forEach(System.out::println);
		System.out.println("leading master UDP:");
		client.lookupLeadingClusterServiceRecords("mesos", Protocol.UDP)
				.forEach(System.out::println);
		System.out.println();

		System.out.println("framework schedulers:");
		client.lookupFrameworkSchedulers("mesos", "marathon")
				.forEach(System.out::println);
		System.out.println("framework schedulers TCP:");
		client.lookupFrameworkSchedulersServiceRecords("mesos", "marathon",
				Protocol.TCP).forEach(System.out::println);
		System.out.println("framework schedulers UDP:");
		client.lookupFrameworkSchedulersServiceRecords("mesos", "marathon",
				Protocol.UDP).forEach(System.out::println);
		System.out.println();

		System.out.println("masters:");
		client.lookupClusters("mesos").forEach(System.out::println);
		System.out.println("masters TCP:");
		client.lookupClustersServiceRecords("mesos", Protocol.TCP)
				.forEach(System.out::println);
		System.out.println("masters UDP:");
		client.lookupClustersServiceRecords("mesos", Protocol.UDP)
				.forEach(System.out::println);
		System.out.println();

		System.out.println("agents:");
		client.lookupAgents("mesos").forEach(System.out::println);
		System.out.println("agents TCP:");
		client.lookupAgentsServiceRecords("mesos", Protocol.TCP)
				.forEach(System.out::println);
		System.out.println("agents UDP:");
		client.lookupAgentsServiceRecords("mesos", Protocol.UDP)
				.forEach(System.out::println);
		System.out.println();

		System.out.println("srv:");
		client.lookupTaskServiceRecords("mesos", "marathon", "foo", Protocol.TCP)
				.forEach(System.out::println);
		client.lookupTaskServiceRecords("mesos", "marathon", "foo", Protocol.UDP)
				.forEach(System.out::println);
		System.out.println();

		System.out.println("task service srv:");
		client.lookupServiceRecordsForTaskService("mesos", "marathon", "foo", "http", Protocol.TCP)
				.forEach(System.out::println);
		client.lookupServiceRecordsForTaskService("mesos", "marathon", "foo", "http", Protocol.UDP)
				.forEach(System.out::println);
		System.out.println();

		System.out.println("getAgentFor:");
		client.lookupAgentsFor("mesos", "marathon", "mesos-dns")
				.forEach(System.out::println);
		System.out.println();

		System.out.println("lookup:");
		client.lookup("mesos", "marathon", "mesos-dns")
				.forEach(System.out::println);
		System.out.println();
	}

}