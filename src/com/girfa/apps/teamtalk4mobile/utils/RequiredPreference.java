package com.girfa.apps.teamtalk4mobile.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Button;

public class RequiredPreference extends EditTextPreference implements TextWatcher  {
	public RequiredPreference(Context context) {
		super(context);
	}
	
	public RequiredPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public RequiredPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void showDialog(Bundle state) {
		super.showDialog(state);
		getEditText().removeTextChangedListener(this);
		getEditText().addTextChangedListener(this);
		onEditTextChanged(getText());
	}

	@Override
	public void afterTextChanged(Editable s) {}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		onEditTextChanged(s);
	}
	
	private void onEditTextChanged(CharSequence s) {
		try {
			AlertDialog dialog = (AlertDialog) getDialog();
			Button ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
			if (TextUtils.isEmpty(s)) {
				ok.setEnabled(false);
			} else {
				ok.setEnabled(true);
			}
		} catch (NullPointerException ignore) {}
	}
 	
}
