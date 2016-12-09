package ru.babobka.nodeslaveserver.task;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.SubTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.jodah.expiringmap.ExpiringMap;

/**
 * Created by dolgopolov.a on 29.09.15.
 */
public class TasksStorage {

	private final Map<UUID, ConcurrentHashMap<UUID, SubTask>> tasksMap = new ConcurrentHashMap<>();

	private final Map<UUID, Long> stoppedTasksMap = ExpiringMap.builder()
			.expirationPolicy(ExpiringMap.ExpirationPolicy.CREATED).expiration(10, TimeUnit.MINUTES).build();

	public synchronized void put(NodeRequest request, SubTask subTask) {
		if (!tasksMap.containsKey(request.getTaskId())) {
			tasksMap.put(request.getTaskId(), new ConcurrentHashMap<UUID, SubTask>());
			tasksMap.get(request.getTaskId()).put(request.getRequestId(), subTask);

		} else {
			tasksMap.get(request.getTaskId()).put(request.getRequestId(), subTask);
		}

	}

	public synchronized boolean exists(UUID taskId) {
		return tasksMap.containsKey(taskId);
	}

	public synchronized void removeRequest(NodeRequest request) {
		ConcurrentHashMap<UUID, SubTask> localTaskMap = tasksMap.get(request.getTaskId());
		if (localTaskMap != null) {
			localTaskMap.remove(request.getRequestId());
			if (localTaskMap.isEmpty()) {

				tasksMap.remove(request.getTaskId());
			}
		}
	}

	public synchronized void stopTask(UUID taskId, long timeStamp) {
		stoppedTasksMap.put(taskId, timeStamp);
		Map<UUID, SubTask> localTaskMap = tasksMap.get(taskId);
		if (localTaskMap != null) {
			for (Map.Entry<UUID, SubTask> task : localTaskMap.entrySet()) {
				task.getValue().stopTask();
			}
		}
		tasksMap.remove(taskId);
	}

	public synchronized void stopAllTheTasks() {
		for (Map.Entry<UUID, ConcurrentHashMap<UUID, SubTask>> taskEntry : tasksMap.entrySet()) {
			stopTask(taskEntry.getKey(), System.currentTimeMillis());
		}
	}

	public synchronized boolean wasStopped(UUID taskId, long taskTimeStamp) {
		Long stoppedRequestTimeStamp = stoppedTasksMap.get(taskId);
		
		
		if (stoppedRequestTimeStamp != null) {
			return stoppedRequestTimeStamp > taskTimeStamp;
		}
		return false;
	}
	
	public synchronized void clear()
	{
		tasksMap.clear();
		stoppedTasksMap.clear();
	}
}
