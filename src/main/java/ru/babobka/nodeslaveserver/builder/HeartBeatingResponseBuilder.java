package ru.babobka.nodeslaveserver.builder;

import ru.babobka.nodeserials.Mappings;
import ru.babobka.nodeserials.NodeResponse;

public class HeartBeatingResponseBuilder {

	private HeartBeatingResponseBuilder() {

	}

	public static NodeResponse build() {
		return new NodeResponse((long)(Math.random()*Long.MAX_VALUE), 0,
				0, NodeResponse.Status.NORMAL, null, null, Mappings.HEART_BEAT_TASK_NAME);
	}

}
