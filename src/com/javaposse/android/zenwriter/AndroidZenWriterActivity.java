package com.javaposse.android.zenwriter;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class AndroidZenWriterActivity extends Activity {

	public static final int SELECT_PHOTO = 100;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ViewPager pager = (ViewPager) findViewById(R.id.ViewPager1);
		pager.setAdapter(new ZenAdapter(this));
		pager.setCurrentItem(1, true);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = imageReturnedIntent.getData();

				Log.i("SelectedImage", selectedImage.toString());
				View top = findViewById(R.id.Top);
				try {
					InputStream imageStream = getContentResolver().openInputStream(selectedImage);
					Drawable newBackground = Drawable.createFromStream(imageStream, "Background Image");
					top.setBackgroundDrawable(newBackground);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}

	public void openThemeSettings(View parent) {
		Intent settingsActivity = new Intent(this, PrefsActivity.class);
		startActivity(settingsActivity);
	}

}
