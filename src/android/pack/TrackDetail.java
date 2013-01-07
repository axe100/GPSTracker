/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package android.pack;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Alex
 */
public class TrackDetail extends MapActivity {

    private MapView map;
    private MyOverlay overlay;
    private TrackDBOpenHelper mySQLiteAdapter;
    SQLiteDatabase db;
    String LOG_LABEL = "INFOOOOOOO";
    String groupBy = null;
    String having = null;
    String order = null;
    String trackName;
    String trackDate;
    String trackStart;
    String trackEnd;
    String trackLenght;
    String[] id_array;
    int lng, lat;
    double lenght = 0;
    double distance;
    GeoPoint startGP;
    GeoPoint startGP1;
    double currentSpeed;
    double knots;
    

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.trackdetail);

        Long myid = getIntent().getLongExtra("myid", 6);

        String idString = String.valueOf(myid);

        id_array = new String[]{idString};

        TextView nameview = (TextView) findViewById(R.id.name);
        TextView dateview = (TextView) findViewById(R.id.date);
        TextView startview = (TextView) findViewById(R.id.start);
        TextView endview = (TextView) findViewById(R.id.end);
        TextView lenghtview = (TextView) findViewById(R.id.lenght);
        TextView speedview = (TextView) findViewById(R.id.speed);



        Typeface font = Typeface.createFromAsset(getAssets(), "square.ttf");

        Button delete = (Button) findViewById(R.id.delete);
        delete.setBackgroundResource(R.drawable.delete_button);
        delete.setText("Delete Track");
        delete.setTypeface(font);
        delete.setTextSize(20);
        delete.setTextColor(Color.parseColor("#FFFFFF"));

        delete.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                String where = TrackDBOpenHelper.ID + "= ?";

                db = mySQLiteAdapter.getWritableDatabase();
                db.delete(TrackDBOpenHelper.DATABASE_TABLE_TRACKS, where, id_array);


                finish();

            }
        });

        mySQLiteAdapter = new TrackDBOpenHelper(this.getBaseContext(),
                TrackDBOpenHelper.DATABASE_NAME, null,
                TrackDBOpenHelper.DATABASE_VERSION);

        String[] result_columns = new String[]{
            TrackDBOpenHelper.NAME,
            TrackDBOpenHelper.USER_ID,
            TrackDBOpenHelper.START,
            TrackDBOpenHelper.END,
            TrackDBOpenHelper.DATE};

        db = mySQLiteAdapter.getReadableDatabase();


        String where = TrackDBOpenHelper.ID + "=?";

        Cursor c = db.query(TrackDBOpenHelper.DATABASE_TABLE_TRACKS,
                result_columns, where,
                id_array, groupBy, having, order);

        c.moveToFirst();


        trackName = c.getString(c.getColumnIndex(TrackDBOpenHelper.NAME));
        trackDate = c.getString(c.getColumnIndex(TrackDBOpenHelper.DATE));
        trackStart = c.getString(c.getColumnIndex(TrackDBOpenHelper.START));
        trackEnd = c.getString(c.getColumnIndex(TrackDBOpenHelper.END));

        nameview.setText(trackName);
        dateview.setText(trackDate);
        startview.setText(trackStart);
        endview.setText(trackEnd);



        //Item Abfrage

        String[] item_result_columns = new String[]{
            TrackDBOpenHelper.LONG,
            TrackDBOpenHelper.LAT,
            TrackDBOpenHelper.TIMESTAMP};

        String itemwhere = TrackDBOpenHelper.ID_TRACK + "=?";

        Cursor item_c = db.query(TrackDBOpenHelper.DATABASE_TABLE_ITEMS,
                item_result_columns, itemwhere,
                id_array, groupBy, having, order);

        item_c.moveToFirst();

        String string_item_long = item_c.getString(item_c.getColumnIndex(TrackDBOpenHelper.LONG));
        String string_item_lat = item_c.getString(item_c.getColumnIndex(TrackDBOpenHelper.LAT));

        double p_item_lat = Double.parseDouble(string_item_lat);
        double p_item_long = Double.parseDouble(string_item_long);


        lat = (int) (p_item_long * 1E6);
        lng = (int) (p_item_lat * 1E6);

        map = (MapView) findViewById(R.id.map);
        map.setTraffic(false);
        map.setSatellite(true);

        List<Overlay> overlays = map.getOverlays();

        overlay = new MyOverlay();

        overlays.add(overlay);

        MapController mc = map.getController();

        GeoPoint p;

        p = new GeoPoint(lng, lat);
        mc.animateTo(p);
        mc.setZoom(18);

        map.postInvalidate();

        Cursor item_c2 = db.query(TrackDBOpenHelper.DATABASE_TABLE_ITEMS,
                item_result_columns, itemwhere,
                id_array, groupBy, having, order);

        item_c2.moveToFirst();

        List<Double> long_array = new ArrayList<Double>();
        List<Double> lat_array = new ArrayList<Double>();
        List<Double> speeds = new ArrayList<Double>();

        for (int i = 0; i < item_c2.getCount() - 1; i++) {

            String item_long = item_c2.getString(item_c2.getColumnIndex(TrackDBOpenHelper.LONG));
            String item_lat = item_c2.getString(item_c2.getColumnIndex(TrackDBOpenHelper.LAT));
            String item_stamp = item_c2.getString(item_c2.getColumnIndex(TrackDBOpenHelper.TIMESTAMP));

            Double ov_item_lat = Double.parseDouble(item_lat);
            Double ov_item_long = Double.parseDouble(item_long);
            int timestamp = Integer.parseInt(item_stamp);

            long_array.add(ov_item_long);
            lat_array.add(ov_item_lat);
            //ov_td_lats.add(ov_item_lat);
            item_c2.moveToNext();

            String item_long2 = item_c2.getString(item_c2.getColumnIndex(TrackDBOpenHelper.LONG));
            String item_lat2 = item_c2.getString(item_c2.getColumnIndex(TrackDBOpenHelper.LAT));
            String item_stamp2 = item_c2.getString(item_c2.getColumnIndex(TrackDBOpenHelper.TIMESTAMP));

            Double ov_item_lat2 = Double.parseDouble(item_lat2);
            Double ov_item_long2 = Double.parseDouble(item_long2);
            int timestamp2 = Integer.parseInt(item_stamp2);
            

            startGP = new GeoPoint(
                    (int) (ov_item_lat * 1E6),
                    (int) (ov_item_long * 1E6));
            startGP1 = new GeoPoint(
                    (int) (ov_item_lat2 * 1E6),
                    (int) (ov_item_long2 * 1E6));

            distance = CalculationByDistance(startGP, startGP1);

            currentSpeed = (distance*1000 / (timestamp2 - timestamp))*3.6;

            speeds.add(currentSpeed);
            
            lenght = lenght + distance;
        }
        
        DecimalFormat f = new DecimalFormat("##########.##");
        DecimalFormat f2 = new DecimalFormat("#####.#");

        String lenghtString = String.valueOf(f.format(lenght * 1000));


        lenghtview.setText(lenghtString + " m");

        overlay.setOv_lats(lat_array);
        overlay.setOv_longs(long_array);
    
        double in = Collections.max(speeds);
        
        knots = (in/1.852);

        speedview.setText(f2.format(in)+" km/h"+"\n"+f2.format(knots)+" Knots");

    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public double CalculationByDistance(GeoPoint StartP, GeoPoint EndP) {

        double lat1 = StartP.getLatitudeE6() / 1E6;

        double lat2 = EndP.getLatitudeE6() / 1E6;

        double lon1 = StartP.getLongitudeE6() / 1E6;

        double lon2 = EndP.getLongitudeE6() / 1E6;

        double dLat = Math.toRadians(lat2 - lat1);

        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return c * 6378.1;

    }
}
