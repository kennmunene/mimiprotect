package com.variance.mimiprotect.chat;

import com.variance.vjax.android.VObjectMarshaller;
import com.variance.vjax.android.VXMLBindingException;
import com.variance.vjax.android.VXMLMemberNotFoundException;
import com.variance.vjax.android.annotations.Transient;
import com.variance.vjax.xml.VDocument;

public class ChatMessage {
	private String message;
	private String ip;
	private int port;
	private ChatMessageType chatMessageType;
	private String chatMessageId;
	@Transient
	private ChatType chatType;
	@Transient
	private String contactId;

	public ChatMessage() {
		super();
	}

	public ChatMessage(String message, String ip, int port) {
		this(message, ip, port, ChatMessageType.USER_MESSAGE);
	}

	public ChatMessage(String message, String ip, int port,
			ChatMessageType chatMessageType) {
		this(message, ip, port, chatMessageType, System.currentTimeMillis()
				+ "");
	}

	public ChatMessage(String message, String ip, int port,
			ChatMessageType chatMessageType, String chatMessageId) {
		super();
		this.message = message;
		this.ip = ip;
		this.port = port;
		this.chatMessageType = chatMessageType;
		this.chatMessageId = chatMessageId;
	}

	/**
	 * Returns null if the message cannot be decoded into a ChatMessage
	 * 
	 * @return
	 */
	public static ChatMessage getInstance(String message) {
		try {
			return new VObjectMarshaller<ChatMessage>(ChatMessage.class)
					.unmarshall(VDocument.parseDocumentFromString(message));
		} catch (VXMLMemberNotFoundException e) {
			e.printStackTrace();
		} catch (VXMLBindingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public ChatType getChatType() {
		return chatType;
	}

	public void setChatType(ChatType chatType) {
		this.chatType = chatType;
	}

	public String getChatMessageId() {
		return chatMessageId;
	}

	public void setChatMessageId(String chatMessageId) {
		this.chatMessageId = chatMessageId;
	}

	public ChatMessageType getChatMessageType() {
		return chatMessageType;
	}

	public void setChatMessageType(ChatMessageType chatMessageType) {
		this.chatMessageType = chatMessageType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String toString() {
		return new VObjectMarshaller<ChatMessage>(ChatMessage.class)
				.doMarshall(this);
	}
}
