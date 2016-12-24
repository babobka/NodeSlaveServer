package ru.babobka.nodeslaveserver.server;

import ru.babobka.container.Container;
import ru.babobka.nodeslaveserver.controller.SocketController;
import ru.babobka.nodeslaveserver.controller.SocketControllerImpl;
import ru.babobka.nodeslaveserver.exception.SlaveAuthFailException;
import ru.babobka.nodeslaveserver.log.SimpleLogger;
import ru.babobka.nodeslaveserver.runnable.GlitchRunnable;
import ru.babobka.nodeslaveserver.service.AuthService;
import ru.babobka.nodeslaveserver.task.TasksStorage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;

public class SlaveServer extends Thread {

	public static final String SLAVE_SERVER_TEST_CONFIG = "slave_config.json";

	private final AuthService authService = Container.getInstance().get(AuthService.class);

	private final Thread glitchThread;

	private volatile Socket socket;

	private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

	private final TasksStorage tasksStorage;

	public SlaveServer(String serverHost, int port, String login, String password) throws IOException {
		this(serverHost, port, login, password, false);
	}

	public SlaveServer(String serverHost, int port, String login, String password, boolean glithcy) throws IOException {
		socket = new Socket(InetAddress.getByName(serverHost), port);
		logger.log("Connection was successfully established");
		if (!authService.auth(socket, login, password)) {
			logger.log(Level.SEVERE, "Auth fail");
			throw new SlaveAuthFailException();
		} else {
			logger.log("Auth success");
		}
		tasksStorage = new TasksStorage();
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

	}
}
