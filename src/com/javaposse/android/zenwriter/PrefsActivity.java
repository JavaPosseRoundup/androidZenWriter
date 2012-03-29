package com.javaposse.android.zenwriter;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class PrefsActivity extends PreferenceActivity {

    public static final int SELECT_PHOTO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.zenpreferences);
        Preference backgroundPref = this.findPreference("backgroundpref");
        final PrefsActivity THIS = this;
        backgroundPref
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        return THIS.selectBackgroundImage(preference);
                    }
                });
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        if(prefs.getString("backgroundpref", "").length() > 0) {
            backgroundPref.setSummary(new File(prefs.getString("backgroundpref", "")).getName());
        }
    }

    public boolean selectBackgroundImage(Preference pref) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        this.startActivityForResult(photoPickerIntent,
                AndroidZenWriterActivity.SELECT_PHOTO);
        return true;
    }

    public boolean selectTypingSound(Preference pref) {
        Intent soundPickerIntent = new Intent(Intent.ACTION_PICK);
        soundPickerIntent.setType("audio/*");
        this.startActivityForResult(soundPickerIntent,
                AndroidZenWriterActivity.SELECT_AUDIO);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode,
            Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
        case SELECT_PHOTO:
            if (resultCode == RESULT_OK) {
                Uri selectedImage = imageReturnedIntent.getData();

                Log.i("SelectedImage", selectedImage.toString());
                String path = getPath(selectedImage);
                Log.i("SelectedImage", "File Path: " + path);
                File selectedImageFile = new File(path);
/*
                try {

                    File destFile = getFileStreamPath(selectedImageFile
                            .getName());

                    if (!destFile.exists() && selectedImageFile.exists()) {
                        Log.i("SelectedImage", "Copying file: "
                                + selectedImageFile.getAbsolutePath());

                        Toast.makeText(
                                this,
                                "Copying image: " + selectedImageFile.getName(),
                                Toast.LENGTH_LONG).show();
                        FileChannel src = new FileInputStream(selectedImageFile)
                                .getChannel();
                        FileChannel dst = openFileOutput(
                                selectedImageFile.getName(), MODE_PRIVATE)
                                .getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        Log.i("SelectedImage", "Copy Complete!");
                        Log.i("SelectedImage",
                                "Destination File: "
                                        + destFile.getAbsolutePath());
                    } else {
                        Log.i("SelectedImage",
                                "The file was already in our private storage: "
                                        + destFile.getAbsolutePath());
                    }
                    Toast.makeText(this,
                            "Copied image: " + selectedImageFile.getName(),
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e("SelectedImage", "Failed to Copy Image", e);
                }
                */

                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("backgroundpref", selectedImageFile.getAbsolutePath());
                editor.commit();
                Preference backgroundPref = this
                        .findPreference("backgroundpref");
                backgroundPref.setSummary(selectedImageFile.getName());
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("PrefsActivity", "Paused, setting result.");
        setResult(RESULT_OK);
    }

}
