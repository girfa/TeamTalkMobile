package com.girfa.apps.teamtalk4mobile.api.adapter;

public class Server {
	private Integer id;
	private String name;
	private String password;
	private String address;
	private Integer tcpPort;
	private Integer udpPort;
	private String authUsername;
	private String authPassword;
	private String joinChannel;
	private String joinPassword;
	private Integer type;
	
	public Server setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Server setName(String name) {
		this.name = name;
		return this;
	}
	
	public Server setPassword(String password) {
		this.password = password;
		return this;
	}
	
	public Server setAddress(String address) {
		this.address = address;
		return this;
	}
	
	public Server setTcpPort(Integer tcpPort) {
		this.tcpPort = tcpPort;
		return this;
	}
	
	public Server setUdpPort(Integer udpPort) {
		this.udpPort = udpPort;
		return this;
	}
	
	public Server setAuthUsername(String authUsername) {
		this.authUsername = authUsername;
		return this;
	}
	
	public Server setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
		return this;
	}
	
	public Server setJoinChannel(String joinChannel) {
		this.joinChannel = joinChannel;
		return this;
	}
	
	public Server setJoinPassword(String joinPassword) {
		this.joinPassword = joinPassword;
		return this;
	}
	
	public Server setType(Integer type) {
		this.type = type;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getAddress() {
		return address;
	}

	public Integer getTcpPort() {
		return tcpPort;
	}

	public Integer getUdpPort() {
		return udpPort;
	}

	public String getAuthUsername() {
		return authUsername;
	}

	public String getAuthPassword() {
		return authPassword;
	}

	public String getJoinChannel() {
		return joinChannel;
	}

	public String getJoinPassword() {
		return joinPassword;
	}

	public Integer getType() {
		return type;
	}
}
