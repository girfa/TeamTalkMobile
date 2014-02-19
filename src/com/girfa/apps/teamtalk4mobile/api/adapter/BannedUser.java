package com.girfa.apps.teamtalk4mobile.api.adapter;

public class BannedUser {
	private String ipAddress;
	private String channelPath;
	private String banTime;
	private String nickname;
	private String username;
	
	public BannedUser setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
		return this;
	}
	
	public BannedUser setChannelPath(String channelPath) {
		this.channelPath = channelPath;
		return this;
	}
	
	public BannedUser setBanTime(String banTime) {
		this.banTime = banTime;
		return this;
	}
	
	public BannedUser setNickname(String nickname) {
		this.nickname = nickname;
		return this;
	}
	
	public BannedUser setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getChannelPath() {
		return channelPath;
	}

	public String getBanTime() {
		return banTime;
	}

	public String getNickname() {
		return nickname;
	}

	public String getUsername() {
		return username;
	}
}
