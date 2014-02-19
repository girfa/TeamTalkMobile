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
package com.girfa.apps.teamtalk4mobile.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.girfa.apps.teamtalk4mobile.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FileChooserOld extends DialogFragment implements OnItemClickListener {
	private static final String TAG = FileChooserOld.class.getSimpleName();
	private Listener mListener;
	private ListView list;
	private Adapter adapter;
	private List<File> files;
	private File mCurrent;
	private String mExtension;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(false);
		File file = Environment.getExternalStorageDirectory();
		if (file == null) file = new File(File.separator);
		setCurrent(file);
		files = new ArrayList<File>(childsOf(mCurrent));
		adapter = new Adapter(getActivity());
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.file_chooser, null);
		
		list = (ListView) view.findViewById(R.id.file_chooser);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
		return new AlertDialog.Builder(getActivity())
			.setTitle(mCurrent.getPath())
			.setIcon(R.drawable.ic_folder)
			.setView(view)
			.setNegativeButton(R.string.cancel, null)
			.create();
	}
	
	@Override
	public void onResume() {
		getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() != KeyEvent.ACTION_UP) return false;
				if (keyCode != KeyEvent.KEYCODE_BACK) return false;
				File parent = mCurrent.getParentFile();
				if (parent == null) {
					dismiss();
				} else {
					files = new ArrayList<File>(childsOf(parent));
					setCurrent(parent);
					adapter.notifyDataSetChanged();
				}
				return true;
			}
		});
		super.onResume();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		File file = files.get(position);
		if (file.isDirectory()) {
			if (file.listFiles() == null) {
				files = new ArrayList<File>();
			} else {
				files = new ArrayList<File>(childsOf(file));
			}
			setCurrent(file);
			adapter.notifyDataSetChanged();
		} else {
			mListener.result(file);
			dismiss();
		}
	}
	
	public void setListener(Listener listener) {
		mListener = listener;
	}
	
	private void setCurrent(File current) {
		mCurrent = current;
		if (getDialog() != null) {
			getDialog().setTitle(current.getPath());
		}
	}
	
	public void setExtension(String extension) {
		mExtension = extension;
	}
	
	public void show(FragmentManager manager) {
		show(manager, TAG);
	}
	
	private List<File> childsOf(File file) {
		if (file == null) return null;
		if (!file.isDirectory()) return null;
		Comparator<File> com = new Comparator<File>() {
			@Override
			public int compare(File lhs, File rhs) {
				return lhs.getName().compareToIgnoreCase(rhs.getName());
			}
		};
		List<File> dirs = Arrays.asList(file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		}));
		Collections.sort(dirs, com);
		List<File> files = Arrays.asList(file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (mExtension != null) {
					return file.isFile() && file.getName().endsWith(mExtension);
				}
				return file.isFile();
			}
		}));
		Collections.sort(files, com);
		List<File> childs = new ArrayList<File>();
		childs.addAll(dirs);
		childs.addAll(files);
		return childs;
	}
	
	private class Adapter extends BaseAdapter {
		private LayoutInflater inflater;

		public Adapter(Context context) {
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
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
			File file = files.get(position);
			if (file.isDirectory()) {
				convertView = inflater.inflate(R.layout.folder_list, null);
				((TextView) convertView.findViewById(R.id.folder_name)).setText(file.getName());
			} else {
				convertView = inflater.inflate(R.layout.file_list, null);
				((TextView) convertView.findViewById(R.id.file_name)).setText(file.getName());
			}
			return convertView;
		}
	}
	
	public interface Listener {
		void result(File file);
	}
}
