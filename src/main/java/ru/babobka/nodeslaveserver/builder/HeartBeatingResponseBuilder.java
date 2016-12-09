package ru.babobka.nodeslaveserver.builder;

import java.util.UUID;

import ru.babobka.nodeserials.Mappings;
import ru.babobka.nodeserials.NodeResponse;

public class HeartBeatingResponseBuilder {

	private HeartBeatingResponseBuilder() {

	}

	public static NodeResponse build() {
		return new NodeResponse(UUID.randomUUID(), UUID.randomUUID(), 0, NodeResponse.Status.NORMAL, null, null,
				Mappings.HEART_BEAT_TASK_NAME);
	}

}
