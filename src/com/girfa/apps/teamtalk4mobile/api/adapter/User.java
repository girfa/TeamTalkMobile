package com.girfa.apps.teamtalk4mobile.api.adapter;

import java.util.Map;

import com.girfa.apps.teamtalk4mobile.api.bitflags.StatusModes;
import com.girfa.apps.teamtalk4mobile.api.bitflags.Subscriptions;
import com.girfa.apps.teamtalk4mobile.api.bitflags.UserStates;
import com.girfa.apps.teamtalk4mobile.api.bitflags.UserTypes;
import com.girfa.apps.teamtalk4mobile.api.enumflags.AudioFileFormat;
import com.girfa.apps.teamtalk4mobile.api.enumflags.SoundLevel;

public class User extends ConfigDump {
	private static final String TAG = User.class.getSimpleName();

	private Integer id;
	private String nickname;
	private String username;
	private StatusModes statusModes;
	private String statusMessage;
	private Channel channel;
	private String ipAddress;
	private String udpAddess;
	private String version;
	private Integer packetProtocol;
	private UserTypes userTypes;
	private UserStates userStates;
	private Subscriptions localSubscriptions;
	private Subscriptions peerSubscriptions;
	private Integer userData;
	private String audioFolder;
	private AudioFileFormat aff;
	private String audioFileName;
	private Boolean join;
	private Boolean unread;
	private SoundLevel soundLevel;
	
	private static final String
		USER_ID = "userid",
		NICKNAME = "nickname",
		USERNAME = "username",
		IP_ADDRESS = "ipaddr",
		UDP_ADDRESS = "udpaddr",
		CHANNEL_ID = "channelid",
		STATUS_MODE = "statusmode",
		STATUS_MESSAGE = "statusmsg",
		VERSION = "version",
		PACKET_PROTOCOL = "packetprotocol",
		USER_TYPE = "usertype",
		SUB_LOCAL = "sublocal",
		SUB_PEER = "subpeer",
		USER_DATA = "userdata";
	
	public User() {}
	
	public User(Integer id) {
		setId(id);
	}
	
	public User(Map<String, Object> map) {
		build(map);
	}
	
