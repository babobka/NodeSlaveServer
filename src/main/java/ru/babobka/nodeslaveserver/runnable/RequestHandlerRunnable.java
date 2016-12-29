package ru.babobka.nodeslaveserver.runnable;

import ru.babobka.nodeslaveserver.builder.BadResponseBuilder;
import ru.babobka.nodeslaveserver.logger.SimpleLogger;
import ru.babobka.nodeslaveserver.task.TaskRunner;
import ru.babobka.nodeslaveserver.task.TasksStorage;
import ru.babobka.nodeslaveserver.util.StreamUtil;
import ru.babobka.container.Container;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.subtask.model.SubTask;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

/**
 * Created by dolgopolov.a on 27.07.15.
 */
public class RequestHandlerRunnable implements Runnable {

	private final Socket socket;

	private final NodeRequest request;

	private final SubTask subTask;

	private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

	private final TasksStorage tasksStorage;

	public RequestHandlerRunnable(Socket socket, TasksStorage tasksStorage, NodeRequest request, SubTask subTask) {
		this.socket = socket;
		this.request = request;
		this.subTask = subTask;
		this.tasksStorage = tasksStorage;
	}

	@Override
	public void run() {
		try {
			NodeResponse response = TaskRunner.runTask(tasksStorage, request, subTask);
			if (!response.isStopped()) {
				StreamUtil.sendObject(response, socket);
				logger.log(response.toString());
				logger.log("Response was sent");
			}
		} catch (NullPointerException e) {
			try {
				StreamUtil.sendObject(BadResponseBuilder.getInstance(request.getTaskId(), request.getRequestId(),
						request.getTaskName()), socket);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} catch (IOException e) {
			logger.log(e);
			logger.log(Level.SEVERE, "Response wasn't sent");
		}
	}
}