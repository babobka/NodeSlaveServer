package ru.babobka.nodeslaveserver.controller;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.logging.Level;

import ru.babobka.nodeslaveserver.builder.HeartBeatingResponseBuilder;
import ru.babobka.nodeslaveserver.model.TaskMap;

import ru.babobka.nodeslaveserver.runnable.RequestHandlerRunnable;
import ru.babobka.nodeslaveserver.server.ServerContext;
import ru.babobka.nodeslaveserver.task.TaskPool;
import ru.babobka.nodeslaveserver.util.StreamUtil;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.SubTask;

public class SocketControllerImpl implements SocketController {

	private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private static TaskPool taskPool = TaskPool.getInstance();

	@Override
	public void control(Socket socket) throws IOException {
		socket.setSoTimeout(ServerContext.getInstance().getConfig().getRequestTimeoutMillis());
		NodeRequest request = (NodeRequest) StreamUtil.receiveObject(socket);
		if (request.isHeartBeatingRequest()) {
			StreamUtil.sendObject(HeartBeatingResponseBuilder.build(), socket);
		} else if (request.isStoppingRequest()) {
			ServerContext.getInstance().getLogger().log(request.toString());
			TaskMap.stopTask(request.getTaskId());
		} else if (request.isRaceStyle() && TaskMap.exists(request.getTaskId())) {
			ServerContext.getInstance().getLogger().log(Level.WARNING,
					request.getTaskName() + " is race style task. Repeated request was not handled.");
		} else {
			ServerContext.getInstance().getLogger().log(request.toString());
			try {
				SubTask subTask = taskPool.get(request.getTaskName()).getTask();
				TaskMap.put(request, subTask);
				threadPool.submit(new RequestHandlerRunnable(socket, request, subTask));
			} catch (Exception e)

			{
				throw new IOException(e);
			}

		}

	}

	@Override
	public void close() throws IOException {
		threadPool.shutdown();
	}

}
