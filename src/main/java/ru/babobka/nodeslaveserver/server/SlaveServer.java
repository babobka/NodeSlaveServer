package ru.babobka.nodeslaveserver.server;

import ru.babobka.nodeslaveserver.controller.SocketController;
import ru.babobka.nodeslaveserver.controller.SocketControllerImpl;
import ru.babobka.nodeslaveserver.exception.SlaveAuthFailException;
import ru.babobka.nodeslaveserver.log.SimpleLogger;
import ru.babobka.nodeslaveserver.model.TaskMap;
import ru.babobka.nodeslaveserver.service.AuthService;
import ru.babobka.nodeslaveserver.service.AuthServiceImpl;
import ru.babobka.nodeslaveserver.task.TaskPool;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;

public class SlaveServer extends Thread {

	private static final AuthService authService = AuthServiceImpl.getInstance();

	private volatile Socket socket;

	private final SimpleLogger logger;

	public SlaveServer(String serverHost, int port, String login, String password) throws IOException {
		
		TaskPool.getInstance();
		this.logger = ServerContext.getInstance().getLogger();
		this.socket = new Socket(InetAddress.getByName(serverHost), port);
		this.logger.log("Connection was successfully established");
		if (!authService.auth(socket, login, password)) {
			logger.log(Level.SEVERE, "Auth fail");
			throw new SlaveAuthFailException();
		} else {
			logger.log("Auth success");
		}
	}

	@Override
	public void run() {
		try (SocketController controller = new SocketControllerImpl();) {
			while (!Thread.currentThread().isInterrupted()) {
				controller.control(socket);
			}
		} catch (IOException e) {
			if (!socket.isClosed()) {
				logger.log(e);
			} else {
				logger.log(Level.INFO, "Slave server is done");
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
		TaskMap.stopAllTheTasks();
		if (socket != null && !socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
