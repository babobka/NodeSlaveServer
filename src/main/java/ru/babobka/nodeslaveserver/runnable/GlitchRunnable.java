package ru.babobka.nodeslaveserver.runnable;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import ru.babobka.container.Container;
import ru.babobka.nodeslaveserver.logger.SimpleLogger;

public class GlitchRunnable implements Runnable {

	private final Socket socket;

	private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

	public GlitchRunnable(Socket socket)

	{
		this.socket = socket;
	}

	@Override
	public void run() {
		Random random = new Random();
		while (!Thread.currentThread().isInterrupted()) {
			int timeToWaitSec = random.nextInt(60);
			logger.log("Seconds to glitch " + timeToWaitSec);
			try {
				Thread.sleep(timeToWaitSec * 1000L);
				try {
					logger.log("Closing socket in GlitchRunnable");
					socket.close();
				} catch (IOException e) {
					logger.log(e);
				}
			} catch (InterruptedException e) {
				logger.log(e);
				Thread.currentThread().interrupt();
			}
		}
	}

}
