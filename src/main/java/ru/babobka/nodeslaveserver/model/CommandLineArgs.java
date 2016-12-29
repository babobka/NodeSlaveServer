package ru.babobka.nodeslaveserver.model;

import ru.babobka.nodeslaveserver.util.TextUtil;

public class CommandLineArgs {

	private static final String IPV4_REGEX = "\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";

	private final String host;

	private final int port;

	private final String login;

	private final String password;

	public CommandLineArgs(String... args) {
		if (args.length < 4) {
			throw new IllegalArgumentException("Command usage is <host> <port> <login> <password>");
		}
		host = args[0];
		if (!host.equals("localhost") && !host.matches(IPV4_REGEX)) {
			throw new IllegalArgumentException("Invalid host value " + host);
		}

		port = TextUtil.tryParseInt(args[1], -1);
		if (port < 0 || port > 65535) {
			throw new IllegalArgumentException("Invalid port value " + args[1]);
		}
		login = args[2];
		password = args[3];
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

}
