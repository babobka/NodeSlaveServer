package ru.babobka.nodeslaveserver.builder;

import org.json.JSONObject;

import ru.babobka.nodeslaveserver.exception.ServerConfigurationException;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeslaveserver.util.StreamUtil;

public interface JSONFileServerConfigBuilder {

	public static SlaveServerConfig build(String configFolder) {

		try {

			return new SlaveServerConfig(new JSONObject(StreamUtil.readFile(configFolder)));

		} catch (Exception e) {
			throw new ServerConfigurationException("Can not build server configuration", e);
		}

	}

}
