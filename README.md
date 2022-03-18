# Java Client for Mesos-DNS
A Java library for querying a Mesos-DNS server.

## Usage
```java
final MesosDnsClient client = new MesosDnsClient(new CachingDnsClient(new RetryingDnsClient(new DefaultDnsClient())));
final String domain = "mesos", framework = "marathon", task = "foo";
client.lookupTaskServiceRecords(domain, framework, task, Protocol.UDP).stream().forEach(System.out::println);
//SrvDnsRecord{type=SRV, name='_foo._udp.marathon.mesos', priority=0, weight=1, port=54, target='foo-pcsc9-s0.marathon.mesos.'}
```

## Maven Installation (pom.xml)
```xml
<repositories>
    <repository>
        <id>iland-maven-foss</id>
        <url>
            https://us-central1-maven.pkg.dev/iland-software-engineering/iland-maven-foss
        </url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.iland</groupId>
            <artifactId>mesos-dns-client</artifactId>
            <version>${mesos-dns-client.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```