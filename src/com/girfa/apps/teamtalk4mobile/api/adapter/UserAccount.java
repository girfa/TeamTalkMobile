package com.girfa.apps.teamtalk4mobile.api.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.girfa.apps.teamtalk4mobile.api.bitflags.UserTypes;
import com.girfa.apps.teamtalk4mobile.utils.Utils;

import android.text.TextUtils;

public class UserAccount extends ConfigDump {
	private static final String TAG = UserAccount.class.getSimpleName();
	private static final String SPARATOR = "|";
	
	private String username;
	private String password;
	private UserTypes userType;
	private Integer userData;
	private String note;
	private String initChannel;
	private List<Channel> autoOperatorChannels;
	
	private static final String
		USERNAME = "username",
		USER_TYPE = "usertype",
		USER_DATA = "userdata",
		NOTE = "note",
		CHANNEL = "channel",
		OP_CHANNELS = "opchannels";
	
	public UserAccount() {}
	
	public UserAccount(Map<String, Object> map) {
		build(map);
	}
	
	@SuppressWarnings("unchecked")
	public UserAccount build(Map<String, Object> map) {
		if (map == null) return this;
		if (map.containsKey(USERNAME)) {
			setUsername(map.get(USERNAME) + "");
		}
		if (map.containsKey(USER_TYPE)) {
			setUserType(new UserTypes(Integer.valueOf(map.get(USER_TYPE) + "")));
		}
		if (map.containsKey(USER_DATA)) {
			setUserData(Integer.valueOf(map.get(USER_DATA) + ""));
		}
		if (map.containsKey(NOTE)) {
			setNote(map.get(NOTE) + "");
		}
		if (map.containsKey(CHANNEL)) {
			setInitChannel(map.get(CHANNEL) + "");
		}
		if (map.containsKey(OP_CHANNELS)) {
			setAutoOperatorChannels(new ArrayList<Channel>());
			List<Integer> voice = (List<Integer>) map.get(OP_CHANNELS);
			for (Integer i : voice) {
				autoOperatorChannels.add(new Channel(i));
			}
		}
		return this;
	}
	
	public UserAccount setUsername(String username) {
		setString(TAG + 1, username);
		this.username = username;
		return this;
	}
	
	public UserAccount setPassword(String password) {
		setString(TAG + 2, password);
		this.password = password;
		return this;
	}
	
	public UserAccount setUserType(UserTypes userType) {
		if (userType != null) setInt(TAG + 3, userType.getFlags());
		this.userType = userType;
		return this;
	}
	
	public UserAccount setUserData(Integer userData) {
		setInt(TAG + 4, userData);
		this.userData = userData;
		return this;
	}
	
	public UserAccount setNote(String note) {
		setString(TAG + 5, note);
		this.note = note;
		return this;
	}
	
	public UserAccount setInitChannel(String initChannel) {
		setString(TAG + 6, initChannel);
		this.initChannel = initChannel;
		return this;
	}
	
	public UserAccount setAutoOperatorChannels(List<Channel> autoOperatorChannels) {
		if (autoOperatorChannels != null) setString(TAG + 7, TextUtils.join(SPARATOR, autoOperatorChannels));
		this.autoOperatorChannels = autoOperatorChannels;
		return this;
	}

	public String getUsername() {
		if (isDump()) return getString(TAG + 1, null);
		return username;
	}

	public String getPassword() {
		if (isDump()) return getString(TAG + 2, null);
		return password;
	}

	public UserTypes getUserType() {
		if (isDump()) return new UserTypes(getInt(TAG + 3, 0));
		return userType;
	}

	public Integer getUserData() {
		if (isDump()) return getInt(TAG + 4, 0);
		return userData;
	}

	public String getNote() {
		if (isDump()) return getString(TAG + 5, null);
		return note;
	}

	public String getInitChannel() {
		if (isDump()) return getString(TAG + 6, null);
		return initChannel;
	}

	public List<Channel> getAutoOperatorChannels() {
		if (isDump()) {
			String string = getString(TAG + 7, null);
			List<Channel> channels = new ArrayList<Channel>();
			if (!Utils.isEmpty(string)) {
				String[] strA = string.split(Pattern.quote(SPARATOR));
				for (String i : strA) {
					channels.add(new Channel(Integer.valueOf(i)));
				}
			}
			return channels;
		}
		return autoOperatorChannels;
	}

	@Override
	public void reset() {
		for (int i = 1; i <= 7; i++) {
			remove(TAG + i);
		}
	}
}
