package com.javaposse.android.zenwriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class AndroidZenWriterActivity extends SherlockActivity {

    public static String settingsFilename = "settings.properties";
    protected Note currentNote = null;
    protected List<Note> notes = new ArrayList<Note>();
    public static final int SELECT_PHOTO = 100;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light_ForceOverflow);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        loadSettings();
        if(currentNote == null) {
            currentNote = new Note();
            notes.add(currentNote);
        }
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

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, currentNote.name);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //saveSettings();
        //saveFile(currentNote.filename);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveSettings();
        saveFile(currentNote.filename);
    }

    int defaultNameLength = 30;
    
    protected void saveFile(String filename) {
        FileOutputStream fos = null;
        
        

        EditText editText = (EditText) findViewById(R.id.editText1);
        if (editText != null) {
            String content = editText.getText().toString();
            if(currentNote.name.length() == 0) {
                currentNote.name = currentNote.getDefaultNameFromContent(content, defaultNameLength);
            }
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
            Toast.makeText(this, "Saved file", Toast.LENGTH_SHORT).show();
        }
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
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String backgroundImage = prefs.getString("backgroundpref", "");
        Log.i("AndroidZenWriter:applyPreferences", "BackgroundImage: "
                + backgroundImage);
        if (!backgroundImage.equals("")) {
            Drawable background = getDrawable(this, backgroundImage);
            if (background != null) {
                top.setBackgroundDrawable(background);
            }
        }
        // TODO: Apply other preferences
    }

    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == EDIT_PREFERENCES && resultCode == RESULT_OK) {
            this.applyPreferences();
        }

    }

    public void saveSettings() {
        FileOutputStream fos = null;

        Properties settings = new Properties();
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            String prefix = "note." + i + ".";
            settings.setProperty(prefix + "id", note.id);
            settings.setProperty(prefix + "name", note.name);
            settings.setProperty(prefix + "filename", note.filename);
            settings.setProperty(prefix + "lastModified",
                    String.valueOf(note.lastModified.getTime()));
            Log.i("saveSettings", "Saving Note Metadata: " + note);
        }
        settings.setProperty("currentNoteId", currentNote.id);
        Log.i("saveSettings", "currentNoteId: " + currentNote.id);
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
        Toast.makeText(this, "Saved settings", Toast.LENGTH_SHORT).show();
    }

    public void loadSettings() {
        FileInputStream fis = null;

        File settingsFile = getFileStreamPath(settingsFilename);
        if (settingsFile.exists()) {
            Properties settings = new Properties();
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
            String currentNoteId = "";
            if (settings.containsKey("currentNoteId")) {
                currentNoteId = settings.getProperty("currentNoteId");
            }

            for (int i = 0;; i++) {
                String prefix = "note." + i + ".";
                if (settings.containsKey(prefix + "id")) {
                    String id = settings.getProperty(prefix + "id");
                    String name = settings.getProperty(prefix + "name");
                    String filename = settings.getProperty(prefix + "filename");
                    Date lastModified = new Date(Long.valueOf(settings
                            .getProperty(prefix + "lastModified")));
                    Note note = new Note(id, name, filename, null, lastModified);
                    Log.i("loadSettings", "Loaded Note: " + note);
                    notes.add(note);
                    if (note.id.equals(currentNoteId)) {
                        currentNote = note;
                    }

                } else {
                    break;
                }
            }
        }
        Toast.makeText(this, "Loaded settings", Toast.LENGTH_SHORT).show();
    }

    public static Drawable getDrawable(Context context, String filename) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 2;
        Bitmap backgroundBitmap = BitmapFactory.decodeFile(context
                .getFileStreamPath(filename).getAbsolutePath(), opts);

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

        if (item.getItemId() == SHARE_CONTEXT_MENU_ITEMID) {
            shareStuff();
        }

        return true;
    }

    public static final int ACTION_NEW = 1;
    public static final int ACTION_LIST = 2;
    public static final int ACTION_EDIT = 3;
    public static final int ACTION_SEARCH = 4;
    public static final int ACTION_SAVE = 5;
    public static final int ACTION_SHARE = 6;
    public static final int ACTION_DELETE = 7;

    // Action Bar
    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {

        menu.add(com.actionbarsherlock.view.Menu.NONE, ACTION_NEW, ACTION_NEW,
                "New Note")
                .setIcon(android.R.drawable.ic_menu_add)
                .setShowAsAction(
                        com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(com.actionbarsherlock.view.Menu.NONE, ACTION_LIST,
                ACTION_LIST, "List Notes")
                .setIcon(android.R.drawable.ic_menu_agenda)
                .setShowAsAction(
                        com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
        
        menu.add(com.actionbarsherlock.view.Menu.NONE, ACTION_EDIT,
                ACTION_EDIT, "Rename")
                .setIcon(android.R.drawable.ic_menu_edit)
                .setShowAsAction(
                        com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(com.actionbarsherlock.view.Menu.NONE, ACTION_SHARE,
                ACTION_SHARE, "Share")
                .setIcon(android.R.drawable.ic_menu_share)
                .setShowAsAction(
                        com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
        

        menu.add(com.actionbarsherlock.view.Menu.NONE, ACTION_DELETE,
                ACTION_DELETE, "Delete Note")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setShowAsAction(
                        com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);


        menu.add(com.actionbarsherlock.view.Menu.NONE, ACTION_SEARCH,
                ACTION_SEARCH, "Search")
                .setIcon(android.R.drawable.ic_menu_search)
                .setShowAsAction(
                        com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_ALWAYS);
        

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(
            com.actionbarsherlock.view.MenuItem item) {
        boolean ret = super.onOptionsItemSelected(item);
        int itemId = item.getItemId();
        if (itemId == ACTION_SAVE) {
            saveFile(currentNote.filename);
            saveSettings();
            ret = true;
        } else if (itemId == ACTION_SHARE) {
            shareStuff();
            ret = true;
        } else if (itemId == ACTION_SEARCH) {
            // TODO: Implement Search
        } else if (itemId == ACTION_EDIT) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Edit Note");
            alert.setMessage("Enter Note Name:");

            // Set an EditText view to get user input
            final EditText input = new EditText(this);
            input.setText(currentNote.name);
            alert.setView(input);

            alert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int whichButton) {
                            String value = input.getText().toString();
                            currentNote.name = value;
                        }
                    });

            alert.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int whichButton) {
                            // Canceled.
                        }
                    });

            alert.show();
        } else if (itemId == ACTION_NEW) {
            createNote();
        }
        else if (itemId == ACTION_LIST) {
            if(currentNote.name.length() == 0) {
                EditText editor = (EditText) findViewById(R.id.editText1);
                String content = editor.getText().toString();
                currentNote.name = Note.getDefaultNameFromContent(content, defaultNameLength);
            }
            final AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
            listDialog.setTitle("Select Active Note");
            ArrayAdapter<Note> adapter = new ArrayAdapter<Note>(this, android.R.layout.simple_list_item_single_choice, notes);
            listDialog.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int whichButton) {
                            dialog.dismiss();
                            switchToNote((Integer) ((AlertDialog)dialog).getListView().getTag());
                        }
                    });
            listDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int whichButton) {
                            // Canceled.
                        }
                    });
            listDialog.setSingleChoiceItems(adapter, adapter.getPosition(currentNote), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ListView lv = ((AlertDialog)dialog).getListView();
                    lv.setTag(new Integer(which));
                }
            });
            listDialog.show();
            
        }
        else if(itemId == ACTION_DELETE) {
            AlertDialog.Builder confirm = new AlertDialog.Builder(this);

            confirm.setTitle("Delete Note");
            confirm.setMessage("Are you sure you want to delete this note?");


            confirm.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int whichButton) {
                            int index = notes.indexOf(currentNote);
                            notes.remove(index);
                            File fileToDelete = getFileStreamPath(currentNote.filename);
                            if(fileToDelete.exists()) {
                                boolean deletedFile = fileToDelete.delete();
                                Log.i("deleteNote", "Deleted file: " + fileToDelete.getAbsolutePath() + ": " + deletedFile);
                            }
                            if(notes.size() > 0) {
                                switchToNote(Math.max(0,index - 1));
                            }
                            else {
                                createNote();
                            }
                        }
                    });

            confirm.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int whichButton) {
                            // Canceled.
                        }
                    });

            confirm.show();
        }

        return ret;
    }

    private void createNote() {
        currentNote = new Note();
        notes.add(currentNote);
        EditText editor = (EditText) findViewById(R.id.editText1);
        editor.setText("");
    }

    boolean actionBarVisible = false;

    public void toggleTheme() {
        if (!actionBarVisible) {
            this.getSupportActionBar().show();
        } else {
            this.getSupportActionBar().hide();
        }
        actionBarVisible = !actionBarVisible;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            toggleTheme();
        }

        return ret;

    }
    
    private void switchToNote(int position) {
        saveFile(currentNote.filename);
        saveSettings();
        currentNote = notes.get(position);
        loadFile(currentNote.filename);
    }

}