	public User build(Map<String, Object> map) {
		if (map == null) return this;
		if (map.containsKey(USER_ID)) {
			setId(Integer.valueOf(map.get(USER_ID) + ""));
		}
		if (map.containsKey(NICKNAME)) {
			setNickname(map.get(NICKNAME) + "");
		}
		if (map.containsKey(USERNAME)) {
			setUsername(map.get(USERNAME) + "");
		}
		if (map.containsKey(IP_ADDRESS)) {
			setIpAddress(map.get(IP_ADDRESS) + "");
		}
		if (map.containsKey(UDP_ADDRESS)) {
			setUdpAddess(map.get(UDP_ADDRESS) + "");
		}
		if (map.containsKey(CHANNEL_ID)) {
			setChannel(new Channel(Integer.valueOf(map.get(CHANNEL_ID) + "")));
		}
		if (map.containsKey(STATUS_MODE)) {
			setStatusModes(new StatusModes(Integer.valueOf(map.get(STATUS_MODE) + "")));
		}
		if (map.containsKey(STATUS_MESSAGE)) {
			setStatusMessage(map.get(STATUS_MESSAGE) + "");
		}
		if (map.containsKey(VERSION)) {
			setVersion(map.get(VERSION) + "");
		}
		if (map.containsKey(PACKET_PROTOCOL)) {
			setPacketProtocol(Integer.valueOf(map.get(PACKET_PROTOCOL) + ""));
		}
		if (map.containsKey(USER_TYPE)) {
			setUserTypes(new UserTypes(Integer.valueOf(map.get(USER_TYPE) + "")));
		}
		if (map.containsKey(SUB_LOCAL)) {
			setLocalSubscriptions(new Subscriptions(Integer.valueOf(map.get(SUB_LOCAL) + "")));
		}
		if (map.containsKey(SUB_PEER)) {
			setPeerSubscriptions(new Subscriptions(Integer.valueOf(map.get(SUB_PEER) + "")));
		}
		if (map.containsKey(USER_DATA)) {
			setUserData(Integer.valueOf(map.get(USER_DATA) + ""));
		}
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof User) {
			return getId().equals(((User) o).getId());
		}
		return super.equals(o);
	}
	
	@Override
	public String toString() {
		return getId() + "";
	}
	
	public User setId(Integer id) {
		setInt(TAG + 1, id);
		this.id = id;
		return this;
	}
	
	public User setNickname(String nickname) {
		setString(TAG + 2, nickname);
		this.nickname = nickname;
		return this;
	}
	
	public User setUsername(String username) {
		setString(TAG + 3, username);
		this.username = username;
		return this;
	}
	
	public User setStatusModes(StatusModes statusModes) {
		if (statusModes != null) setInt(TAG + 4, statusModes.getFlags());
		this.statusModes = statusModes;
		return this;
	}
	
	public User setStatusMessage(String statusMessage) {
		setString(TAG + 5, statusMessage);
		this.statusMessage = statusMessage;
		return this;
	}
	
	public User setChannel(Channel channel) {
		if (channel != null) setInt(TAG + 6, channel.getId());
		this.channel = channel;
		return this;
	}
	
	public User setIpAddress(String ipAddress) {
		setString(TAG + 7, ipAddress);
		this.ipAddress = ipAddress;
		return this;
	}
	
	public User setUdpAddess(String udpAddess) {
		setString(TAG + 8, udpAddess);
		this.udpAddess = udpAddess;
		return this;
	}
	
	public User setVersion(String version) {
		setString(TAG + 9, version);
		this.version = version;
		return this;
	}
	
	public User setPacketProtocol(Integer packetProtocol) {
		setInt(TAG + 10, packetProtocol);
		this.packetProtocol = packetProtocol;
		return this;
	}
	
	public User setUserTypes(UserTypes userTypes) {
		if (userTypes != null) setInt(TAG + 11, userTypes.getFlags());
		this.userTypes = userTypes;
		return this;
	}
	
	public User setUserStates(UserStates userStates) {
		if (userStates != null) setInt(TAG + 12, userStates.getFlags());
		this.userStates = userStates;
		return this;
	}
	
	public User setLocalSubscriptions(Subscriptions localSubscriptions) {
		if (localSubscriptions != null) setInt(TAG + 13, localSubscriptions.getFlags());
		this.localSubscriptions = localSubscriptions;
		return this;
	}
	
	public User setPeerSubscriptions(Subscriptions peerSubscriptions) {
		if (peerSubscriptions != null) setInt(TAG + 14, peerSubscriptions.getFlags());
		this.peerSubscriptions = peerSubscriptions;
		return this;
	}
	
	public User setUserData(Integer userData) {
		setInt(TAG + 15, userData);
		this.userData = userData;
		return this;
	}
	
	public User setAudioFolder(String audioFolder) {
		setString(TAG + 16, audioFolder);
		this.audioFolder = audioFolder;
		return this;
	}
	
	public User setAff(AudioFileFormat aff) {
		if (aff != null) setInt(TAG + 17, aff.value);
		this.aff = aff;
		return this;
	}
	
	public User setAudioFileName(String audioFileName) {
		setString(TAG + 18, audioFileName);
		this.audioFileName = audioFileName;
		return this;
	}

	public User setJoin(Boolean join) {
		setBoolean(TAG + 19, join);
		this.join = join;
		return this;
	}
	
	public User setUnread(Boolean unread) {
		setBoolean(TAG + 20, unread);
		this.unread = unread;
		return this;
	}
	
	public User setSoundLevel(SoundLevel soundLevel) {
		this.soundLevel = soundLevel;
		return this;
	}
	
	public Integer getId() {
		if (isDump()) return getInt(TAG + 1, 0);
		return id;
	}

	public String getNickname() {
		if (isDump()) return getString(TAG + 2, null);
		return nickname;
	}

	public String getUsername() {
		if (isDump()) return getString(TAG + 3, null);
		return username;
	}

	public StatusModes getStatusModes() {
		if (isDump()) return new StatusModes(getInt(TAG + 4, 0));
		return statusModes;
	}

	public String getStatusMessage() {
		if (isDump()) return getString(TAG + 5, null);
		return statusMessage;
	}

	public Channel getChannel() {
		if (isDump()) return new Channel(getInt(TAG + 6, 0));
		return channel;
	}

	public String getIpAddress() {
		if (isDump()) return getString(TAG + 7, null);
		return ipAddress;
	}
	
	public String getUdpAddess() {
		if (isDump()) return getString(TAG + 8, null);
		return udpAddess;
	}

	public String getVersion() {
		if (isDump()) return getString(TAG + 9, null);
		return version;
	}

	public Integer getPacketProtocol() {
		if (isDump()) return getInt(TAG + 10, 0);
		return packetProtocol;
	}
	
	public UserTypes getUserTypes() {
		if (isDump()) return new UserTypes(getInt(TAG + 11, 0));
		return userTypes;
	}

	public UserStates getUserStates() {
		if (isDump()) return new UserStates(getInt(TAG + 12, 0));
		return userStates;
	}

	public Subscriptions getLocalSubscriptions() {
		if (isDump()) return new Subscriptions(getInt(TAG + 13, 0));
		return localSubscriptions;
	}

	public Subscriptions getPeerSubscriptions() {
		if (isDump()) return new Subscriptions(getInt(TAG + 14, 0));
		return peerSubscriptions;
	}

	public Integer getUserData() {
		if (isDump()) return getInt(TAG + 15, 0);
		return userData;
	}

	public String getAudioFolder() {
		if (isDump()) return getString(TAG + 16, null);
		return audioFolder;
	}

	public AudioFileFormat getAff() {
		if (isDump()) return AudioFileFormat.valueOf(getString(TAG + 17, null));
		return aff;
	}

	public String getAudioFileName() {
		if (isDump()) return getString(TAG + 18, null);
		return audioFileName;
	}
	
	public Boolean isJoin() {
		if (isDump()) return getBoolean(TAG + 19, false);
		return join;
	}
	
	public Boolean isUnread() {
		if (isDump()) return getBoolean(TAG + 20, false);
		return unread;
	}
	
	public SoundLevel getSoundLevel() {
		return soundLevel;
	}

	@Override
	public void reset() {
		for (int i = 1; i <= 20; i++) {
			remove(TAG + i);
		}
	}
}
