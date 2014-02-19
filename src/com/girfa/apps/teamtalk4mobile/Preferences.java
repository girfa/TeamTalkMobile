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

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.girfa.apps.teamtalk4mobile.fragment.PreferencesFrg;

public class Preferences extends SherlockActivity {
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT < 11) {
			Intent old = new Intent(this, PreferencesOld.class);
			old.putExtras(getIntent());
			startActivity(old);
			finish();
		} else {
			FragmentManager fm = getFragmentManager();
			Fragment sff = fm.findFragmentByTag(PreferencesFrg.TAG);
			if (sff == null) {
				fm.beginTransaction()
					.add(android.R.id.content, 
						PreferencesFrg.newInstance(),
						PreferencesFrg.TAG)
					.commit();
			} else {
				fm.beginTransaction()
					.remove(sff)
					.add(android.R.id.content, 
						PreferencesFrg.newInstance(),
						PreferencesFrg.TAG)
					.commit();
			}
		}
	}
}
