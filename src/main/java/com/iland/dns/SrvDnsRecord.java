package com.iland.dns;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SrvDnsRecord extends DnsRecord {

	private final int priority, weight, port;
	private final String target;

	/**
	 * @param type  i.e. {@link RecordType#SRV SRV}
	 * @param name  a name
	 * @param value a value
	 * @return a {@link SrvDnsRecord SRV record}
	 * @throws IllegalArgumentException if the value can not be parsed
	 */
	public static SrvDnsRecord create(final RecordType type, final String name,
			final String value) {
		if (!RecordType.SRV.equals(type)) {
			throw new IllegalArgumentException("type must be SRV");
		}

		String regex = "(\\d+)\\s(\\d+)\\s(\\d+)\\s(.*)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(value);
		if (matcher.find()) {
			final int priority = Integer.parseInt(matcher.group(1));
			final int weight = Integer.parseInt(matcher.group(2));
			final int port = Integer.parseInt(matcher.group(3));
			final String target = matcher.group(4).trim();

			return new SrvDnsRecord(type, name, value, priority, weight, port,
					target);
		} else {
			final String message =
					String.format("SRV record \"%s\" could not be parsed", value);
			throw new IllegalArgumentException(message);
		}
	}

	protected SrvDnsRecord(final RecordType type, final String name,
			final String value, final int priority, final int weight, final int port,
			final String target) {
		super(type, name, value);
		this.priority = priority;
		this.weight = weight;
		this.port = port;
		this.target = Objects.requireNonNull(target, "target must not be null");
	}

	/**
	 * Returns the priority of the target host, lower value means more preferred.
	 *
	 * @return the priority of the target host
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Returns a relative weight for records with the same priority, higher value means higher chance of getting picked.
	 *
	 * @return relative weight for records with the same priority
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * Returns the TCP or UDP port on which the service is to be found.
	 *
	 * @return the TCP or UDP port on which the service is to be found
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the canonical hostname of the machine providing the service, ending in a dot.
	 *
	 * @return the canonical hostname of the machine providing the service, ending in a dot
	 */
	public String getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return SrvDnsRecord.class.getSimpleName() + "{" + "type=" + getType()
				+ ", name='" + getName() + '\'' + ", priority=" + priority + ", weight="
				+ weight + ", port=" + port + ", target='" + target + '\'' + '}';
	}

}
