package ru.babobka.nodeslaveserver.service;

import java.net.Socket;

/**
 * Created by dolgopolov.a on 30.10.15.
 */
public interface AuthService {

    public boolean auth(Socket socket, String login, String password);
}
