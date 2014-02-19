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

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.girfa.apps.teamtalk4mobile.fragment.ServerListFrg;


public class ServerList extends SherlockFragmentActivity {
	@Override
	public void onCreate(Bundle saved) {
		super.onCreate(saved);
		FragmentManager fm = getSupportFragmentManager();
		if (fm.findFragmentByTag(ServerListFrg.TAG) == null) {
			fm.beginTransaction()
				.add(android.R.id.content,
					ServerListFrg.newInstance(),
					ServerListFrg.TAG)
				.commit();
		}
	}
}
