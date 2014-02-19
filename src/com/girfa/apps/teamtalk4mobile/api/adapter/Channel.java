package com.girfa.apps.teamtalk4mobile.api.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.girfa.apps.teamtalk4mobile.api.bitflags.ChannelTypes;

public class Channel {
	private Channel parent;
	private Integer id;
	private String uri;
	private String name;
	private String topic;
	private String password;
	private Boolean isProtected;
	private ChannelTypes channelTypes;
	private Long diskQuota;
	private String opPassword;
	private Integer maxUsers;
	private Integer totalUsers;
	private AudioCodec audioCodec;
	private AudioConfig audioConfig;
	private List<User> operators;
	private List<User> voiceUsers;
	private List<User> videoUsers;
	private List<User> desktopUsers;

	private static final String
		CHANNEL_ID = "channelid",
		CHANNEL = "channel",
		TOPIC = "topic",
		PASSWORD = "password",
		PROTECTED = "protected",
		TYPE = "type",
		DISK_QUOTA = "diskquota",
		OP_PASSWORD = "oppassword",
		MAX_USERS = "maxusers",
		AUDIO_CODEC = "audiocodec",
		AUDIO_CONFIG = "audiocfg",
		OPERATORS = "operators",
		VOICE_USERS = "voiceusers",
		VIDEO_USERS = "videousers",
		DESKTOP_USERS = "desktopusers";
	
	public Channel() {}
	
	public Channel(Integer id) {
		setId(id);
	}
	
	public Channel(Map<String, Object> map) {
		build(map);
	}
	
