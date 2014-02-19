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

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.girfa.apps.teamtalk4mobile.R;
import com.girfa.apps.teamtalk4mobile.api.adapter.TextMessage;
import com.girfa.apps.teamtalk4mobile.api.adapter.User;
import com.girfa.apps.teamtalk4mobile.api.enumflags.TextMessageType;
import com.girfa.apps.teamtalk4mobile.db.MessageDB;
import com.girfa.apps.teamtalk4mobile.db.UserDB;
import com.girfa.apps.teamtalk4mobile.service.Connector;
import com.girfa.apps.teamtalk4mobile.service.Stream;
import com.girfa.apps.teamtalk4mobile.utils.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ChatDialog extends SherlockDialogFragment implements Handler.Callback {
	public static final String TAG = ChatDialog.class.getSimpleName();
	
	private Connector con;
	private MessageDB mdb;
	private User dest;
	private View view;
	private ListView list;
	private EditText input;
	private ListAdapter adapter;
	private List<TextMessage> messages;

	public static ChatDialog newInstance(Bundle data) {
		ChatDialog cd = new ChatDialog();
		cd.setArguments(data);
		return cd;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		Context context = getSherlockActivity();
		con = new Connector(context, this);
		mdb = (MessageDB) new MessageDB(context).openRead();
		dest = new User(getArguments().getInt(MessageDB.COL_DEST_USER_ID));
		messages = mdb.gets(dest);
		adapter = new ListAdapter(context);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		view = getActivity().getLayoutInflater().inflate(R.layout.chat_dialog, null);
		return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.messages)
			.setIcon(R.drawable.ic_message)
			.setView(view)
			.setPositiveButton(R.string.send, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			})
			.setNegativeButton(R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					readMessage();
				}
			})
			.create();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		list = (ListView) view.findViewById(R.id.cd_list);
		input = ((EditText) view.findViewById(R.id.cd_input));
		list.setAdapter(adapter);
		list.setClickable(false);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		AlertDialog dialog = (AlertDialog) getDialog();
		Button send = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		send.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String content = input.getText().toString();
					if (!Utils.isEmpty(content)) {
						Bundle data = new Bundle();
						data.putInt(MessageDB.COL_TYPE, TextMessageType.USER.value);
						data.putInt(MessageDB.COL_DEST_USER_ID, dest.getId());
						data.putString(MessageDB.COL_CONTENT, content);
						con.send(Stream.REQ_MESSAGE, data);
						User curUser = new User();
						curUser.dump(getSherlockActivity());
						TextMessage msg = new TextMessage()
							.setTextMessageType(TextMessageType.USER)
							.setSrcUser(curUser)
							.setDestUser(dest)
							.setContent(content);
						mdb.add(msg);
						input.setText("");
						adapter.notifyDataSetChanged();
						
					}
					readMessage();
				}
			});
	}
	
	@Override
	public void onResume() {
		con.bind();
		mdb.openRead();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		mdb.close();
		con.unbind();
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		mdb.close();
		super.onDestroy();
	}

	private void readMessage() {
		Bundle data = new Bundle();
		data.putInt(UserDB.COL_ID, dest.getId());
		con.send(Stream.REQ_READ_MESSAGE, data);
	}
	
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		
		public ListAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			if (messages == null) return 0;
			return messages.size();
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
			convertView = inflater.inflate(R.layout.chat_dialog_item, null);
			TextView nickname = (TextView) convertView.findViewById(R.id.cd_nickname);
			TextView message = (TextView) convertView.findViewById(R.id.cd_content);
			nickname.setText(messages.get(position).getSrcUser().getNickname());
			message.setText(messages.get(position).getContent());
			if (dest.equals(messages.get(position).getSrcUser())) {
				nickname.setGravity(Gravity.RIGHT);
				message.setGravity(Gravity.RIGHT);
			} else {
				nickname.setGravity(Gravity.LEFT);
				message.setGravity(Gravity.LEFT);
			}
			return convertView;
		}
		
		@Override
		public void notifyDataSetChanged() {
			messages = mdb.gets(dest);
			super.notifyDataSetChanged();
		}
		
		@Override
		public boolean isEnabled(int position) {
			return false;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		Bundle data = msg.getData();
		switch (msg.what) {
			case Stream.RES_MESSAGE :
				if (TextMessageType.valueOf(data.getInt(MessageDB.COL_TYPE)) 
					!= TextMessageType.USER) break;
				adapter.notifyDataSetChanged();
				break;
		}
		return false;
	}
}
