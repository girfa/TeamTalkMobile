package com.girfa.apps.teamtalk4mobile.api.adapter;

import java.util.Map;

import com.girfa.apps.teamtalk4mobile.api.enumflags.TextMessageType;

public class TextMessage {
	private TextMessageType textMessageType;
	private User srcUser;
	private User destUser;
	private Channel destChannel;
	private String content;
	
	private static final String
		TYPE = "type",
		SRC_USER_ID = "srcuserid",
		DEST_USER_ID = "destuserid",
		CONTENT = "content",
		CHANNEL = "channel",
		CHANNEL_ID = "channelid";
	
	public TextMessage() {}
	
	public TextMessage(Map<String, Object> map) {
		build(map);
	}
	
	public TextMessage build(Map<String, Object> map) {
		if (map == null) return this;
		if (map.containsKey(TYPE)) {
			setTextMessageType(TextMessageType.valueOf(Integer.valueOf(map.get(TYPE) + "")));
		}
		if (map.containsKey(SRC_USER_ID)) {
			setSrcUser(new User(Integer.valueOf(map.get(SRC_USER_ID) + "")));
		}
		if (map.containsKey(DEST_USER_ID)) {
			setDestUser(new User(Integer.valueOf(map.get(DEST_USER_ID) + "")));
		}
		if (map.containsKey(CONTENT)) {
			setContent(map.get(CONTENT) + "");
		}
		if (map.containsKey(CHANNEL_ID) || map.containsKey(CHANNEL)) {
			destChannel = new Channel();
			if (map.containsKey(CHANNEL_ID)) {
				destChannel.setId(Integer.valueOf(map.get(CHANNEL_ID) + ""));
			}
			if (map.containsKey(CHANNEL)) {
				destChannel.setURI(map.get(CHANNEL) + "");
			}
		}
		return this;
	}

	public TextMessage setTextMessageType(TextMessageType textMessageType) {
		this.textMessageType = textMessageType;
		return this;
	}
	
	public TextMessage setSrcUser(User srcUser) {
		this.srcUser = srcUser;
		return this;
	}
	
	public TextMessage setDestUser(User destUser) {
		this.destUser = destUser;
		return this;
	}
	
	public TextMessage setDestChannel(Channel destChannel) {
		this.destChannel = destChannel;
		return this;
	}
	
	public TextMessage setContent(String content) {
		this.content = content;
		return this;
	}
	
	public TextMessageType getTextMessageType() {
		return textMessageType;
	}

	public User getSrcUser() {
		return srcUser;
	}

	public User getDestUser() {
		return destUser;
	}

	public Channel getDestChannel() {
		return destChannel;
	}

	public String getContent() {
		return content;
	}
}
