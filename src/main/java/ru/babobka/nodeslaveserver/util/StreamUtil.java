package ru.babobka.nodeslaveserver.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

import ru.babobka.nodeslaveserver.classloader.JarClassLoader;
import ru.babobka.subtask.model.SubTask;

/**
 * Created by dolgopolov.a on 08.07.15.
 */
public class StreamUtil {

	private StreamUtil() {

	}

	public static String readFile(InputStream is) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(is).useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";

		} finally {
			if (scanner != null) {
				scanner.close();
			}
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static JSONObject getConfigJson(String jarFilePath) throws IOException {
		return new JSONObject(readTextFileFromJar(jarFilePath, "task.json"));
	}

	public static String readTextFileFromJar(String jarFilePath, String fileName) throws IOException {
		URL url = new URL("jar:file:" + jarFilePath + "!/" + fileName);
		InputStream is = url.openStream();
		return readFile(is);
	}

	public static SubTask getTaskClassFromJar(String jarFilePath, String className) throws IOException {
		try {
			JarClassLoader jarLoader = new JarClassLoader(jarFilePath);
			return (SubTask) (jarLoader.loadClass(className, true).newInstance());
		} catch (Exception e) {
			throw new IOException("Can not get " + className, e);
		}
	}

	public static List<String> getJarFileListFromFolder(String folderPath) {
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		LinkedList<String> files = new LinkedList<>();
		if (listOfFiles != null) {
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile() && listOfFiles[i].getAbsolutePath().endsWith(".jar")) {
					files.add(listOfFiles[i].getName());
				}
			}
		}
		return files;
	}

	public static String readFile(String filename) {
		String content = null;
		File file = new File(filename);
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content;
	}

	public static String getRunningFolder() throws URISyntaxException {
		String folder = new File(StreamUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
				.toString();
		if (folder.endsWith(".jar")) {
			folder = folder.substring(0, folder.lastIndexOf(File.separator));
		}
		return folder;

	}

	public static void sendObject(Object object, Socket socket) throws IOException {
		byte[] message = objectToByteArray(object);
		DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
		dOut.writeInt(message.length);
		dOut.write(message);
		socket.getOutputStream().flush();
	}

	public static Object receiveObject(Socket socket) throws IOException {
		DataInputStream dIn = new DataInputStream(socket.getInputStream());
		int length = dIn.readInt();
		if (length > 0) {
			byte[] message = new byte[length];
			dIn.readFully(message, 0, message.length);
			try {
				return byteArrayToObject(message);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}
		return null;
	}

	private static Object byteArrayToObject(byte[] byteArray) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			return in.readObject();
		} finally {
			try {
				bis.close();
			} catch (IOException ex) {
				// ignore close exception
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
		}
	}

	private static byte[] objectToByteArray(Object object) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(object);
			return bos.toByteArray();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
			try {
				bos.close();
			} catch (IOException ex) {
				// ignore close exception
			}
		}
	}

	public static LinkedList<String> getFileListFromFolder(String folderPath) {

		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		LinkedList<String> files = new LinkedList<>();
		if (listOfFiles != null) {
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					files.add(listOfFiles[i].getName());
				}
			}
		}
		return files;
	}

	public static Class<?> getTaskClassFromJar(String jarFilePath) throws ClassNotFoundException {
		String className = "subtask.Task";
		JarClassLoader jarLoader = new JarClassLoader(jarFilePath);
		return jarLoader.loadClass(className, true);

	}

}
