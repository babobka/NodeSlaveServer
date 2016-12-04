package ru.babobka.nodeslaveserver.controller;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.logging.Level;

import ru.babobka.nodeslaveserver.builder.HeartBeatingResponseBuilder;
import ru.babobka.nodeslaveserver.model.TasksStorage;

import ru.babobka.nodeslaveserver.runnable.RequestHandlerRunnable;
import ru.babobka.nodeslaveserver.server.SlaveServerContext;
import ru.babobka.nodeslaveserver.task.TaskPool;
import ru.babobka.nodeslaveserver.util.StreamUtil;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.SubTask;

public class SocketControllerImpl implements SocketController {

	private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private static TaskPool taskPool = TaskPool.getInstance();

	private final TasksStorage tasksStorage;

	public SocketControllerImpl(TasksStorage tasksStorage) {
		this.tasksStorage = tasksStorage;
	}

	@Override
	public void control(Socket socket) throws IOException {
		socket.setSoTimeout(SlaveServerContext.getInstance().getConfig().getRequestTimeoutMillis());
		NodeRequest request = (NodeRequest) StreamUtil.receiveObject(socket);
		if (request.isHeartBeatingRequest()) {
			StreamUtil.sendObject(HeartBeatingResponseBuilder.build(), socket);
		} else if (request.isStoppingRequest()) {
			SlaveServerContext.getInstance().getLogger().log(request.toString());
			tasksStorage.stopTask(request.getTaskId());
		} else if (request.isRaceStyle() && tasksStorage.exists(request.getTaskId())) {
			SlaveServerContext.getInstance().getLogger().log(Level.WARNING,
					request.getTaskName() + " is race style task. Repeated request was not handled.");
		} else {
			SlaveServerContext.getInstance().getLogger().log("Got request " + request);
			try {
				SubTask subTask = taskPool.get(request.getTaskName()).getTask();
				tasksStorage.put(request, subTask);
				threadPool.submit(new RequestHandlerRunnable(socket, tasksStorage, request, subTask));
			} catch (Exception e)

			{
				throw new IOException(e);
			}

		}

	}

	@Override
	public void close() throws IOException {
		threadPool.shutdownNow();
	}

}
