package ru.babobka.nodeslaveserver.task;

import ru.babobka.nodeslaveserver.model.TaskMap;
import ru.babobka.nodeslaveserver.model.Timer;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.subtask.model.ExecutionResult;
import ru.babobka.subtask.model.SubTask;
import ru.babobka.subtask.model.ValidationResult;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public interface TaskRunner {


	public static NodeResponse runTask(NodeRequest request, SubTask subTask) {
		try {			
			ValidationResult validationResult = subTask
					.validateRequest(request);
			if (validationResult.isValid()) {
				Timer timer = new Timer();
				ExecutionResult result = subTask.execute(request);
				if (result.isStopped()) {
					return new NodeResponse(request.getTaskId(),
							request.getRequestId(), timer.getTimePassed(),
							NodeResponse.Status.STOPPED, null, null,
							request.getTaskName());
				} else {
					return new NodeResponse(request.getTaskId(),
							request.getRequestId(), timer.getTimePassed(),
							NodeResponse.Status.NORMAL, null,
							result.getResultMap(),
							request.getTaskName());
				}
			} else {
				return new NodeResponse(request.getTaskId(),
						request.getRequestId(), -1, NodeResponse.Status.FAILED,
						validationResult.getMessage(), null,
						request.getTaskName());
			}

		} finally {
			TaskMap.removeRequest(request);
		}
	}

}
