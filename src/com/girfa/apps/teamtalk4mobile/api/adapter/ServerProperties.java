package com.girfa.apps.teamtalk4mobile.api.adapter;

import java.util.Map;

import com.girfa.apps.teamtalk4mobile.api.bitflags.UserRights;

public class ServerProperties extends ConfigDump {
	private static final String TAG = ServerProperties.class.getSimpleName();
	
	private String serverName;
	private String serverPassword;
	private String motd;
	private String motdRaw;
	private UserRights userRights;
	private Integer maxUsers;
	private Integer maxLoginAttempts;
	private Integer maxLoginsPerIPAddress;
	private Integer audioCodecBpsLimit;
	private Integer maxAudioTxPerSecond;
	private Integer maxVideoTxPerSecond;
	private Integer maxDesktopTxPerSecond;
	private Integer maxTotalTxPerSecond;
	private Boolean autoSave;
	private Integer tcpPort;
	private Integer udpPort;
	private Integer userTimeout;
	private String serverVersion;
	private String serverProtocolVersion;
	
	private static final String
		SERVER_NAME = "servername",
		USER_RIGHTS = "userrights",
		MAX_USERS = "maxusers",
		MAX_IP_LOGINS = "maxiplogins",
		USER_TIMEOUT = "usertimeout",
		MOTD = "motd",
		MOTD_RAW = "motdraw",
		SERVER_PASSWORD = "serverpassword",
		MAX_LOGIN_ATTEMPTS = "maxloginattempts",
		AUTOSAVE = "autosave",
		TCP_PORT = "tcpport",
		UDP_PORT = "udpport",
		AUDIO_CODEC_LIMIT = "audiocodeclimit",
		AUDIO_TX_LIMIT = "audiotxlimit",
		VIDEO_TX_LIMIT = "videotxlimit",
		DESKTOP_TX_LIMIT = "desktoptxlimit",
		TOTAL_TX_LIMIT = "totaltxlimit",
		VERSION = "version",
		PROTOCOL = "protocol";
	
	public ServerProperties() {}
	
	public ServerProperties(Map<String, Object> map) {
		build(map);
	}
	
	public ServerProperties build(Map<String, Object> map) {
		if (map == null) return this;
		if (map.containsKey(SERVER_NAME)) {
			setServerName(map.get(SERVER_NAME) + "");
		}
		if (map.containsKey(USER_RIGHTS)) {
			setUserRights(new UserRights(Integer.valueOf(map.get(USER_RIGHTS) + "")));
		}
		if (map.containsKey(MAX_USERS)) {
			setMaxUsers(Integer.valueOf(map.get(MAX_USERS) + ""));
		}
		if (map.containsKey(MAX_IP_LOGINS)) {
			setMaxLoginsPerIPAddress(Integer.valueOf(map.get(MAX_IP_LOGINS) + ""));
		}
		if (map.containsKey(USER_TIMEOUT)) {
			setUserTimeout(Integer.valueOf(map.get(USER_TIMEOUT) + ""));
		}
		if (map.containsKey(MOTD)) {
			setMOTD(map.get(MOTD) + "");
		}
		if (map.containsKey(MOTD_RAW)) {
			setMOTDRaw(map.get(MOTD_RAW) + "");
		}
		if (map.containsKey(SERVER_PASSWORD)) {
			setServerPassword(map.get(SERVER_PASSWORD) + "");
		}
		if (map.containsKey(MAX_LOGIN_ATTEMPTS)) {
			setMaxLoginAttempts(Integer.valueOf(map.get(MAX_LOGIN_ATTEMPTS) + ""));
		}
		if (map.containsKey(AUTOSAVE)) {
			setAutoSave(Integer.valueOf(map.get(AUTOSAVE) + "") != 0);
		}
		if (map.containsKey(TCP_PORT)) {
			setTcpPort(Integer.valueOf(map.get(TCP_PORT) + ""));
		}
		if (map.containsKey(UDP_PORT)) {
			setUdpPort(Integer.valueOf(map.get(UDP_PORT) + ""));
		}
		if (map.containsKey(AUDIO_CODEC_LIMIT)) {
			setAudioCodecBpsLimit(Integer.valueOf(map.get(AUDIO_CODEC_LIMIT) + ""));
		}
		if (map.containsKey(AUDIO_TX_LIMIT)) {
			setMaxAudioTxPerSecond(Integer.valueOf(map.get(AUDIO_TX_LIMIT) + ""));
		}
		if (map.containsKey(VIDEO_TX_LIMIT)) {
			setMaxVideoTxPerSecond(Integer.valueOf(map.get(VIDEO_TX_LIMIT) + ""));
		}
		if (map.containsKey(DESKTOP_TX_LIMIT)) {
			setMaxDesktopTxPerSecond(Integer.valueOf(map.get(DESKTOP_TX_LIMIT) + ""));
		}
		if (map.containsKey(TOTAL_TX_LIMIT)) {
			setMaxTotalTxPerSecond(Integer.valueOf(map.get(TOTAL_TX_LIMIT) + ""));
		}
		if (map.containsKey(VERSION)) {
			setServerVersion(map.get(VERSION) + "");
		}
		if (map.containsKey(PROTOCOL)) {
			setServerProtocolVersion(map.get(PROTOCOL) + "");
		}
		return this;
	}
	