	@SuppressWarnings("unchecked")
	public Channel build(Map<String, Object> map) {
		if (map == null) return this;
		if (map.containsKey(CHANNEL_ID)) {
			setId(Integer.valueOf(map.get(CHANNEL_ID) + ""));
		}
		if (map.containsKey(CHANNEL)) {
			setURI(map.get(CHANNEL) + "");
		}
		if (map.containsKey(TOPIC)) {
			setTopic(map.get(TOPIC) + "");
		}
		if (map.containsKey(PASSWORD)) {
			setPassword(map.get(PASSWORD) + "");
		}
		if (map.containsKey(PROTECTED)) {
			setProtected(Integer.valueOf(map.get(PROTECTED) + "") != 0);
		}
		if (map.containsKey(TYPE)) {
			setChannelTypes(new ChannelTypes(Integer.valueOf(map.get(TYPE) + "")));
		}
		if (map.containsKey(DISK_QUOTA)) {
			setDiskQuota(Long.valueOf(map.get(DISK_QUOTA) + ""));
		}
		if (map.containsKey(OP_PASSWORD)) {
			setOpPassword(map.get(OP_PASSWORD) + "");
		}
		if (map.containsKey(MAX_USERS)) {
			setMaxUsers(Integer.valueOf(map.get(MAX_USERS) + ""));
		}
		if (map.containsKey(AUDIO_CODEC)) {
			setAudioCodec(new AudioCodec((List<Integer>) map.get(AUDIO_CODEC)));
		}
		if (map.containsKey(AUDIO_CONFIG)) {
			setAudioConfig(new AudioConfig((List<Integer>) map.get(AUDIO_CONFIG)));
		}
		if (map.containsKey(OPERATORS)) {
			setOperators(new ArrayList<User>());
			List<Integer> opers = (List<Integer>) map.get(OPERATORS);
			for (Integer i : opers) {
				operators.add(new User(i));
			}
		}
		if (map.containsKey(VOICE_USERS)) {
			setVoiceUsers(new ArrayList<User>());
			List<Integer> voice = (List<Integer>) map.get(VOICE_USERS);
			for (Integer i : voice) {
				voiceUsers.add(new User(i));
			}
		}
		if (map.containsKey(VIDEO_USERS)) {
			setVideoUsers(new ArrayList<User>());
			List<Integer> video = (List<Integer>) map.get(VIDEO_USERS);
			for (Integer i : video) {
				videoUsers.add(new User(i));
			}
		}
		if (map.containsKey(DESKTOP_USERS)) {
			setDesktopUsers(new ArrayList<User>());
			List<Integer> desktop = (List<Integer>) map.get(DESKTOP_USERS);
			for (Integer i : desktop) {
				desktopUsers.add(new User(i));
			}
		}
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Channel) return this.getId().equals(((Channel) o).getId());
		return super.equals(o);
	}
	
	public Channel setParent(Channel parent) {
		this.parent = parent;
		return this;
	}
	
	public Channel setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Channel setURI(String uri) {
		this.uri = uri;
		return this;
	}
	
	public Channel setName(String name) {
		this.name = name;
		return this;
	}
	
	public Channel setTopic(String topic) {
		this.topic = topic;
		return this;
	}
	
	public Channel setPassword(String password) {
		this.password = password;
		return this;
	}
	
	public Channel setProtected(Boolean isProtected) {
		this.isProtected = isProtected;
		return this;
	}
	
	public Channel setChannelTypes(ChannelTypes channelTypes) {
		this.channelTypes = channelTypes;
		return this;
	}
	
	public Channel setDiskQuota(Long diskQuota) {
		this.diskQuota = diskQuota;
		return this;
	}
	
	public Channel setOpPassword(String opPassword) {
		this.opPassword = opPassword;
		return this;
	}
	
	public Channel setMaxUsers(Integer maxUsers) {
		this.maxUsers = maxUsers;
		return this;
	}
	
	public Channel setTotalUsers(Integer totalUsers) {
		this.totalUsers = totalUsers;
		return this;
	}
	
	public Channel setAudioCodec(AudioCodec audioCodec) {
		this.audioCodec = audioCodec;
		return this;
	}
	
	public Channel setAudioConfig(AudioConfig audioConfig) {
		this.audioConfig = audioConfig;
		return this;
	}
	
	public Channel setOperators(List<User> operators) {
		this.operators = operators;
		return this;
	}
	
	public Channel setVoiceUsers(List<User> voiceUsers) {
		this.voiceUsers = voiceUsers;
		return this;
	}
	
	public Channel setVideoUsers(List<User> videoUsers) {
		this.videoUsers = videoUsers;
		return this;
	}
	
	public Channel setDesktopUsers(List<User> desktopUsers) {
		this.desktopUsers = desktopUsers;
		return this;
	}

	public Channel getParent() {
		return parent;
	}

	public Integer getId() {
		return id;
	}

	public String getURI() {
		return uri;
	}
	
	public String getName() {
		return name;
	}

	public String getTopic() {
		return topic;
	}

	public String getPassword() {
		return password;
	}

	public Boolean isProtected() {
		return isProtected;
	}

	public ChannelTypes getChannelTypes() {
		return channelTypes;
	}

	public Long getDiskQuota() {
		return diskQuota;
	}

	public String getOpPassword() {
		return opPassword;
	}

	public Integer getMaxUsers() {
		return maxUsers;
	}
	
	public Integer getTotalUsers() {
		return totalUsers;
	}

	public AudioCodec getAudioCodec() {
		return audioCodec;
	}

	public AudioConfig getAudioConfig() {
		return audioConfig;
	}

	public List<User> getOperators() {
		return operators;
	}
	
	public Boolean isOperator(User user) {
		try {
			return getOperators().contains(user);
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public List<User> getVoiceUsers() {
		return voiceUsers;
	}
	
	public Boolean isVoiceUser(User user) {
		try {
			return getVoiceUsers().contains(user);
		} catch (NullPointerException e) {
			return false;
		}
	}

	public List<User> getVideoUsers() {
		return videoUsers;
	}
	
	public Boolean isVideoUser(User user) {
		try {
			return getVideoUsers().contains(user);
		} catch (NullPointerException e) {
			return false;
		}
	}

	public List<User> getDesktopUsers() {
		return desktopUsers;
	}
	
	public Boolean isDesktopUser(User user) {
		try {
			return getDesktopUsers().contains(user);
		} catch (NullPointerException e) {
			return false;
		}
	}
}
