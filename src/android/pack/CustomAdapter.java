/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package android.pack;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 *
 * @author Alex
 */
public class CustomAdapter extends SimpleCursorAdapter {

  private int layout;

  public CustomAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
    super(context, layout, c, from, to);
    this.layout = layout;
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {

    Cursor c = getCursor();

    final LayoutInflater inflater = LayoutInflater.from(context);
    View v = inflater.inflate(layout, parent, false);
    return constructViewContent(context, c, v);
  }

  @Override
  public void bindView(View v, Context context, Cursor c) {
    constructViewContent(context, c, v);
  }
  


  private View constructViewContent(Context context,Cursor c, View v) {
     
     Animation rotate_l = AnimationUtils.loadAnimation( context,R.anim.rotate_center_1);
      
     String name = c.getString(c.getColumnIndex(TrackDBOpenHelper.NAME));
     String date = c.getString(c.getColumnIndex(TrackDBOpenHelper.DATE));
     String synced = c.getString(c.getColumnIndex(TrackDBOpenHelper.SYNCED));
     
     TextView nameView = (TextView) v.findViewById(R.id.text);
     TextView dateView = (TextView) v.findViewById(R.id.date);
     ImageView loadImage = (ImageView) v.findViewById(R.id.load);
     
     nameView.setText(name);
     dateView.setText(date);
     
     if((synced.equals("2"))){
        loadImage.startAnimation(rotate_l);   
     }
     
     loadImage.setVisibility(View.INVISIBLE);
       
    return v;  

  }
}
