package com.javaposse.android.zenwriter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

public class AndroidZenWriterActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ViewPager pager = (ViewPager) findViewById(R.id.ViewPager1);
        pager.setAdapter(new ZenAdapter(this));
        pager.setCurrentItem(1, true);
    }
}