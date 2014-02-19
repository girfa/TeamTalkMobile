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

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.girfa.apps.teamtalk4mobile.R;
import com.girfa.apps.teamtalk4mobile.api.adapter.Channel;
import com.girfa.apps.teamtalk4mobile.api.adapter.ServerProperties;
import com.girfa.apps.teamtalk4mobile.api.adapter.TextMessage;
import com.girfa.apps.teamtalk4mobile.api.adapter.User;
import com.girfa.apps.teamtalk4mobile.api.enumflags.TextMessageType;
import com.girfa.apps.teamtalk4mobile.db.HistoryDB;
import com.girfa.apps.teamtalk4mobile.db.MessageDB;
import com.girfa.apps.teamtalk4mobile.service.Connector;
import com.girfa.apps.teamtalk4mobile.service.Response;
import com.girfa.apps.teamtalk4mobile.service.Stream;
import com.girfa.apps.teamtalk4mobile.utils.Utils;

public class ChatTab extends SherlockFragment implements Handler.Callback {
	public static final String TAG = ChatTab.class.getSimpleName();
	private Context context;
	private Connector con;
	private HistoryDB hdb;
	private ListView list;
	private List<Map<Response, Object>> logs;
	private ListAdapter adapter;
	private boolean set = false;
	
	
	public static ChatTab newInstance() {
		return new ChatTab();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getSherlockActivity();
		con = new Connector(context, this);
		hdb = (HistoryDB) new HistoryDB(context).openRead();
		adapter = new ListAdapter(context);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.chat_tab, container, false);
		list = (ListView) view.findViewById(R.id.ct_list);
		Button send = (Button) view.findViewById(R.id.ct_send);
		final EditText input = (EditText) view.findViewById(R.id.ct_input);
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = input.getText().toString();
				if (!Utils.isEmpty(content)) {
					Bundle data = new Bundle();
					data.putInt(MessageDB.COL_TYPE, TextMessageType.CHANNEL.value);
					data.putString(MessageDB.COL_CONTENT, content);
					con.send(Stream.REQ_MESSAGE, data);
					input.setText("");
				}
				
			}
		});
		return view;
	}
	
	@Override
	public void onResume() {
		con.start();
		con.bind();
		hdb.openRead();
		adapter.notifyDataSetChanged();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		hdb.close();
		con.unbind();
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		hdb.close();
		super.onDestroy();
	}
	
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		
		public ListAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (logs == null) return 0;
			return logs.size();
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
			String msg = null;
			int strId = 0;
			Map<Response, Object> map = logs.get(position);
			Response res = map.keySet().iterator().next();
			try {
				switch (res) {
					case serverupdate :
						ServerProperties sp = (ServerProperties) map.get(res);
						convertView = inflater.inflate(R.layout.chat_tab_item_server, null);
						((TextView) convertView.findViewById(R.id.ct_s_name))
							.setText(getResources().getString(R.string.s_ct_name)
								.replace("[n]", sp.getServerName()));
						((TextView) convertView.findViewById(R.id.ct_s_motd))
							.setText(getResources().getString(R.string.s_ct_motd)
								.replace("[m]", sp.getMOTD()));
						break;
					case adduser :
						strId = R.string.c_user_joined;
						User us = (User) map.get(res);
						Channel ch = us.getChannel();
						if (ch != null) {
							convertView = inflater.inflate(R.layout.chat_tab_item_channel, null);
							((TextView) convertView.findViewById(R.id.ct_c_channel))
								.setText(getResources().getString(R.string.c_ct_name)
									.replace("[c]", ch.getURI()));
							((TextView) convertView.findViewById(R.id.ct_c_topic))
								.setText(getResources().getString(R.string.c_ct_topic)
									.replace("[t]", ch.getTopic()));
							((TextView) convertView.findViewById(R.id.ct_c_quota))
								.setText(getResources().getString(R.string.c_ct_quota)
									.replace("[q]", Utils.readSize(ch.getDiskQuota() * 1024)));
							break;
						}
					case removeuser :
						if (res == Response.removeuser) {
							strId = R.string.c_user_left;
						}
						String nick = ((User) map.get(res)).getNickname();
						msg = getResources().getString(strId)
							.replace("[u]", !Utils.isEmpty(nick) ? nick :
								getResources().getString(R.string.noname));
					case joined :
						if (res == Response.joined) {
							strId = R.string.c_me_joined;
						}
					case left :
						if (res == Response.left) {
							strId = R.string.c_me_left;
						}
						if (msg == null) {
							msg = getResources().getString(strId)
								.replace("[c]", ((Channel) map.get(res)).getURI());
						}
						convertView = inflater.inflate(R.layout.chat_tab_item_log, null); 
						((TextView) convertView.findViewById(R.id.ct_log)).setText(msg);
						break;
					case messagedeliver :
						TextMessage tm = (TextMessage) map.get(res);
						msg = tm.getSrcUser().getNickname();
						if (tm.getTextMessageType() == TextMessageType.BROADCAST) {
							msg += " » " + getResources().getString(R.string.broadcast);
						}
						msg += " » " + tm.getContent();
						convertView = inflater.inflate(R.layout.chat_tab_item, null); 
						((TextView) convertView.findViewById(R.id.ct_message))
							.setText(msg);
						break;
					default:
						break;
				}
			} catch (NullPointerException e) {
				Log.e(TAG, "getView." + e.getMessage());
			}
			return convertView;
		}
		
		@Override
		public void notifyDataSetChanged() {
			if (set) logs = hdb.getChats();
			super.notifyDataSetChanged();
		}
		
		@Override
		public boolean isEnabled(int position) {
			return false;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
			case Stream.INIT_ALREADY_CONNECTED :
				list.setAdapter(adapter);
				set = true;
			case Stream.RES_HISTORY :
				if (!set) {
					list.setAdapter(adapter);
					set = true;
				}
				adapter.notifyDataSetChanged();
				break;
		}
		return false;
	}

}
