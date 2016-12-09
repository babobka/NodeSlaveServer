package ru.babobka.nodeslaveserver.builder;

import java.util.UUID;

import ru.babobka.nodeserials.NodeResponse;

/**
 * Created by dolgopolov.a on 03.08.15.
 */
public class BadResponseBuilder {
	
	private BadResponseBuilder()
	{
		
	}

	public static NodeResponse getInstance(UUID taskId, UUID requestId,
			String uri) {
		return new NodeResponse(taskId, requestId, -1,
				NodeResponse.Status.FAILED, null, null, uri);
	}
}
