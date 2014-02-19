/*******************************************************************************
 * Copyright (C) 2013 - 2014, Girfa eSuite
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Author : Afrig Aminuddin <my@girfa.com>
 ******************************************************************************/
package com.girfa.apps.teamtalk4mobile.db;

import com.girfa.apps.teamtalk4mobile.R;
import com.girfa.apps.teamtalk4mobile.api.adapter.User;
import com.girfa.apps.teamtalk4mobile.api.bitflags.StatusModes;
import com.girfa.apps.teamtalk4mobile.api.bitflags.Subscriptions;
import com.girfa.apps.teamtalk4mobile.service.Connector;
import com.girfa.apps.teamtalk4mobile.service.Stream;
import com.girfa.apps.teamtalk4mobile.utils.Config;
import com.girfa.apps.teamtalk4mobile.utils.MultiSelectListPreference;
import com.girfa.apps.teamtalk4mobile.utils.Utils;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;

public class PrefChanges extends Config {
	public static final String TAG = PrefChanges.class.getSimpleName();
	public static final int MAX_FLAGS = 0x07F;
	public static final String 
		NICKNAME		= "nickname",
		GENDER			= "gender",
		MESSAGE			= "message",
		MODE			= "mode",
		SUBSCRIPTION	= "subscription",
		PTT				= "ptt",
		ACTIVATION		= "activation";
	
	private Resources res;
	private Connector con;
	
	public PrefChanges(Context context) {
		super(context);
		res = context.getResources();
	}
	
	public void connect(Connector connector) {
		con = connector;
	}
	
	public void sync(User user) {
		user.setNickname(getString(NICKNAME, null));
		int gender = Integer.valueOf(getString(GENDER, res.getString(R.string.p_g_gender_def)));
		int mode = Integer.valueOf(getString(MODE, res.getString(R.string.p_g_mode_def)));
		user.setStatusModes(new StatusModes(gender | mode));
		user.setStatusMessage(getString(MESSAGE, res.getString(R.string.p_g_message_def)));
		int flags = 0x40;
		String[] cs;
		String subs = getString(SUBSCRIPTION, null);
		if (subs == null) {
			cs = res.getStringArray(R.array.p_g_subs_def);
		} else {
			cs = MultiSelectListPreference.unpack(subs);
		}
		for (String i : cs) {
			flags |= Integer.valueOf((String) i);
		}
		user.setLocalSubscriptions(new Subscriptions(flags));
	}
	
	public void updateSummary(Preference pref, String key) {
		Bundle data = new Bundle();
		StatusModes modes = null;
		if (key.equals(GENDER) || key.equals(MESSAGE) || key.equals(MODE)) {
			int gender = Integer.valueOf(getString(GENDER, res.getString(R.string.p_g_gender_def)));
			int mode = Integer.valueOf(getString(MODE, res.getString(R.string.p_g_mode_def)));
			modes = new StatusModes(gender | mode);
			if (con != null) {
				data.clear();
				data.putInt(UserDB.COL_STATUS_MODE, modes.getFlags());
				data.putString(UserDB.COL_STATUS_MESSAGE, getString(MESSAGE, null));
				con.send(Stream.REQ_CHANGE_STATUS, data);
			}
		}
		int num;
		if (key.equals(NICKNAME)) {
			pref.setSummary(getString(NICKNAME, res.getString(R.string.p_g_nick_desc)));
			if (con != null) {
				data.clear();
				data.putString(UserDB.COL_NICKNAME, getString(NICKNAME, null));
				con.send(Stream.REQ_CHANGE_NICKNAME, data);
			}
		} else if (key.equals(GENDER)) {
			if (modes.is(StatusModes.GENDER_FEMALE)) {
				num = 1;
			} else {
				num = 0;
			}
			pref.setSummary(res.getStringArray(R.array.p_g_gender_lang)[num]);
		} else if (key.equals(MESSAGE)) {
			pref.setSummary(getString(MESSAGE, res.getString(R.string.notset)));
		} else if (key.equals(MODE)) {
			if (modes.is(StatusModes.AWAY)) {
				num = 1;
			} else if (modes.is(StatusModes.QUESTION)) {
				num = 2;
			} else {
				num = 0;
			}
			pref.setSummary(res.getStringArray(R.array.p_g_mode_lang)[num]);
		} else if (key.equals(SUBSCRIPTION)) {
			if (Utils.isEmpty(pref.getSummary())) {
				pref.setSummary(R.string.p_g_subs_none);
			}
		} else if (key.equals(ACTIVATION)) {
			if (Utils.isEmpty(pref.getSummary())) {
				pref.setSummary(R.string.p_m_act_none);
			}
		}
	}
}
