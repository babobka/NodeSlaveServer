package ru.babobka.nodeslaveserver.server;

import ru.babobka.container.Container;
import ru.babobka.container.ContainerStrategyException;
import ru.babobka.nodeslaveserver.controller.SocketController;
import ru.babobka.nodeslaveserver.controller.SocketControllerImpl;
import ru.babobka.nodeslaveserver.exception.SlaveAuthFailException;
import ru.babobka.nodeslaveserver.logger.SimpleLogger;
import ru.babobka.nodeslaveserver.model.CommandLineArgs;
import ru.babobka.nodeslaveserver.runnable.GlitchRunnable;
import ru.babobka.nodeslaveserver.service.AuthService;
import ru.babobka.nodeslaveserver.task.TasksStorage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;

public class SlaveServer extends Thread {

	public static final String SLAVE_SERVER_TEST_CONFIG = "slave_config.json";

	private final AuthService authService = Container.getInstance().get(AuthService.class);

	private final Thread glitchThread;

	private final Socket socket;

	private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

	private final TasksStorage tasksStorage;

	public SlaveServer(String serverHost, int port, String login, String password) throws IOException {
		this(serverHost, port, login, password, false);
	}

	public SlaveServer(String serverHost, int port, String login, String password, boolean glitchy) throws IOException {
		socket = new Socket(InetAddress.getByName(serverHost), port);
		logger.log("Connection was successfully established");
		if (!authService.auth(socket, login, password)) {
			logger.log(Level.SEVERE, "Auth fail");
			throw new SlaveAuthFailException();
		} else {
			logger.log("Auth success");
		}
		tasksStorage = new TasksStorage();
		if (glitchy) {
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
				logger.log(e);
			}
		}
	}

	public static void main(String[] args)
			throws InterruptedException, ContainerStrategyException, FileNotFoundException {
		new SlaveServerContainerStrategy(new FileInputStream(SLAVE_SERVER_TEST_CONFIG))
				.contain(Container.getInstance());

		/*
		 * new SlaveServerContainerStrategy(StreamUtil.getLocalResource(
		 * SlaveServer.class, SlaveServer.SLAVE_SERVER_TEST_CONFIG))
		 * .contain(Container.getInstance());
		 */

		SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

		CommandLineArgs command = new CommandLineArgs(args);
		// CommandLineArgs command = new
		// CommandLineArgs("localhost","1918","test_user","abc");
		while (!Thread.currentThread().isInterrupted()) {
			try {
				SlaveServer slaveSever = new SlaveServer(command.getHost(), command.getPort(), command.getLogin(),
						command.getPassword());
				slaveSever.start();
				slaveSever.join();

			} catch (SlaveAuthFailException e) {
				logger.log(e);
				return;
			} catch (IOException e) {
				logger.log(e);
				logger.log(Level.WARNING, "Reconnecting");
				Thread.sleep(1000);
			}
		}

	}
}
