package ru.babobka.nodeslaveserver.runnable;

import java.io.IOException;
import java.net.Socket;
import ru.babobka.nodeslaveserver.server.ServerContext;

public class GlitchRunnable implements Runnable {

	private final Socket socket;

	public GlitchRunnable(Socket socket)

	{
		this.socket = socket;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			int timeToWaitSec = (int) (Math.random() * 300);
			ServerContext.getInstance().getLogger().log("Seconds to glitch " + timeToWaitSec);
			try {
				Thread.sleep(timeToWaitSec * 1000L);
				try {
					ServerContext.getInstance().getLogger().log("Closing socket in GlitchRunnable");
					socket.close();
				} catch (IOException e) {
					ServerContext.getInstance().getLogger().log(e);
				}
			} catch (InterruptedException e) {
				ServerContext.getInstance().getLogger().log(e);
				Thread.currentThread().interrupt();
			}
		}
	}

}
