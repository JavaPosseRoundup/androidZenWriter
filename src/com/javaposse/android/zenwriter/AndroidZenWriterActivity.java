package com.javaposse.android.zenwriter;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;

import dalvik.system.VMRuntime;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
				String path = getPath(selectedImage);
				Log.i("SelectedImage", "File Path: " + path);
			    File selectedImageFile = new File(path);
				
				try {

				    File destFile = getFileStreamPath(selectedImageFile.getName());
				    
			        if (!destFile.exists() && selectedImageFile.exists()) {
			            Log.i("SelectedImage", "Copying file: " + selectedImageFile.getAbsolutePath());
			        	FileChannel src = new FileInputStream(selectedImageFile).getChannel();
			            FileChannel dst = openFileOutput(selectedImageFile.getName(), MODE_PRIVATE).getChannel();
			            dst.transferFrom(src, 0, src.size());
			            src.close();
			            dst.close();
			            Log.i("SelectedImage", "Copy Complete!");
			            Log.i("SelectedImage", "Destination File: " + destFile.getAbsolutePath());
			        }
			        else {
			        	Log.i("SelectedImage", "The file was already in our private storage: " + destFile.getAbsolutePath());
			        }
			        Toast.makeText(this, "Copied image: " + selectedImageFile.getName(), Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Log.e("SelectedImage", "Failed to Copy Image", e);
				}
				
				BitmapFactory.Options opts = new BitmapFactory.Options();
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);
				Log.i("SelectedImage", "DensityDPI: " + metrics.densityDpi);
				Log.i("SelectedImage", "Density: " + metrics.density);
				opts.inTargetDensity = metrics.densityDpi;
				opts.inSampleSize = 2;
				Bitmap backgroundBitmap = BitmapFactory.decodeFile(selectedImageFile.getAbsolutePath(), opts);
				
				if(backgroundBitmap != null) {
					BitmapDrawable drawable = new BitmapDrawable(backgroundBitmap);
					top.setBackgroundDrawable(drawable);
				}
				//Drawable d = Drawable.createFromPath(selectedImageFile.getAbsolutePath());
				//top.setBackgroundDrawable(d);

				
			}
		}
	}
	
	public String getPath(Uri uri) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    startManagingCursor(cursor);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}


	public void openThemeSettings(View parent) {
		Intent settingsActivity = new Intent(this, PrefsActivity.class);
		startActivity(settingsActivity);
	}

}
