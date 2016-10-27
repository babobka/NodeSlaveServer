package ru.babobka.nodeslaveserver.util;

import java.security.MessageDigest;

/**
 * Created by dolgopolov.a on 06.07.15.
 */
public class MathUtil {

	public static byte[] sha2(String message) {
		if (message != null) {
			try {
				MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
				byte[] messageBytes = message.getBytes();
				return sha256.digest(messageBytes);
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return new byte[] {};
	}
}
