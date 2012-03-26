package com.javaposse.android.zenwriter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class SelectImageListener implements OnClickListener {

	Activity context = null;

	public SelectImageListener(Activity context) {
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		context.startActivityForResult(photoPickerIntent, AndroidZenWriterActivity.SELECT_PHOTO);
	}

}
