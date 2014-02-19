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

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

public class Utils {
	private static final String TAG = Utils.class.getSimpleName();

	@SuppressLint("DefaultLocale")
	public static String readSize(long bytes) {
	    int unit = 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    char pre = "KMGTPE".charAt(exp-1);
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static boolean isEmpty(Object object) {
		if (object == null || object.equals("")) return true;
		return false;
	}

	@SuppressLint("DefaultLocale")
	public static String getMimeType(File file) {
		String ext = Utils.getFileExtension(file);
		if (ext == null) return null;
		MimeTypeMap mime = MimeTypeMap.getSingleton();
		return mime.getMimeTypeFromExtension(ext);
	}

	@SuppressLint("DefaultLocale")
	public static String getFileExtension(File file) {
		if (file == null) return null;
		String name = file.getName();
		String parts[]= name.split("\\.");
	    return parts[parts.length - 1].toLowerCase();
	}

	public static boolean isEmpty(String string) {
		return isEmpty((Object) string);
	}

	public static int minimize(long size, int digit) {
		if (Math.log10(size) < digit) return (int) size;
		return (int) (size / Math.pow(10, Math.round(Math.log10(size)) - digit));
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void setBackground(View view, Drawable background) {
		if (Build.VERSION.SDK_INT < 16) {
			view.setBackgroundDrawable(background);
		} else {
			view.setBackground(background);
		}
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			Log.e(TAG, "sleep." + e.getMessage());
		}
	}

	public static boolean checkConnection(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			return (netInfo != null && netInfo.isConnectedOrConnecting());
		} catch (SecurityException e) {
			return false;
		}
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void clipboard(Context context, String string) {
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE); 
		if (Build.VERSION.SDK_INT < 11) {
			clipboard.setText(string);
		} else {
			ClipData clip = ClipData.newPlainText(TAG, string);
			clipboard.setPrimaryClip(clip);
		}
	}
	
	public static void share(Context context, String message) {
		Intent share = new Intent();
		share.setAction(Intent.ACTION_SEND);
		share.putExtra(Intent.EXTRA_TEXT, message);
		share.setType("text/plain");
		context.startActivity(share);
	}
	
}
