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

import java.io.File;
import java.util.List;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.girfa.apps.teamtalk4mobile.R;
import com.girfa.apps.teamtalk4mobile.api.adapter.FileInfo;
import com.girfa.apps.teamtalk4mobile.api.adapter.FileTransfer;
import com.girfa.apps.teamtalk4mobile.api.adapter.User;
import com.girfa.apps.teamtalk4mobile.db.FileDB;
import com.girfa.apps.teamtalk4mobile.service.Connector;
import com.girfa.apps.teamtalk4mobile.service.FileDownload;
import com.girfa.apps.teamtalk4mobile.service.FileUpload;
import com.girfa.apps.teamtalk4mobile.service.Stream;
import com.girfa.apps.teamtalk4mobile.utils.FileChooserOld;
import com.girfa.apps.teamtalk4mobile.utils.Utils;

public class FilesTab extends SherlockListFragment implements Handler.Callback {
	public static final String TAG = FilesTab.class.getSimpleName();
	private Context context;
	private Connector con;
	private FileDB fdb;
	private List<FileInfo> files;
	private ListAdapter adapter;
	private SparseArray<ProgressDialog> dialogs = new SparseArray<ProgressDialog>();
	
	public static FilesTab newInstance(Bundle data) {
		return new FilesTab();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		this.context = getSherlockActivity();
		con = new Connector(context, this);
		fdb = (FileDB) new FileDB(context).openRead();
		adapter = new ListAdapter(context);
		setListAdapter(adapter);
	}
	
	@Override
	public void onResume() {
		con.start();
		con.bind();
		fdb.openRead();
		adapter.notifyDataSetChanged();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		fdb.close();
		con.unbind();
		super.onPause();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.files, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.f_upload :
				FileChooserOld fc = new FileChooserOld();
				fc.setListener(new FileChooserOld.Listener() {
					@Override
					public void result(File file) {
						FileInfo fInfo = new FileInfo()
							.setFileName(file.getName())
							.setChannel(((User) new User().dump(context)).getChannel())
							.setFileSize(file.length())
							.setFileTransfer(new FileTransfer()
								.setLocalFilePath(file.getPath()));
						final int id = fdb.add(fInfo);
						final Bundle data = new Bundle();
						data.putInt(FileDB.COL_ID, id);
						con.send(Stream.REQ_UPLOAD_FILE, data);
					}
				});
				fc.show(getFragmentManager());
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		final FileInfo fInfo = files.get(position);
		if (openFile(fInfo)) return;
		if (dialogs.indexOfKey(fInfo.getId()) >= 0) {
			dialogs.get(fInfo.getId()).show();
		} else {
			final Bundle data = new Bundle();
			data.putInt(FileDB.COL_ID, fInfo.getId());
			con.send(Stream.REQ_DOWNLOAD_FILE, data);
		}
	}
	
