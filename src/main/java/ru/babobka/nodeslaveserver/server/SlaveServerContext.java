package ru.babobka.nodeslaveserver.server;

import java.io.InputStream;
import java.util.logging.Level;

import ru.babobka.nodeslaveserver.builder.JSONFileServerConfigBuilder;
import ru.babobka.nodeslaveserver.log.SimpleLogger;

public class SlaveServerContext {

	private static volatile SlaveServerConfig config;

	private final SimpleLogger logger;

	private static volatile SlaveServerContext instance;

	private SlaveServerContext() {
		try {
			if (config == null) {
				throw new IllegalStateException("Configuration was not specified.");
			}
			logger = new SimpleLogger("slave", config.getLoggerFolder(), "slave");
			logger.log("ServerContext was successfuly created");
			logger.log(config.toString());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static SlaveServerContext getInstance() {
		SlaveServerContext localInstance = instance;
		if (localInstance == null) {
			synchronized (SlaveServerContext.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new SlaveServerContext();

				}
			}
		}
		return localInstance;
	}

	public static SlaveServerConfig getConfig() {
		return config;

	}

	public SimpleLogger getLogger() {
		return logger;
	}

	public static void setConfig(InputStream configFileInputStream) {
		if (instance == null) {
			config = JSONFileServerConfigBuilder.build(configFileInputStream);
		} else {
			instance.logger.log(Level.WARNING, "Can not redefine configuration. Context is already created.");
		}
	}

}
