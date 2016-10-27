package ru.babobka.nodeslaveserver.server;

import ru.babobka.nodeslaveserver.builder.JSONFileServerConfigBuilder;
import ru.babobka.nodeslaveserver.log.SimpleLogger;

public class ServerContext {

	private final ServerConfig config;

	private final SimpleLogger logger;

	private static volatile ServerContext instance;

	private static volatile boolean production;

	private ServerContext() {
		try {
			config = JSONFileServerConfigBuilder.build(production);
			logger = new SimpleLogger("NodeServer", config.getLoggerFolder(), "server");
			logger.log("ServerContext was successfuly created");
			logger.log(config.toString());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static ServerContext getInstance() {
		ServerContext localInstance = instance;
		if (localInstance == null) {
			synchronized (ServerContext.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new ServerContext();

				}
			}
		}
		return localInstance;
	}

	public ServerConfig getConfig() {
		return config;

	}

	public SimpleLogger getLogger() {
		return logger;
	}

	static void setProduction(boolean production) {
		if (instance == null) {
			synchronized (ServerContext.class) {
				if (instance == null) {
					ServerContext.production = production;
				}
			}
		}
	}

}
