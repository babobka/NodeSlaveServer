package ru.babobka.nodeslaveserver.task;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.SubTask;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TasksStorageTest {

	private SubTask mockTask = mock(SubTask.class);

	private TasksStorage storage;

	@Before
	public void setUp() {
		storage = new TasksStorage();
	}

	@After
	public void tearDown() {
		storage.clear();
	}

	@Test
	public void testStopTaskNormal() {
		UUID taskId = UUID.randomUUID();
		long timeStamp=System.currentTimeMillis()+10;
		NodeRequest requestOne = new NodeRequest(taskId, false, "test");
		storage.put(requestOne, mockTask);
		storage.stopTask(taskId, timeStamp);
		assertTrue(storage.wasStopped(taskId, requestOne.getTimeStamp()));
	}
	
	@Test
	public void testStopTaskReorder() {
		UUID taskId = UUID.randomUUID();
		long timeStamp=System.currentTimeMillis()-10;
		NodeRequest requestOne = new NodeRequest(taskId, false, "test");
		storage.put(requestOne, mockTask);
		storage.stopTask(taskId, timeStamp);
		assertFalse(storage.wasStopped(taskId, requestOne.getTimeStamp()));
	}

	
	@Test
	public void testStopTwoTaskNormal() {
		UUID taskId = UUID.randomUUID();
		long timeStamp=System.currentTimeMillis()+10;
		NodeRequest requestOne = new NodeRequest(taskId, false, "test");
		NodeRequest requestTwo = new NodeRequest(taskId, false, "test");
		storage.put(requestOne, mockTask);
		storage.put(requestTwo, mockTask);
		storage.stopTask(taskId, timeStamp);
		assertTrue(storage.wasStopped(taskId, requestOne.getTimeStamp()));
		assertTrue(storage.wasStopped(taskId, requestTwo.getTimeStamp()));
	}
	
	@Test
	public void testStopTwoTaskReorder() {
		UUID taskId = UUID.randomUUID();
		long timeStamp=System.currentTimeMillis()-10;
		NodeRequest requestOne = new NodeRequest(taskId, false, "test");
		NodeRequest requestTwo = new NodeRequest(taskId, false, "test");
		storage.stopTask(taskId, timeStamp);
		storage.put(requestOne, mockTask);
		storage.put(requestTwo, mockTask);
		assertFalse(storage.wasStopped(taskId, requestOne.getTimeStamp()));
		assertFalse(storage.wasStopped(taskId, requestTwo.getTimeStamp()));
	}
	
	
}
