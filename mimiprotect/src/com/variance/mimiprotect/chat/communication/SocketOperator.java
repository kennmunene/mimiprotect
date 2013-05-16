package com.variance.mimiprotect.chat.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;

import android.util.Log;

import com.variance.mimiprotect.chat.interfaces.ISocketOperator;
import com.variance.mimiprotect.chat.interfaces.MessageReceiver;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.Utils;

public class SocketOperator implements ISocketOperator {
	private class ReceiveConnection extends Thread {
		Socket clientSocket = null;

		public ReceiveConnection(Socket socket) {
			this.clientSocket = socket;
			SocketOperator.this.sockets.put(socket.getInetAddress(), socket);
			Log.i("ClientSocket:", this.clientSocket.getInetAddress() + "");
		}

		@Override
		public void run() {
			try {
				Log.e("Receive Socket connection:", "Receiving data");
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));
				synchronized (SocketOperator.this) {
					SocketOperator.this.wait(100); // wait for a few seconds for
													// quacks in delayed data.
				}
				String message = null;
				if (reader.ready()) {
					message = reader.readLine(); // the message comes in
													// as a
													// single line.
				}
				Log.i("Receive Socket connection:", "Received data: " + message);
				if (!Utils.isNullStringOrEmpty(message)) {
					Log.e("Receive Socket connection:", "data: " + message);
					messageReceiver.messageReceived(message);
				}
			} catch (Exception e) {
				Log.e("ReceiveConnection.run: when receiving connection ", ""
						+ e.toString());
			}
		}
	}

	private static final String AUTHENTICATION_SERVER_ADDRESS = "http://localhost/android_im/";

	private int listeningPort = 0;
	private static final String HTTP_REQUEST_FAILED = null;
	private HashMap<InetAddress, Socket> sockets = new HashMap<InetAddress, Socket>();
	private ServerSocket serverSocket = null;
	private boolean listening;
	private MessageReceiver messageReceiver;

	public SocketOperator(MessageReceiver appManager) {
		this.messageReceiver = appManager;
	}

	public String sendHttpRequest(String params) {
		URL url;
		String result = new String();
		try {
			url = new URL(AUTHENTICATION_SERVER_ADDRESS);
			HttpURLConnection connection;
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);

			PrintWriter out = new PrintWriter(connection.getOutputStream());

			out.println(params);
			out.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				result = result.concat(inputLine);
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (result.length() == 0) {
			result = HTTP_REQUEST_FAILED;
		}

		return result;

	}

	public boolean sendMessage(String message, String ip, int port) {
		try {
			String[] str = ip.split("\\.");
			byte[] IP = new byte[str.length];
			for (int i = 0; i < str.length; i++) {
				IP[i] = (byte) Integer.parseInt(str[i]);
			}
			Socket socket = getSocket(InetAddress.getByAddress(IP), port);
			if (socket == null) {
				return false;
			}
			OutputStream out = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(out);
			writer.println(message);
			writer.flush();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public int startListening(int portNo) {
		listening = true;
		try {
			serverSocket = new ServerSocket(portNo);
			this.listeningPort = portNo;
			Settings.setChatPort(listeningPort);
		} catch (Exception e) {
			e.printStackTrace();
			this.listeningPort = 0;
			return 0;
		}
		while (listening) {
			Log.i("startListening:socket", listening + "");
			try {
				new ReceiveConnection(serverSocket.accept()).start();
			} catch (Exception e) {
				e.printStackTrace();
				return 2;
			}
		}
		Log.i("Listening error", "socket Listening stopped");
		try {
			serverSocket.close();
		} catch (IOException e) {
			Log.e("Exception server socket",
					"Exception when closing server socket");
			return 3;
		}
		return 1;
	}

	public void stopListening() {
		this.listening = false;
	}

	private Socket getSocket(InetAddress addr, int portNo) {
		Socket socket = null;
		if (sockets.containsKey(addr)) {
			socket = sockets.get(addr);
			// check the status of the socket
			if (socket.isConnected() || socket.isInputShutdown()
					|| socket.isOutputShutdown() || socket.getPort() != portNo) {
				// if socket is not suitable, then create a new socket
				sockets.remove(addr);
				try {
					socket.shutdownInput();
					socket.shutdownOutput();
					socket.close();
					socket = new Socket(addr, portNo);
					sockets.put(addr, socket);
				} catch (IOException e) {
					Log.e("getSocket: when closing and removing", "");
				}
			}
		} else {
			try {
				socket = new Socket(addr, portNo);
				sockets.put(addr, socket);
			} catch (IOException e) {
				Log.e("getSocket: when creating", "");
			}
		}
		return socket;
	}

	public void exit() {
		try {
			for (Iterator<Socket> iterator = sockets.values().iterator(); iterator
					.hasNext();) {
				Socket socket = (Socket) iterator.next();
				try {
					socket.shutdownInput();
					socket.shutdownOutput();
					socket.close();
				} catch (IOException e) {
				}
			}
			sockets.clear();
			this.stopListening();
			messageReceiver = null;
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getListeningPort() {
		return this.listeningPort;
	}

}
