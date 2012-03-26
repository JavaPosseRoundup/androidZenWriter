package com.javaposse.android.zenwriter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ZenAdapter extends PagerAdapter {

  private Activity context;
  
  
  public ZenAdapter(Activity context) {
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
    LayoutInflater inflater = 
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    switch (position) {

    case 1:
      view = inflater.inflate(R.layout.editview, null);
      break;
    case 0:
        view = inflater.inflate(R.layout.thememanager, null);
        Button btn = (Button) view.findViewById(R.id.SelectBackgroundButton);
        btn.setOnClickListener(new SelectImageListener(context));
        break;
    case 2:
    default:
      TextView textView = new TextView(context);
      textView.setText("" + position);
      textView.setTextSize(50);
      textView.setTextColor(Color.RED);
      view = textView;
      break;
    }

    container.addView(view);

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