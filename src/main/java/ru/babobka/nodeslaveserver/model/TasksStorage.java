package ru.babobka.nodeslaveserver.model;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.SubTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dolgopolov.a on 29.09.15.
 */
public class TasksStorage {

	private final Map<Long, ConcurrentHashMap<Long, SubTask>> map = new ConcurrentHashMap<>();



	public synchronized void put(NodeRequest request, SubTask subTask) {
		if (!map.containsKey(request.getTaskId())) {
			map.put(request.getTaskId(), new ConcurrentHashMap<Long, SubTask>());
			map.get(request.getTaskId()).put(request.getRequestId(), subTask);

		} else {
			map.get(request.getTaskId()).put(request.getRequestId(), subTask);
		}

	}

	public synchronized boolean exists(Long taskId) {
		return map.containsKey(taskId);
	}

	public synchronized void removeRequest(NodeRequest request) {
		ConcurrentHashMap<Long, SubTask> localTaskMap = map.get(request.getTaskId());
		if (localTaskMap != null) {
			localTaskMap.remove(request.getRequestId());
			if (localTaskMap.isEmpty()) {

				map.remove(request.getTaskId());
			}
		}
	}



	public synchronized void stopTask(Long taskId) {
		ConcurrentHashMap<Long, SubTask> localTaskMap = map.get(taskId);
		if (localTaskMap != null) {
			for (ConcurrentHashMap.Entry<Long, SubTask> task : localTaskMap.entrySet()) {
				task.getValue().stopTask();

			}
		}
		map.remove(taskId);
	}

	public synchronized void stopAllTheTasks() {
		for (Map.Entry<Long, ConcurrentHashMap<Long, SubTask>> taskEntry : map.entrySet()) {
			stopTask(taskEntry.getKey());
		}
	}
}
