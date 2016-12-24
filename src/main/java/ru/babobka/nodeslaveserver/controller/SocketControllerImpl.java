package ru.babobka.nodeslaveserver.controller;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;

import ru.babobka.nodeslaveserver.builder.HeartBeatingResponseBuilder;
import ru.babobka.nodeslaveserver.log.SimpleLogger;
import ru.babobka.nodeslaveserver.runnable.RequestHandlerRunnable;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeslaveserver.task.TaskPool;
import ru.babobka.nodeslaveserver.task.TasksStorage;
import ru.babobka.nodeslaveserver.util.StreamUtil;
import ru.babobka.container.Container;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.SubTask;

public class SocketControllerImpl implements SocketController {

	private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private final TaskPool taskPool = Container.getInstance().get(TaskPool.class);

	private final SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);

	private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

	private final TasksStorage tasksStorage;

	public SocketControllerImpl(TasksStorage tasksStorage) {
		this.tasksStorage = tasksStorage;
	}

	@Override
	public void control(Socket socket) throws IOException {
		socket.setSoTimeout(slaveServerConfig.getRequestTimeoutMillis());
		NodeRequest request = (NodeRequest) StreamUtil.receiveObject(socket);
		if (request.isHeartBeatingRequest()) {
			StreamUtil.sendObject(HeartBeatingResponseBuilder.build(), socket);
		} else if (request.isStoppingRequest()) {
			logger.log(request.toString());
			tasksStorage.stopTask(request.getTaskId(), request.getTimeStamp());
		} else if (request.isRaceStyle() && tasksStorage.exists(request.getTaskId())) {
			logger.log(Level.WARNING, request.getTaskName() + " is race style task. Repeated request was not handled.");
		} else if (!tasksStorage.wasStopped(request.getTaskId(), request.getTimeStamp())) {
			logger.log("Got request " + request);
			SubTask subTask = taskPool.get(request.getTaskName()).getTask();
			tasksStorage.put(request, subTask);
			try {
				threadPool.submit(new RequestHandlerRunnable(socket, tasksStorage, request, subTask));
			} catch (RejectedExecutionException e) {
				logger.log("New request was rejected", e);
			}

		}

	}

	@Override
	public void close() throws IOException {
		threadPool.shutdownNow();
	}

}
