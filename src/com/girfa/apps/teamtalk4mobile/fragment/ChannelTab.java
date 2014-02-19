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
package com.girfa.apps.teamtalk4mobile.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.girfa.apps.teamtalk4mobile.R;
import com.girfa.apps.teamtalk4mobile.api.adapter.Channel;
import com.girfa.apps.teamtalk4mobile.api.adapter.Server;
import com.girfa.apps.teamtalk4mobile.api.adapter.User;
import com.girfa.apps.teamtalk4mobile.api.bitflags.StatusModes;
import com.girfa.apps.teamtalk4mobile.api.bitflags.UserTypes;
import com.girfa.apps.teamtalk4mobile.api.enumflags.SoundLevel;
import com.girfa.apps.teamtalk4mobile.api.enumflags.TextMessageType;
import com.girfa.apps.teamtalk4mobile.db.ChannelDB;
import com.girfa.apps.teamtalk4mobile.db.MessageDB;
import com.girfa.apps.teamtalk4mobile.db.ServerDB;
import com.girfa.apps.teamtalk4mobile.db.UserDB;
import com.girfa.apps.teamtalk4mobile.service.Connector;
import com.girfa.apps.teamtalk4mobile.service.Stream;
import com.girfa.apps.teamtalk4mobile.utils.Config;
import com.girfa.apps.teamtalk4mobile.utils.Utils;

@SuppressLint("ValidFragment")
public class ChannelTab extends SherlockListFragment implements Handler.Callback {
	public static final String TAG = ChannelTab.class.getSimpleName();
	
	private Context context;
	private Connector con;
	private ChannelDB cdb;
	private UserDB udb;
	private volatile SparseArray<Object> cous;
	private SparseArray<Channel> channels;
	private SparseArray<User> users;
	private volatile ListAdapter adapter;
	private User curUser;
	private Channel curChannel;
	private ActionBar ab;
	
	public static ChannelTab newInstance(Bundle data) {
		ChannelTab ct = new ChannelTab();
		ct.setArguments(data);
		return ct;
	}
	
	@Override
	public void onCreate(Bundle saved) {
		super.onCreate(saved);
		setHasOptionsMenu(true);
		context = getSherlockActivity();
		new Config(context);
		curUser = new User();
		curUser.dump(context);
		curChannel = curUser.getChannel();
		con = new Connector(context, this);
		cdb = new ChannelDB(context);
		udb = new UserDB(context);
		cous = new SparseArray<Object>();
		channels = new SparseArray<Channel>();
		users = new SparseArray<User>();
		adapter = new ListAdapter(context);
		ab = getSherlockActivity().getSupportActionBar();
	}
	
