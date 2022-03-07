package com.iland.dns;

import java.util.Objects;

public class DnsRecord {

	private final RecordType type;
	private final String name;
	private final String value;

	public DnsRecord(final RecordType type, final String name,
			final String value) {
		this.type = Objects.requireNonNull(type, "type must not be null");
		this.name = Objects.requireNonNull(name, "name must not be null");
		this.value = Objects.requireNonNull(value, "value must not be null");
	}

	public RecordType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final DnsRecord dnsRecord = (DnsRecord) o;
		return type == dnsRecord.type && name.equals(dnsRecord.name)
				&& value.equals(dnsRecord.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, name, value);
	}

	@Override
	public String toString() {
		return DnsRecord.class.getSimpleName() + "{" + "type=" + type + ", name='"
				+ name + '\'' + ", value='" + value + '\'' + '}';
	}

}
