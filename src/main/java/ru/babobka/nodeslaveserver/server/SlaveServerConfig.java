package ru.babobka.nodeslaveserver.server;

import java.io.File;

import org.json.JSONObject;

import ru.babobka.nodeslaveserver.exception.ServerConfigurationException;

public class SlaveServerConfig {

	private final int requestTimeoutMillis;

	private final int authTimeoutMillis;

	private final String loggerFolder;

	private final String tasksFolder;

	public SlaveServerConfig(int requestTimeoutMillis, int authTimeoutMillis, String loggerFolder, String tasksFolder)
			throws ServerConfigurationException {

		if (requestTimeoutMillis <= 0) {
			throw new ServerConfigurationException("'requestTimeoutMillis' value must be positive");
		}
		this.requestTimeoutMillis = requestTimeoutMillis;

		if (authTimeoutMillis <= 0) {
			throw new ServerConfigurationException("'authTimeoutMillis' value must be positive");
		}
		this.authTimeoutMillis = authTimeoutMillis;

		if (loggerFolder == null) {
			throw new ServerConfigurationException("'loggerFolder' is null");
		} else {
			File loggerFolderFile = new File(loggerFolder);
			if (!loggerFolderFile.exists() && !loggerFolderFile.mkdirs()) {
				throw new ServerConfigurationException("Can not create logger folder "+loggerFolderFile);
			}
		}

		this.loggerFolder = loggerFolder;

		if (tasksFolder == null) {
			throw new ServerConfigurationException("'tasksFolder' is null");
		} else if (!new File(tasksFolder).exists()) {
			throw new ServerConfigurationException("'tasksFolder' " + tasksFolder + " doesn't exist");
		}
		this.tasksFolder = tasksFolder;

	}

	public SlaveServerConfig(JSONObject jsonObject) throws ServerConfigurationException {
		this(jsonObject.getInt("requestTimeoutMillis"), jsonObject.getInt("authTimeoutMillis"),
				jsonObject.getString("loggerFolder"), jsonObject.getString("tasksFolder"));

	}

	public int getRequestTimeoutMillis() {
		return requestTimeoutMillis;
	}

	public int getAuthTimeoutMillis() {
		return authTimeoutMillis;
	}

	public String getLoggerFolder() {
		return loggerFolder;
	}

	public String getTasksFolder() {
		return tasksFolder;
	}

	@Override
	public String toString() {
		return "SlaveServerConfig [requestTimeoutMillis=" + requestTimeoutMillis + ", authTimeoutMillis="
				+ authTimeoutMillis + ", loggerFolder=" + loggerFolder + ", tasksFolder=" + tasksFolder + "]";
	}

}
