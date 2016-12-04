package ru.babobka.nodeslaveserver.server;

import java.io.File;
import java.util.logging.Level;

import ru.babobka.nodeslaveserver.builder.JSONFileServerConfigBuilder;
import ru.babobka.nodeslaveserver.log.SimpleLogger;

public class SlaveServerContext {

	private static String configPath;
	
	private final SlaveServerConfig config;

	private final SimpleLogger logger;

	private static volatile SlaveServerContext instance;


	private SlaveServerContext() {
		try {
			config = JSONFileServerConfigBuilder.build(configPath);
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

	public SlaveServerConfig getConfig() {
		return config;

	}

	public SimpleLogger getLogger() {
		return logger;
	}

	
	public static synchronized String getConfigPath() {
		return configPath;
	}

	public static synchronized void setConfigPath(String configPath) {
		if (instance == null) {
			File f = new File(configPath);
			if (f.exists() && !f.isDirectory()) {
				SlaveServerContext.configPath = configPath;
			} else {
				throw new RuntimeException("'configPath' " + configPath + " doesn't exists");
			}

		} else {
			instance.logger.log(Level.WARNING, "Can not define 'configFolder' value. Context is already created.");
		}
	}

}