	@Override
	public void onResume() {
		con.start();
		con.bind();
		cdb.openRead();
		udb.openRead();
		if (curChannel != null) {
			setSubtitle(curChannel.getURI());
			Channel channel = cdb.get(curChannel);
			if (channel != null) {
				setTotalUsers(channel.getTotalUsers());
			}
		}
		adapter.reset();
		adapter.notifyDataSetChanged();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		udb.close();
		cdb.close();
		con.unbind();
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		con.unbind();
		super.onDestroy();
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem miU = menu.findItem(R.id.c_up);
		boolean enabled = false;
		try {
			enabled = cdb.get(curChannel).getParent().getId() > 0;
		} catch (Exception ignore) {}
		miU.getIcon().setAlpha(enabled ? 255 : 63);
		miU.setEnabled(enabled);
		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.channel, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.c_up :
				adapter.joinChannel(cdb.get(curChannel.getParent()));
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Object cou = cous.valueAt(position);
		if (cou instanceof Channel) {
			adapter.joinChannel((Channel) cou);
		} else if (cou instanceof User) {
			User user = (User) cou;
			if (!user.equals(curUser)) {
				Bundle data = new Bundle();
				data.putInt(MessageDB.COL_DEST_USER_ID, user.getId());
				if (getFragmentManager().findFragmentByTag(ChatDialog.TAG + user.getId()) == null) {
					ChatDialog.newInstance(data).show(getFragmentManager(), ChatDialog.TAG + user.getId());
				}
			}
		}
	}
	
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		
		public ListAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return cous.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Object cou = cous.valueAt(position);
			convertView = inflater.inflate(R.layout.channel_item, null);
			ImageView icon = (ImageView) convertView.findViewById(R.id.cou_icon);
			TextView name = (TextView) convertView.findViewById(R.id.cou_name);
			Resources res = getResources();
			List<Drawable> list = new ArrayList<Drawable>();
			if (cou instanceof Channel) {
				Channel channel = (Channel) cou;
				name.setText(channel.getName() + " (" + channel.getTotalUsers() + ")");
				list.add(res.getDrawable(R.drawable.ic_channel));
				if (channel.isProtected()) {
					list.add(res.getDrawable(R.drawable.ic_protected));
				}
			} else if (cou instanceof User) {
				User user = (User) cou;
				String msg = user.getStatusMessage();
				if (Utils.isEmpty(msg)) {
					name.setText(user.getNickname());
				} else {
					name.setText(user.getNickname() + " - " + msg);
				}
				StatusModes modes = user.getStatusModes();
				if (curUser.equals(user)) {
					name.setTypeface(null, Typeface.BOLD);
				}
				if (modes.is(StatusModes.STREAM_VIDEO)) {
					list.add(res.getDrawable(R.drawable.ic_str_video));
				}
				if (modes.is(StatusModes.STREAM_DESKTOP)) {
					list.add(res.getDrawable(R.drawable.ic_str_desktop));
				}
				if (modes.is(StatusModes.GENDER_FEMALE)) {
					list.add(res.getDrawable(R.drawable.ic_user_female));
				} else if (modes.is(StatusModes.GENDER_MALE)) {
					list.add(res.getDrawable(R.drawable.ic_user_male));
				}
				if (modes.is(StatusModes.STREAM_AUDIOFILE)) {
					list.add(res.getDrawable(R.drawable.ic_str_audiofile));
				}
				if (modes.is(StatusModes.AWAY)) {
					list.add(res.getDrawable(R.drawable.ic_away));
				} else if (modes.is(StatusModes.QUESTION)) {
					list.add(res.getDrawable(R.drawable.ic_question));
				}
				if (user.isUnread()) {
					list.add(res.getDrawable(R.drawable.ic_message_alert));
				}
				if (user.getUserTypes().is(UserTypes.ADMIN)) {
					list.add(res.getDrawable(R.drawable.ic_administrator));
				} else if (curChannel != null && curChannel.isOperator(user)) {
					list.add(res.getDrawable(R.drawable.ic_operator));
				}
				if (user.getSoundLevel() != null) {
					if (user.getSoundLevel().equals(SoundLevel.GAIN_DEFAULT)) {
						Utils.setBackground(convertView, res.getDrawable(R.drawable.user_gain_default));
					} else if (user.getSoundLevel().equals(SoundLevel.GAIN_MAX)) {
						Utils.setBackground(convertView, res.getDrawable(R.drawable.user_gain_max));
					}
				}
			}
			icon.setImageDrawable(new LayerDrawable(list.toArray(new Drawable[list.size()])));
			return convertView;
		}
		
		private void reset() {
			cous.clear();
			if (channels != null) {
				for (int i = 0; i < channels.size(); i++) {
					cous.append(-channels.keyAt(i), channels.valueAt(i));
				}
			}
			if (users != null) {
				for (int i = 0; i < users.size(); i++) {
					cous.append(users.keyAt(i), users.valueAt(i));
				}
			}
		}
		
		private void reloadChannels() {
			curChannel = cdb.get(curUser.getChannel());
			channels = cdb.gets(curChannel);
		}
		
		private void reloadUsers() {
			curChannel = cdb.get(curUser.getChannel());
			users = udb.gets(curChannel);
		}
		
		private void joinChannel(Channel channel) {
			final Bundle data = new Bundle();
			data.putString(ChannelDB.COL_URI, channel.getURI());
			data.putBoolean(ChannelDB.COL_PROTECTED, channel.isProtected());
			if (channel.isProtected() && Utils.isEmpty(channel.getPassword())) {
				final SpecifyPassword sp = SpecifyPassword.newInstance();
				sp.setListener(new Runnable() {
					@Override
					public void run() {
						data.putString(ChannelDB.COL_PASSWORD, sp.getInput().getText().toString());
						con.send(Stream.REQ_JOIN_CHANNEL, data);
					}
				});
				sp.show(getFragmentManager(), SpecifyPassword.TAG);
			} else {
				data.putString(ChannelDB.COL_PASSWORD, channel.getPassword());
				con.send(Stream.REQ_JOIN_CHANNEL, data);
			}
		}
	}
	
	private void setSubtitle(String title) {
		if (Utils.isEmpty(title)) title = "/";
		((TextView) ab.getCustomView()
			.findViewById(R.id.custom_ab_subtitle))
			.setText(title);
	}
	
	private void setTotalUsers(Integer total) {
		if (Utils.isEmpty(total)) return;
		((TextView) ab.getCustomView()
			.findViewById(R.id.custom_ab_count))
			.setText(total.toString());
	}

	@Override
	public boolean handleMessage(final Message msg) {
		final Bundle data = msg.getData();
		int id;
		switch (msg.what) {
			case Stream.INIT_ALREADY_CONNECTED :
				adapter.reloadChannels();
				adapter.reloadUsers();
				setListAdapter(adapter);
				adapter.reset();
				adapter.notifyDataSetChanged();
				getSherlockActivity().supportInvalidateOptionsMenu();
				break;
			case Stream.RES_WELCOME :
				setListAdapter(adapter);
				break;
			case Stream.RES_ADD_CHANNEL :
			case Stream.RES_REMOVE_CHANNEL :
				adapter.reloadChannels();
				adapter.reset();
				adapter.notifyDataSetChanged();
				break;
			case Stream.RES_UPDATE_CHANNEL :
				if (data == null) {
					adapter.reloadUsers();
					adapter.reset();
				} else {
					id = data.getInt(ChannelDB.COL_ID);
					if (cous.get(-id) != null) cous.put(-id, cdb.get(id));
				}
				Channel channel = cdb.get(curChannel);
				if (channel != null) {
					setTotalUsers(channel.getTotalUsers());
				}
				adapter.notifyDataSetChanged();
				break;
			case Stream.RES_ADD_USER :
			case Stream.RES_REMOVE_USER :
				adapter.reloadUsers();
				adapter.reset();
				adapter.notifyDataSetChanged();
				break;
			case Stream.RES_LOADED :
				ServerDB sdb = (ServerDB) new ServerDB(context).openRead();
				Server server = sdb.get(getArguments().getInt(ServerDB.COL_ID, 0));
				sdb.close();
				adapter.joinChannel(
					new Channel()
						.setURI(server.getJoinChannel())
						.setProtected(!Utils.isEmpty(server.getJoinPassword()))
						.setPassword(server.getJoinPassword()));
				getSherlockActivity().supportInvalidateOptionsMenu();
				break;
			case Stream.RES_JOINED :
				if (curChannel != null) {
					setSubtitle(curChannel.getURI());
					setTotalUsers(cdb.get(curChannel).getTotalUsers());
				}
				adapter.reloadChannels();
				adapter.reloadUsers();
				adapter.reset();
				adapter.notifyDataSetChanged();
				getSherlockActivity().supportInvalidateOptionsMenu();
				break;
			case Stream.RES_MESSAGE :
				if (TextMessageType.valueOf(data.getInt(MessageDB.COL_TYPE)) 
					!= TextMessageType.USER) break;
			case Stream.RES_MESSAGE_READ :
			case Stream.RES_UPDATE_USER :
				id = data.getInt(UserDB.COL_ID);
				cous.put(id, udb.get(id));
				adapter.notifyDataSetChanged();
				break;
			case Stream.RX_AUDIO :
				if (cous == null) break;
				id = data.getInt(UserDB.COL_ID);
				User user = null;
				try {
					user = (User) cous.get(id);
				} catch (ClassCastException ignore) {}
				if (user == null) break;
				if (user.getSoundLevel() == null) {
					cous.put(id, user.setSoundLevel(SoundLevel.idOf(data.getInt(UserDB.SOUND_LEVEL))));
				} else {
					cous.put(id, user.setSoundLevel(SoundLevel.idOf(data.getInt(UserDB.SOUND_LEVEL))));
					adapter.notifyDataSetChanged();
					if (!user.getSoundLevel().equals(SoundLevel.GAIN_MIN)) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								Utils.sleep(1000);
								data.putInt(UserDB.SOUND_LEVEL, SoundLevel.GAIN_MIN.id);
								con.sendBack(Stream.RX_AUDIO, data);
							}
						}).start();
					}
				}
				break;
		}
		return false;
	}
}
