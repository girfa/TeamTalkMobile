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

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.girfa.apps.teamtalk4mobile.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SpecifyPassword extends SherlockDialogFragment {
	public static final String TAG = SpecifyPassword.class.getSimpleName();
	private EditText input;
	private Runnable callback;
	
	public static SpecifyPassword newInstance() {
		return new SpecifyPassword();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle saved) {
		LayoutInflater factory = LayoutInflater.from(getActivity());
		View view = factory.inflate(R.layout.specify_password, null);
		input = (EditText) view.findViewById(R.id.c_password);
		
		Resources res = getResources();
		List<Drawable> list = new ArrayList<Drawable>();
		list.add(res.getDrawable(R.drawable.ic_channel));
		list.add(res.getDrawable(R.drawable.ic_protected));
		
		return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.c_join)
			.setIcon(new LayerDrawable(list.toArray(new Drawable[list.size()])))
			.setView(view)
			.setPositiveButton(R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					callback.run();
				}
			})
			.setNegativeButton(R.string.cancel, null)
			.create();
	}
	
	public void setListener(Runnable listener) {
		this.callback = listener;
	}
	
	public EditText getInput() {
		return input;
	}
}
