package com.javaposse.android.zenwriter;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.TextView;

public class ZenAdapter extends PagerAdapter {

    private AndroidZenWriterActivity context;
    private SoundPool soundPool;
    private int soundID;
    boolean soundLoaded = false;

    public ZenAdapter(AndroidZenWriterActivity context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (position) {

        case 1:
            view = inflater.inflate(R.layout.editview, null);
            break;
        case 2:
            view = inflater.inflate(R.layout.thememanager, null);
            break;
        case 0:
        default:
            TextView textView = new TextView(context);
            textView.setText("" + position);
            textView.setTextSize(50);
            textView.setTextColor(Color.RED);
            view = textView;
            break;
        }

        container.addView(view);

        if (position == 1) {
            EditText editor = (EditText) view.findViewById(R.id.editText1);
            if(context.sharedContents != null && context.sharedContents != "")
                context.createNote(context.sharedContents);
            else
                context.loadFile(context.currentNote.getFilename());

            View.OnCreateContextMenuListener editorOnContextMenuCreateListener = new View.OnCreateContextMenuListener() {

                public void onCreateContextMenu(ContextMenu menu, View view,
                        ContextMenuInfo menuInfo) {
                    menu.add(Menu.NONE,
                            AndroidZenWriterActivity.SHARE_CONTEXT_MENU_ITEMID,
                            Menu.NONE, "Share");
                }
            };
            editor.setOnCreateContextMenuListener(editorOnContextMenuCreateListener);
            editor.setOnTouchListener(new OnTouchListener() {
                
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return context.onTouchEvent(event);
                }
            });

            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId,
                        int status) {
                    soundLoaded = true;
                }
            });
            soundID = soundPool.load(context, R.raw.typewriterkey, 1);

            editor.setOnKeyListener(new OnKeyListener() {
                
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction()!=KeyEvent.ACTION_DOWN) {
                        
                        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        float actualVolume = (float) audioManager
                                .getStreamVolume(AudioManager.STREAM_MUSIC);
                        float maxVolume = (float) audioManager
                                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        float volume = actualVolume / maxVolume;
                        // Is the sound loaded already?
                        if (soundLoaded) {
                            soundPool.play(soundID, volume, volume, 1, 0, 1f);
                        }
                        
                        return true;
                    }
                    else
                        return false;
                }
            });
        }

        if (position == 2) {
            // initialize the gallery
            Gallery gallery = (Gallery) context.findViewById(R.id.themegallery);

            gallery.setAdapter(new ImageAdapter(context));

            gallery.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v,
                        int position, long id) {
                    // find the layout object
                    View top = context.findViewById(R.id.Top);

                    int selected = 0;

                    switch (position) {
                    case 0:
                        selected = R.drawable.beach1;
                        break;
                    case 1:
                        selected = R.drawable.kauai;
                        break;
                    case 2:
                        selected = R.drawable.mirror;
                        break;
                    case 3:
                        selected = R.drawable.napali;
                        break;
                    default:
                        selected = R.drawable.stonearch;
                        break;
                    }

                    top.setBackgroundResource(selected);
                }
            });
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

}
