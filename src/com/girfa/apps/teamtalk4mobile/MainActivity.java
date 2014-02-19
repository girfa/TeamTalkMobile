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
package com.girfa.apps.teamtalk4mobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.girfa.apps.teamtalk4mobile.R;
import com.girfa.apps.teamtalk4mobile.api.adapter.ServerProperties;
import com.girfa.apps.teamtalk4mobile.api.enumflags.TextMessageType;
import com.girfa.apps.teamtalk4mobile.db.FileDB;
import com.girfa.apps.teamtalk4mobile.db.MessageDB;
import com.girfa.apps.teamtalk4mobile.fragment.ChannelTab;
import com.girfa.apps.teamtalk4mobile.fragment.ChatDialog;
import com.girfa.apps.teamtalk4mobile.fragment.ChatTab;
import com.girfa.apps.teamtalk4mobile.fragment.FilesTab;
import com.girfa.apps.teamtalk4mobile.service.Connector;
import com.girfa.apps.teamtalk4mobile.service.Recorder;
import com.girfa.apps.teamtalk4mobile.service.Stream;
import com.viewpagerindicator.PageIndicator;

public class MainActivity extends SherlockFragmentActivity implements Handler.Callback {
	public static final String TAG = MainActivity.class.getSimpleName();
	public static final String SERVER_SORT = "server_sort";
	private Bundle data;
	private FragmentAdapter adapter;
	private ViewPager pager;
	private PageIndicator indicator;
	private Connector con;
	private ActionBar ab;
	private ServerProperties sp;
	
	
	private final int[] titles = {
		R.string.channel,
		R.string.chat,
		// R.string.video,
		// R.string.desktops,
		R.string.files
	};
	
	@Override
	public void onCreate(Bundle saved) {
		super.onCreate(saved);

		setContentView(R.layout.main);
		sp = (ServerProperties) new ServerProperties().dump(this);
		con = new Connector(this, this);
		data = getIntent().getExtras();
		adapter = new FragmentAdapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
		indicator = (PageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		ab = getSherlock().getActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.custom_ab);
		setTitle(sp.getServerName());
	}
	
	@Override
	protected void onResume() {
		con.start();
		con.bind();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		con.unbind();
		super.onPause();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			prompt();
	        return true;
	    }
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.s_disconnect :
				prompt();
				break;
			case R.id.m_pref :
				Intent pref = new Intent(this, Preferences.class);
				startActivity(pref);
				break;
			/*
			case R.id.m_record :
				con.send(Stream.REQ_RECORD);
				break;
			*/
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void setTitle(CharSequence title) {
		if (title == null) title = TeamTalk4Mobile.NAME;
		((TextView) ab.getCustomView().findViewById(R.id.custom_ab_title)).setText(title);
	}
	
	class FragmentAdapter extends FragmentPagerAdapter {
		public FragmentAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public int getCount() {
			return titles.length;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return getResources().getString(titles[position]);
		}
		
		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0 :
					return ChannelTab.newInstance(data);
				case 1 :
					return ChatTab.newInstance();
				/*
				case 2 :
					return VideoTab;
				case 3 :
					return Desktops;
				*/
				case 2 :
					return FilesTab.newInstance(data);
			}
			return null;
		}
	}

	private void prompt() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.s_disconnect);
		builder.setMessage(R.string.s_prompt);
		builder.setIcon(R.drawable.ab_disconnect);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				con.send(Stream.REQ_DISCONNECT);
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		builder.create().show();
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
			case Stream.INIT_NOT_CONNECTED :
				con.send(Stream.REQ_CONNECT, data);
				break;
			case Stream.RES_WELCOME :
				setTitle(sp.getServerName());
				con.send(Stream.REQ_LOGIN);
				con.send(Stream.REQ_PING);
				break;
			case Stream.RES_MESSAGE :
				if (TextMessageType.valueOf(data.getInt(MessageDB.COL_TYPE)) 
					!= TextMessageType.USER) break;
				int id = data.getInt(MessageDB.COL_DEST_USER_ID);
				if (getSupportFragmentManager().findFragmentByTag(ChatDialog.TAG + id) == null) {
					ChatDialog.newInstance(data).show(getSupportFragmentManager(), ChatDialog.TAG + id);
				}
				break;
			case Stream.RES_DOWNLOAD_FINISH :
				Toast.makeText(this, 
					getResources().getString(R.string.f_downto)
						.replace("[f]", msg.getData().getString(FileDB.FILE_PATH)),
					Toast.LENGTH_LONG).show();
				break;
			case Stream.RES_DISCONNECTED :
				finish();
				break;
			case Stream.RES_RECORD_STARTED :
				Toast.makeText(this, R.string.m_rec_start, Toast.LENGTH_LONG).show();
				break;
			case Stream.RES_RECORD_STOPPED :
				Toast.makeText(this, 
					getResources().getString(R.string.m_rec_stop)
						.replace("[f]", msg.getData().getString(Recorder.LOCATION)),
					Toast.LENGTH_LONG).show();
				break;
		}
		return false;
	}
}
