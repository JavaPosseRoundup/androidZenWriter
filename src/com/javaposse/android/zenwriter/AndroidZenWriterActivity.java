package com.javaposse.android.zenwriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AndroidZenWriterActivity extends SherlockActivity {

	public static String currentFilename = "current.txt";
	public static String settingsFilename = "settings.properties";

	public static final int SELECT_PHOTO = 100;
	private static final boolean ACTIONBAR = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	        if (ACTIONBAR) {
	            setTheme(R.style.Theme_Sherlock_Light);
	        }
		super.onCreate(savedInstanceState);
		//loadSettings();
		setContentView(R.layout.main);
		ViewPager pager = (ViewPager) findViewById(R.id.ViewPager1);
		pager.setAdapter(new ZenAdapter(this));
		pager.setCurrentItem(1, true);
		
        applyPreferences();
        
		
	}

	public static final int EDIT_PREFERENCES = 1;

	public void openThemeSettings(View parent) {
		Intent settingsActivity = new Intent(this, PrefsActivity.class);
		startActivityForResult(settingsActivity, EDIT_PREFERENCES);
	}

	public void shareStuff() {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		
		EditText editText = (EditText) findViewById(R.id.editText1);
		String content = editText.getText().toString();

		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "TODO NEED Subject!!!");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
		
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		saveFile(currentFilename);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//saveSettings();
		saveFile(currentFilename);
	}

	protected void saveFile(String filename) {
		FileOutputStream fos = null;

		EditText editText = (EditText) findViewById(R.id.editText1);
		String content = editText.getText().toString();
		try {
			fos = openFileOutput(filename, MODE_PRIVATE);
			fos.write(content.getBytes());
		} catch (IOException e) {
			Log.e("SaveFile", "Failed to save file: ", e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
		Toast.makeText(this, "Saved file", Toast.LENGTH_LONG).show();
	}

	protected void loadFile(String filename) {
		FileInputStream fis = null;
		BufferedReader br = null;
		File file = getFileStreamPath(filename);
		StringBuilder content = new StringBuilder();
		if (file.exists()) {
			EditText editText = (EditText) findViewById(R.id.editText1);
			try {
				fis = openFileInput(filename);
				br = new BufferedReader(new InputStreamReader(fis));
				while (br.ready()) {
					content.append(br.readLine()).append("\n");
				}
				editText.setText(content.toString());
				editText.setSelection(content.length());

			} catch (IOException e) {
				Log.e("SaveFile", "Failed to save file: ", e);
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
					}
				}
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
					}
				}
			}
		}

	}
	

	protected void applyPreferences() {
	    View top = findViewById(R.id.Top);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String backgroundImage = prefs.getString("backgroundpref", "");
        Log.i("AndroidZenWriter:applyPreferences", "BackgroundImage: " + backgroundImage);
        if(!backgroundImage.equals("")) {
            Drawable background = getDrawable(this, backgroundImage);
            if(background != null) {
                top.setBackgroundDrawable(background);
            }
        }
        // TODO: Apply other preferences
	}
	
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == EDIT_PREFERENCES && resultCode == RESULT_OK) {
            this.applyPreferences();
        }
        
    }
/*
	public void saveSettings() {
		FileOutputStream fos = null;

		try {
			fos = openFileOutput(settingsFilename, MODE_PRIVATE);
			settings.save(fos, "Settings saved at: " + new Date());
		} catch (IOException e) {
			Log.e("SaveFile", "Failed to save file: ", e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
		Toast.makeText(this, "Saved settings", Toast.LENGTH_LONG).show();
	}

	public void loadSettings() {
		FileInputStream fis = null;

		File settingsFile = getFileStreamPath(settingsFilename);
		if (settingsFile.exists()) {
			try {
				fis = openFileInput(settingsFilename);
				settings.load(fis);
			} catch (IOException e) {
				Log.e("SaveFile", "Failed to save file: ", e);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
					}
				}
			}
		}
		Toast.makeText(this, "Loaded settings", Toast.LENGTH_LONG).show();
	}
*/
	public static Drawable getDrawable(Context context, String filename) {

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 2;
		Bitmap backgroundBitmap = BitmapFactory.decodeFile(
				context.getFileStreamPath(filename).getAbsolutePath(), opts);

		if (backgroundBitmap != null) {
			BitmapDrawable drawable = new BitmapDrawable(backgroundBitmap);
			return drawable;

		}
		return null;

	}
	
	public static final int SHARE_CONTEXT_MENU_ITEMID = 100;
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    super.onContextItemSelected(item);
	    
	    if(item.getItemId() == SHARE_CONTEXT_MENU_ITEMID) {
	        shareStuff();
	    }
	    
	    return true;
	}
	
	

    // Action Bar
    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        if (!ACTIONBAR) {
            return false;
        }
	menu.add("Edit").setIcon(android.R.drawable.ic_menu_edit)
		.setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_ALWAYS);

	menu.add("Search").setIcon(android.R.drawable.ic_menu_search)
		.setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_ALWAYS);

	menu.add("Save").setIcon(android.R.drawable.ic_menu_save)
		.setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);

	menu.add("Share").setIcon(android.R.drawable.ic_menu_share)
		.setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
	return true;
    }
}
