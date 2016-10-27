package ru.babobka.nodeslaveserver.service;

import ru.babobka.nodeslaveserver.builder.AuthResponseBuilder;
import ru.babobka.nodeslaveserver.server.ServerContext;
import ru.babobka.nodeslaveserver.util.StreamUtil;
import ru.babobka.nodeserials.PublicKey;
import ru.babobka.nodeserials.RSA;

import java.net.Socket;

/**
 * Created by dolgopolov.a on 30.10.15.
 */
public class AuthServiceImpl implements AuthService {

	private static volatile AuthServiceImpl instance;

	private AuthServiceImpl() {

	}

	public static AuthServiceImpl getInstance() {
		AuthServiceImpl localInstance = instance;
		if (localInstance == null) {
			synchronized (AuthServiceImpl.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new AuthServiceImpl();
				}
			}
		}
		return localInstance;
	}

	@Override
	public boolean auth(Socket socket, String login, String password) {
		
		try {
			socket.setSoTimeout(ServerContext.getInstance().getConfig().getAuthTimeoutMillis());
			PublicKey publicKey = (PublicKey) StreamUtil.receiveObject(socket);
			StreamUtil.sendObject(AuthResponseBuilder.build(new RSA(null, publicKey), login, password), socket);
			
			return (Boolean) StreamUtil.receiveObject(socket);
		} catch (Exception e) {
			ServerContext.getInstance().getLogger().log(e);
			return false;
		} 
	}
}
