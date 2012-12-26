package org.tuml.javageneration.validation;

import org.tuml.java.metamodel.OJPathName;

public class Url implements Validation {

	private String protocol;
	private String host;
	private int port;
	private String regexp;
	private String flags;

	public String getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getRegexp() {
		return regexp;
	}

	public String getFlags() {
		return flags;
	}

	public Url(String protocol, String host, int port, String regexp, String flags) {
		super();
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.regexp = regexp;
		this.flags = flags;
	}

	@Override
	public String toStringForMethod() {
		return "\"" + getProtocol() + "\", \"" + getHost() + "\", " + String.valueOf(getPort()) + ", \"" + getRegexp() + "\", \"" + getFlags() + "\"";
	}

	@Override
	public String toNewRuntimeTumlValidation() {
		return "new Url(\"" + protocol + "\", \"" + host + "\"," + port + ", \"" + regexp + "\", \"" + flags + "\" )";
	}

	@Override
	public OJPathName getPathName() {
		return new OJPathName("org.tuml.runtime.validation.Url");
	}

	@Override
	public String toJson() {
		return "\\\"url\\\": {\\\"protocol\\\": \\\"" + protocol + "\\\", \\\"host\\\": \\\"" + host + "\\\", \\\"port\\\": "+String.valueOf(port)+", \\\"regexp\\\": \\\"" + regexp + "\\\", \\\"flags\\\": \\\"" + flags+ "\\\"}";
	}
}
