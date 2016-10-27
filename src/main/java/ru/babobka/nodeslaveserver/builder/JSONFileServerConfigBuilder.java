package ru.babobka.nodeslaveserver.builder;

import java.io.File;

import org.json.JSONObject;

import ru.babobka.nodeslaveserver.exception.ServerConfigurationException;
import ru.babobka.nodeslaveserver.server.ServerConfig;
import ru.babobka.nodeslaveserver.util.StreamUtil;

public class JSONFileServerConfigBuilder {

	private static final String CONFIG = "slave_config.json";

	private JSONFileServerConfigBuilder() {

	}

	public static ServerConfig build(boolean production) {

		try {
			if (production) {
				return new ServerConfig(
						new JSONObject(StreamUtil.readFile(StreamUtil.getRunningFolder() + File.separator + CONFIG)));
			} else {
				return new ServerConfig(new JSONObject(StreamUtil
						.readFile(JSONFileServerConfigBuilder.class.getClassLoader().getResourceAsStream(CONFIG))));
			}

		} catch (Exception e) {
			throw new ServerConfigurationException("Can not build server configuration", e);
		}

	}

}
