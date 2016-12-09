package ru.babobka.nodeslaveserver.server;

import ru.babobka.nodeslaveserver.controller.SocketController;
import ru.babobka.nodeslaveserver.controller.SocketControllerImpl;
import ru.babobka.nodeslaveserver.exception.SlaveAuthFailException;
import ru.babobka.nodeslaveserver.log.SimpleLogger;
import ru.babobka.nodeslaveserver.runnable.GlitchRunnable;
import ru.babobka.nodeslaveserver.service.AuthService;
import ru.babobka.nodeslaveserver.service.AuthServiceImpl;
import ru.babobka.nodeslaveserver.task.TaskPool;
import ru.babobka.nodeslaveserver.task.TasksStorage;
import ru.babobka.nodeslaveserver.util.StreamUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;

public class SlaveServer extends Thread {

	public static final String SLAVE_SERVER_TEST_CONFIG = "slave_config.json";

	private final AuthService authService = AuthServiceImpl.getInstance();

	private final Thread glitchThread;

	private volatile Socket socket;

	private final SimpleLogger logger;

	private final TasksStorage tasksStorage;

	public SlaveServer(String serverHost, int port, String login, String password) throws IOException {
		this(serverHost, port, login, password, false);
	}

	public SlaveServer(String serverHost, int port, String login, String password, boolean glithcy) throws IOException {
		TaskPool.getInstance();
		this.logger = SlaveServerContext.getInstance().getLogger();
		this.socket = new Socket(InetAddress.getByName(serverHost), port);
		this.logger.log("Connection was successfully established");
		if (!authService.auth(socket, login, password)) {
			logger.log(Level.SEVERE, "Auth fail");
			throw new SlaveAuthFailException();
		} else {
			logger.log("Auth success");
		}
		this.tasksStorage = new TasksStorage();
		if (glithcy) {
			glitchThread = new Thread(new GlitchRunnable(socket));
		} else {
			glitchThread = null;
		}
	}

	@Override
	public void run() {
		if (glitchThread != null)
			glitchThread.start();
		try (SocketController controller = new SocketControllerImpl(tasksStorage);) {
			while (!Thread.currentThread().isInterrupted()) {
				controller.control(socket);
			}
		} catch (IOException e) {
			if (!socket.isClosed()) {
				logger.log(e);
			} else {
				logger.log("Slave server is done");
			}

		} finally {
			clear();
		}
	}

	@Override
	public void interrupt() {
		super.interrupt();
		clear();
	}

	private void clear() {
		if (glitchThread != null)
			glitchThread.interrupt();
		tasksStorage.stopAllTheTasks();
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		SlaveServerContext.setConfig(StreamUtil.getLocalResource(SlaveServer.class, "slave_config.json"));

	}
}
