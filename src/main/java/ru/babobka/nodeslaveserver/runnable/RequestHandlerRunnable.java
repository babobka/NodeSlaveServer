package ru.babobka.nodeslaveserver.runnable;

import ru.babobka.nodeslaveserver.builder.BadResponseBuilder;
import ru.babobka.nodeslaveserver.server.ServerContext;
import ru.babobka.nodeslaveserver.task.TaskRunner;
import ru.babobka.nodeslaveserver.util.StreamUtil;
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

	public RequestHandlerRunnable(Socket socket, NodeRequest request, SubTask subTask) {
		this.socket = socket;
		this.request = request;
		this.subTask = subTask;
	}

	@Override
	public void run() {
		try {
			NodeResponse response = TaskRunner.runTask(request, subTask);
			if (!response.isStopped()) {
				synchronized (socket) {
					StreamUtil.sendObject(response, socket);
				}
				ServerContext.getInstance().getLogger().log(response.toString());
				ServerContext.getInstance().getLogger().log("Response was sent");
			}
		} catch (NullPointerException e) {
			synchronized (socket) {
				try {
					StreamUtil.sendObject(BadResponseBuilder.getInstance(request.getTaskId(), request.getRequestId(),
							request.getTaskName()), socket);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} catch (IOException e) {
			ServerContext.getInstance().getLogger().log(e);
			ServerContext.getInstance().getLogger().log(Level.SEVERE, "Response wasn't sent");
		}
	}
}