	public ServerProperties setServerName(String serverName) {
		setString(TAG + 1, serverName);
		this.serverName = serverName;
		return this;
	}
	
	public ServerProperties setServerPassword(String serverPassword) {
		setString(TAG + 2, serverPassword);
		this.serverPassword = serverPassword;
		return this;
	}
	
	public ServerProperties setMOTD(String motd) {
		setString(TAG + 3, motd);
		this.motd = motd;
		return this;
	}
	
	public ServerProperties setMOTDRaw(String motdRaw) {
		setString(TAG + 4, motdRaw);
		this.motdRaw = motdRaw;
		return this;
	}
	
	public ServerProperties setUserRights(UserRights userRights) {
		if (userRights != null) setInt(TAG + 5, userRights.getFlags());
		this.userRights = userRights;
		return this;
	}
	
	public ServerProperties setMaxUsers(Integer maxUsers) {
		setInt(TAG + 6, maxUsers);
		this.maxUsers = maxUsers;
		return this;
	}
	
	public ServerProperties setMaxLoginAttempts(Integer maxLoginAttempts) {
		setInt(TAG + 7, maxLoginAttempts);
		this.maxLoginAttempts = maxLoginAttempts;
		return this;
	}
	
	public ServerProperties setMaxLoginsPerIPAddress(Integer maxLoginsPerIPAddress) {
		setInt(TAG + 8, maxLoginsPerIPAddress);
		this.maxLoginsPerIPAddress = maxLoginsPerIPAddress;
		return this;
	}
	
	public ServerProperties setAudioCodecBpsLimit(Integer audioCodecBpsLimit) {
		setInt(TAG + 9, audioCodecBpsLimit);
		this.audioCodecBpsLimit = audioCodecBpsLimit;
		return this;
	}
	
	public ServerProperties setMaxAudioTxPerSecond(Integer maxAudioTxPerSecond) {
		setInt(TAG + 10, maxAudioTxPerSecond);
		this.maxAudioTxPerSecond = maxAudioTxPerSecond;
		return this;
	}
	
	public ServerProperties setMaxVideoTxPerSecond(Integer maxVideoTxPerSecond) {
		setInt(TAG + 11, maxVideoTxPerSecond);
		this.maxVideoTxPerSecond = maxVideoTxPerSecond;
		return this;
	}
	
	public ServerProperties setMaxDesktopTxPerSecond(Integer maxDesktopTxPerSecond) {
		setInt(TAG + 12, maxDesktopTxPerSecond);
		this.maxDesktopTxPerSecond = maxDesktopTxPerSecond;
		return this;
	}
	
	public ServerProperties setMaxTotalTxPerSecond(Integer maxTotalTxPerSecond) {
		setInt(TAG + 13, maxTotalTxPerSecond);
		this.maxTotalTxPerSecond = maxTotalTxPerSecond;
		return this;
	}
	
