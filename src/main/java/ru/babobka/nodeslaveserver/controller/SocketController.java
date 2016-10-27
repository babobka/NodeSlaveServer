package ru.babobka.nodeslaveserver.controller;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

public interface SocketController extends Closeable{

	void control(Socket socket) throws  IOException;
	
	
	
}
