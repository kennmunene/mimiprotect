package com.variance.mimiprotect.chat.interfaces;

public interface MessageReceiver {

	public void messageReceived(String message);

	void socketClosed();
}
