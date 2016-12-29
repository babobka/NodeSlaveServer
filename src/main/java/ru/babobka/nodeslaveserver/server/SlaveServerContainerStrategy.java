package ru.babobka.nodeslaveserver.server;

import java.io.InputStream;

import ru.babobka.container.Container;
import ru.babobka.container.ContainerStrategy;
import ru.babobka.container.ContainerStrategyException;
import ru.babobka.nodeslaveserver.builder.JSONFileServerConfigBuilder;
import ru.babobka.nodeslaveserver.logger.SimpleLogger;
import ru.babobka.nodeslaveserver.service.AuthServiceImpl;
import ru.babobka.nodeslaveserver.task.TaskPool;

public class SlaveServerContainerStrategy implements ContainerStrategy {

	private final SlaveServerConfig slaveServerConfig;

	public SlaveServerContainerStrategy(InputStream configFileInputStream) {
		this.slaveServerConfig = JSONFileServerConfigBuilder.build(configFileInputStream);
	}

	@Override
	public void contain(Container container) throws ContainerStrategyException {
		try {

			container.put(slaveServerConfig);
			container.put(new SimpleLogger("slave", slaveServerConfig.getLoggerFolder(), "slave"));
			container.put(new TaskPool());
			container.put(new AuthServiceImpl());
			SimpleLogger logger = container.get(SimpleLogger.class);
			logger.log("Container was successfully initialized");
		} catch (Exception e) {
			throw new ContainerStrategyException(e);
		}
	}

}
