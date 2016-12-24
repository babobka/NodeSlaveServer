package ru.babobka.nodeslaveserver.service;

import ru.babobka.nodeslaveserver.builder.AuthResponseBuilder;
import ru.babobka.nodeslaveserver.log.SimpleLogger;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeslaveserver.util.StreamUtil;
import ru.babobka.container.Container;
import ru.babobka.nodeserials.PublicKey;
import ru.babobka.nodeserials.RSA;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by dolgopolov.a on 30.10.15.
 */
public class AuthServiceImpl implements AuthService {

	private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

	private final SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);

	@Override
	public boolean auth(Socket socket, String login, String password) {

		try {
			socket.setSoTimeout(slaveServerConfig.getAuthTimeoutMillis());
			PublicKey publicKey = (PublicKey) StreamUtil.receiveObject(socket);
			StreamUtil.sendObject(AuthResponseBuilder.build(new RSA(null, publicKey), login, password), socket);

			return (Boolean) StreamUtil.receiveObject(socket);
		} catch (IOException e) {
			logger.log(e);
			return false;
		}
	}
}
