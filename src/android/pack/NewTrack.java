/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package android.pack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 *
 * @author Alex
 */
public class NewTrack extends Activity {

    String LOG_LABEL = "LOOOOOOOOOOOOG";
    Button tracking;
    Chronometer mChronometer;
    private TextView tempTextView; //Temporary TextView
    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime;
    private final int REFRESH_RATE = 1000;
    private String hours, minutes, seconds, milliseconds;
    private long secs, mins, hrs, msecs;
    private boolean stopped = false;
    private TrackDBOpenHelper mySQLiteAdapter;
    Context context;
    Time today;
    private int year;
    private int month;
    private int day;
    private String starttime;
    private String stoptime;
    SQLiteDatabase db;
    private String where = null;
    private String whereArgs[] = null;
    private String groupBy = null;
    private String having = null;
    private String order = null;
    AlertDialog.Builder alertb;
    AlertDialog alert;
    EditText input;
    String nameOfTrack;
    public int tableid = 0;
    LocationManager lm;
    double currentLong;
    double currentLat;
    List<Double> lats;
    List<Double> longs;
    List<Long> timestamps;
    String[] id_array;
    long init, now, time;
    private LocationListener listener = new LocationListener() {
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Log.i(LOG_LABEL, provider + " status changed");
        }

        public void onProviderEnabled(String provider) {
            // Log.i(LOG_LABEL, provider + " enabled");
        }

        public void onProviderDisabled(String provider) {
            // Log.i(LOG_LABEL, provider + " disabled");
        }

        public void onLocationChanged(Location location) {

            long timestamp = System.currentTimeMillis() / 1000;

            Double changed_lat = location.getLatitude();
            Double changed_long = location.getLongitude();

            lats.add(changed_lat);
            longs.add(changed_long);
            timestamps.add(timestamp);

            Log.i(LOG_LABEL, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            Log.i(LOG_LABEL, "" + lats.get(0));
            Log.i(LOG_LABEL, "" + longs.get(0));
            Log.i(LOG_LABEL, "" + timestamp);

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newtrack);

        lats = new ArrayList<Double>();
        longs = new ArrayList<Double>();
        timestamps = new ArrayList<Long>();

        alertb = new AlertDialog.Builder(NewTrack.this);
        input = new EditText(this);

        alertb.setTitle("New Track");
        alertb.setMessage("Tab in New Track Name");

        alertb.setView(input);

        alertb.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                
                dialog.cancel();
                alert.dismiss();
                
            }
        });

        alertb.setNegativeButton("Save Track", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                nameOfTrack = input.getText().toString();

                tracking.setText("Start Tracking");

                today = new Time(Time.getCurrentTimezone());
                today.setToNow();

                year = today.year;
                month = today.month;
                day = today.monthDay;

                StringBuilder my_sp = new StringBuilder();

                my_sp.append(day).append(".").append(month+1).append(".").append(year);

                String my_date = my_sp.toString();

                stoptime = (today.format("%k:%M"));

                ContentValues newValues = new ContentValues();
                newValues.put(TrackDBOpenHelper.NAME, nameOfTrack);
                newValues.put(TrackDBOpenHelper.USER_ID, "laser_100");
                newValues.put(TrackDBOpenHelper.START, starttime);
                newValues.put(TrackDBOpenHelper.END, stoptime);
                newValues.put(TrackDBOpenHelper.DATE, my_date);


                db.insert(TrackDBOpenHelper.DATABASE_TABLE_TRACKS, null, newValues);

                //ID Abfrage

                String[] id_result_column = new String[]{
                    TrackDBOpenHelper.ID,};

                db = mySQLiteAdapter.getReadableDatabase();

                String where = TrackDBOpenHelper.USER_ID + "=?";

                id_array = new String[]{"laser_100"};

                Cursor c = db.query(TrackDBOpenHelper.DATABASE_TABLE_TRACKS,
                        id_result_column, where,
                        id_array, groupBy, having, order);

                c.moveToLast();

                int currentTrackId = c.getInt(c.getColumnIndex(TrackDBOpenHelper.ID));

                for (int i = 0; i < lats.size(); i++) {

                    Double lat = lats.get(i);
                    Double lon = longs.get(i);
                    Long timestamp = timestamps.get(i);

                    Log.i(LOG_LABEL, "" + lats.get(0));
                    Log.i(LOG_LABEL, "" + longs.get(0));
                    Log.i(LOG_LABEL, "" + timestamps.get(0));

                    ContentValues newGeoValues = new ContentValues();
                    newGeoValues.put(TrackDBOpenHelper.ID_TRACK, currentTrackId);
                    newGeoValues.put(TrackDBOpenHelper.LONG, lon);
                    newGeoValues.put(TrackDBOpenHelper.LAT, lat);
                    newGeoValues.put(TrackDBOpenHelper.TIMESTAMP, timestamp);

                    db.insert(TrackDBOpenHelper.DATABASE_TABLE_ITEMS, null, newGeoValues);
                }

                db.close();

                lats.clear();
                longs.clear();
                timestamps.clear();
                
                dialog.cancel();
                
                alert.dismiss();
            }

           
        });
        
        alert = alertb.create();

        mySQLiteAdapter = new TrackDBOpenHelper(this.getBaseContext(),
                TrackDBOpenHelper.DATABASE_NAME, null,
                TrackDBOpenHelper.DATABASE_VERSION);

        db = mySQLiteAdapter.getWritableDatabase();


        final ImageView myimage = (ImageView) findViewById(R.id.skizze);
        myimage.setBackgroundResource(R.drawable.img_row_green);

        Typeface font = Typeface.createFromAsset(getAssets(), "square.ttf");
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.setTypeface(font);
        mChronometer.setText("00:00:00");
        mChronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
            public void onChronometerTick(Chronometer cArg) {

               formatChronometerText(mChronometer);
                //cArg.setText(DateFormat.format("kk:mm:ss", "S"));

                /*SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
                 timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                 now = System.currentTimeMillis();
                 time = now - init;
                 String s2 = timeFormat.format(time);
                 cArg.setText(s2);*/
            }
        });

        tracking = (Button) findViewById(R.id.tracking);
        tracking.setBackgroundResource(R.drawable.tracking_button);
        tracking.setText("Start Tracking");
        tracking.setTypeface(font);
        tracking.setTextSize(30);
        tracking.setTextColor(Color.parseColor("#FFFFFF"));

        tracking.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                AnimationDrawable frameanim = (AnimationDrawable) myimage.getBackground();

                if (frameanim.isRunning()) {
                    frameanim.stop();
                    mChronometer.stop();
                    //mChronometer.setBase(SystemClock.elapsedRealtime());
                    mChronometer.setText("00:00:00");
                    alert.show();


                } else {

                    lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, listener);

                    frameanim.start();
                    tracking.setText("Stop Tracking");
                    mChronometer.setBase(SystemClock.elapsedRealtime());
                    //init = System.currentTimeMillis();
                    //mChronometer.setBase(init);
                    formatChronometerText(mChronometer);
                    mChronometer.start();
                    today = new Time(Time.getCurrentTimezone());
                    today.setToNow();

                    starttime = (today.format("%k:%M:%S"));
                    
                    

                }
            }
            
            
            
        });
    }
       public void formatChronometerText(Chronometer c) {
    int cTextSize = c.getText().length();
    if (cTextSize == 5) {
        mChronometer.setFormat("00:%s");
    } else if (cTextSize == 7) {
        mChronometer.setFormat("0%s");
    }
    
    
       }
    }