	public ServerProperties setAutoSave(Boolean autoSave) {
		setBoolean(TAG + 14, autoSave);
		this.autoSave = autoSave;
		return this;
	}
	
	public ServerProperties setTcpPort(Integer tcpPort) {
		setInt(TAG + 15, tcpPort);
		this.tcpPort = tcpPort;
		return this;
	}
	
	public ServerProperties setUdpPort(Integer udpPort) {
		setInt(TAG + 16, udpPort);
		this.udpPort = udpPort;
		return this;
	}
	
	public ServerProperties setUserTimeout(Integer userTimeout) {
		setInt(TAG + 17, userTimeout);
		this.userTimeout = userTimeout;
		return this;
	}
	
	public ServerProperties setServerVersion(String serverVersion) {
		setString(TAG + 18, serverVersion);
		this.serverVersion = serverVersion;
		return this;
	}
	
	public ServerProperties setServerProtocolVersion(String serverProtocolVersion) {
		setString(TAG + 19, serverProtocolVersion);
		this.serverProtocolVersion = serverProtocolVersion;
		return this;
	}
	
	public String getServerName() {
		if (isDump()) return getString(TAG + 1, null);
		return serverName;
	}
	
	public String getServerPassword() {
		if (isDump()) return getString(TAG + 2, null);
		return serverPassword;
	}
	
	public String getMOTD() {
		if (isDump()) return getString(TAG + 3, null);
		return motd;
	}
	
	public String getMOTDRaw() {
		if (isDump()) return getString(TAG + 4, null);
		return motdRaw;
	}
	
	public UserRights getUserRights() {
		if (isDump()) return new UserRights(getInt(TAG + 5, 0));
		return userRights;
	}
	
	public Integer getMaxUsers() {
		if (isDump()) return getInt(TAG + 6, 0);
		return maxUsers;
	}
	
	public Integer getMaxLoginAttempts() {
		if (isDump()) return getInt(TAG + 7, 0);
		return maxLoginAttempts;
	}
	
	public Integer getMaxLoginsPerIPAddress() {
		if (isDump()) return getInt(TAG + 8, 0);
		return maxLoginsPerIPAddress;
	}
	
	public Integer getAudioCodecBpsLimit() {
		if (isDump()) return getInt(TAG + 9, 0);
		return audioCodecBpsLimit;
	}
	
	public Integer getMaxAudioTxPerSecond() {
		if (isDump()) return getInt(TAG + 10, 0);
		return maxAudioTxPerSecond;
	}
	
	public Integer getMaxVideoTxPerSecond() {
		if (isDump()) return getInt(TAG + 11, 0);
		return maxVideoTxPerSecond;
	}
	
	public Integer getMaxDesktopTxPerSecond() {
		if (isDump()) return getInt(TAG + 12, 0);
		return maxDesktopTxPerSecond;
	}
	
	public Integer getMaxTotalTxPerSecond() {
		if (isDump()) return getInt(TAG + 13, 0);
		return maxTotalTxPerSecond;
	}
	
	public Boolean isAutoSave() {
		if (isDump()) return getBoolean(TAG + 14, false);
		return autoSave;
	}
	
	public Integer getTcpPort() {
		if (isDump()) return getInt(TAG + 15, 0);
		return tcpPort;
	}
	
	public Integer getUdpPort() {
		if (isDump()) return getInt(TAG + 16, 0);
		return udpPort;
	}
	
	public Integer getUserTimeout() {
		if (isDump()) return getInt(TAG + 17, 0);
		return userTimeout;
	}
	
	public String getServerVersion() {
		if (isDump()) return getString(TAG + 18, null);
		return serverVersion;
	}
	
	public String getServerProtocolVersion() {
		if (isDump()) return getString(TAG + 19, null);
		return serverProtocolVersion;
	}

	@Override
	public void reset() {
		for (int i = 1; i <= 19; i++) {
			remove(TAG + i);
		}
	}
}