	private boolean openFile(FileInfo fInfo) {
		if (fInfo == null) return false;
		File file = new File(FileDownload.SAVE_DIRECTORY + fInfo.getFileName());
		if (!file.exists() || !fInfo.getFileSize().equals(file.length())) return false;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
			Utils.getMimeType(file));
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getActivity(), getResources().getString(R.string.f_noapp), Toast.LENGTH_SHORT).show();
		}
		return true;
	}
	
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		
		public ListAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (files == null) return 0;
			return files.size();
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
			FileInfo file = files.get(position);
			convertView = inflater.inflate(R.layout.files_tab_item, null);
			((TextView) convertView.findViewById(R.id.ft_name)).setText(file.getFileName());
			((TextView) convertView.findViewById(R.id.ft_size)).setText(Utils.readSize(file.getFileSize()));
			((TextView) convertView.findViewById(R.id.ft_owner)).setText(file.getUsername());
			return convertView;
		}
		
		@Override
		public void notifyDataSetChanged() {
			User curUser = new User();
			curUser.dump(context);
			if (curUser != null) {
				files = fdb.gets(curUser.getChannel());
			}
			super.notifyDataSetChanged();
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		final Bundle data = msg.getData();
		int id;
		final int fid;
		switch (msg.what) {
			case Stream.INIT_ALREADY_CONNECTED :
				setListAdapter(adapter);
				break;
			case Stream.RES_ADD_FILE :
			case Stream.RES_REMOVE_FILE :
				adapter.notifyDataSetChanged();
				break;
			case Stream.RES_DOWNLOAD_PROGRESS :
				id = data.getInt(FileDB.COL_ID);
				Download dpp = (Download) dialogs.get(id);
				if (dpp != null) {
					dpp.setProgress(data.getLong(FileDB.TRANSFERRED));
					break;
				}
			case Stream.RES_DOWNLOAD_START :
				id = data.getInt(FileDB.COL_ID);
				fid = id;
				dpp = new Download(getSherlockActivity());
				dpp.setMax(fdb.get(id).getFileSize());
				dpp.setButton(ProgressDialog.BUTTON_NEGATIVE, 
					context.getResources().getString(R.string.cancel), 
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						dialogs.remove(fid);
						con.send(Stream.REQ_DOWNLOAD_CANCEL, data);
					}
				});
				dialogs.put(id, dpp);
				if (msg.what == Stream.RES_DOWNLOAD_START) dpp.show();
				break;
			case Stream.RES_DOWNLOAD_FINISH :
				adapter.notifyDataSetChanged();
				id = data.getInt(FileDB.COL_ID);
				Download dpd = (Download) dialogs.get(id);
				if (dpd != null && dpd.isShowing()) {
					dpd.dismiss();
					openFile(fdb.get(id));
				}
				break;
			case Stream.RES_UPLOAD_PROGRESS :
				id = data.getInt(FileDB.COL_ID);
				Upload upp = (Upload) dialogs.get(id);
				if (upp != null) {
					upp.setProgress(data.getLong(FileDB.TRANSFERRED));
					break;
				}
			case Stream.RES_UPLOAD_START :
				id = data.getInt(FileDB.COL_ID);
				fid = id;
				upp = new Upload(getSherlockActivity());
				upp.setMax(fdb.get(id).getFileSize());
				upp.setButton(ProgressDialog.BUTTON_NEGATIVE, 
					context.getResources().getString(R.string.cancel), 
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						dialogs.remove(fid);
						con.send(Stream.REQ_UPLOAD_CANCEL, data);
					}
				});
				dialogs.put(id, upp);
				if (msg.what == Stream.RES_UPLOAD_START) upp.show();
				break;
			case Stream.RES_UPLOAD_FINISH :
				adapter.notifyDataSetChanged();
				id = data.getInt(FileDB.COL_ID);
				Upload upd = (Upload) dialogs.get(id);
				if (upd != null && upd.isShowing()) {
					upd.dismiss();
				}
				break;
			
		}
		return false;
	}
	
	public class Download extends ProgressDialog {
		private long max;
		
		public Download(Context context) {
			super(context);
			setProgressStyle(STYLE_HORIZONTAL);
			setTitle(R.string.f_download);
			setIcon(R.drawable.ab_download);
		}
		
		public void setProgress(long value) {
			setProgress((int) (value * getMax() / max));
		}
		
		public void setMax(long max) {
			this.max = max;
			setMax(Utils.minimize(max, FileDownload.DIGITS));
		}
	}
	
	public class Upload extends ProgressDialog {
		private long max;
		
		public Upload(Context context) {
			super(context);
			setProgressStyle(STYLE_HORIZONTAL);
			setTitle(R.string.f_upload);
			setIcon(R.drawable.ab_upload);
		}
		
		public void setProgress(long value) {
			setProgress((int) (value * getMax() / max));
		}
		
		public void setMax(long max) {
			this.max = max;
			setMax(Utils.minimize(max, FileUpload.DIGITS));
		}
	}
}
