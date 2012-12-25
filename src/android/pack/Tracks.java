/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package android.pack;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tracks extends Activity {

    private TrackDBOpenHelper mySQLiteAdapter;
    SQLiteDatabase db;
    SimpleCursorAdapter cursorAdapter;
    ListView listContent;
    Intent detail;
    String LOG_LABEL = "TRACKSLOG";
    String[] sync_array;
    String id;
    String trackName;
    String userid;
    String trackDate;
    String trackStart;
    String trackEnd;
    String lat;
    String lng;
    String stamp;
    
    JSONObject obj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracks);


        detail = new Intent(this, TrackDetail.class);

        mySQLiteAdapter = new TrackDBOpenHelper(this.getBaseContext(),
                TrackDBOpenHelper.DATABASE_NAME, null,
                TrackDBOpenHelper.DATABASE_VERSION);

        listContent = (ListView) findViewById(R.id.tracks);

        Cursor cursor = mySQLiteAdapter.fetchAllProducts();
        startManagingCursor(cursor);

        String[] from = new String[]{TrackDBOpenHelper.NAME, TrackDBOpenHelper.DATE};
        int[] to = new int[]{R.id.text, R.id.date};

        cursorAdapter = new CustomAdapter(this, R.layout.listitem, cursor, from, to);

        listContent.setAdapter(cursorAdapter);

        listContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                long posit = id;

                Log.v(LOG_LABEL, id + "Blubbbbbbbbbb");

                detail.putExtra("myid", posit);

                //detail.putExtra("position", posit);
                startActivity(detail);

            }
        });


        if (this.isOnline() == true) {

            
        String[] result_columns = new String[]{
            TrackDBOpenHelper.ID,
            TrackDBOpenHelper.NAME,
            TrackDBOpenHelper.USER_ID,
            TrackDBOpenHelper.DATE,
            TrackDBOpenHelper.START,
            TrackDBOpenHelper.END,
            TrackDBOpenHelper.SYNCED};

        String[] item_result_columns = new String[]{
            TrackDBOpenHelper.LAT,
            TrackDBOpenHelper.LONG,
            TrackDBOpenHelper.TIMESTAMP};

        db = mySQLiteAdapter.getWritableDatabase();

        String where = TrackDBOpenHelper.SYNCED + "=?";
        String itemwhere = TrackDBOpenHelper.ID_TRACK + "=?";
        String updatewhere = TrackDBOpenHelper.ID + "=?";

        sync_array = new String[]{"0"};

        Cursor c = db.query(TrackDBOpenHelper.DATABASE_TABLE_TRACKS,
                result_columns, where,
                sync_array, null, null, null);

        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {
            
            Toast toast = Toast.makeText(Tracks.this, "Syncronize Tracks", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 50, 50);
            toast.show();

          obj = new JSONObject();


            id = c.getString(c.getColumnIndex(TrackDBOpenHelper.ID));
            trackName = c.getString(c.getColumnIndex(TrackDBOpenHelper.NAME));
            userid = c.getString(c.getColumnIndex(TrackDBOpenHelper.USER_ID));
            trackDate = c.getString(c.getColumnIndex(TrackDBOpenHelper.DATE));
            trackStart = c.getString(c.getColumnIndex(TrackDBOpenHelper.START));
            trackEnd = c.getString(c.getColumnIndex(TrackDBOpenHelper.END));

            try {

                obj.put("id", id);
                obj.put("name", trackName);
                obj.put("userid", userid);
                obj.put("date", trackDate);
                obj.put("starttime", trackStart);
                obj.put("endtime", trackEnd);

            } catch (JSONException ex) {
                Logger.getLogger(Tracks.class.getName()).log(Level.SEVERE, null, ex);
            }


            String[] id_track_array = {id};

            Cursor item_c2 = db.query(TrackDBOpenHelper.DATABASE_TABLE_ITEMS,
                    item_result_columns, itemwhere,
                    id_track_array, null, null, null);

            item_c2.moveToFirst();

            for (int j = 0; j < item_c2.getCount(); j++) {

                StringBuilder sb = new StringBuilder();
                sb.append(j);

                String item_id = sb.toString();

                lat = item_c2.getString(item_c2.getColumnIndex(TrackDBOpenHelper.LAT));
                lng = item_c2.getString(item_c2.getColumnIndex(TrackDBOpenHelper.LONG));
                stamp = item_c2.getString(item_c2.getColumnIndex(TrackDBOpenHelper.TIMESTAMP));

                JSONArray item = new JSONArray();
                item.put(lat);
                item.put(lng);
                item.put(stamp);

                try {
                    obj.put(item_id, item);

                    item_c2.moveToNext();

                } catch (JSONException ex) {
                    Logger.getLogger(Tracks.class.getName()).log(Level.SEVERE, null, ex);

                }
            }
            ContentValues newValues = new ContentValues();

            newValues.put(TrackDBOpenHelper.SYNCED, "2");

            db.update(TrackDBOpenHelper.DATABASE_TABLE_TRACKS, newValues, updatewhere, id_track_array);



            c.moveToNext();

            Toast toast2 = Toast.makeText(Tracks.this, obj.toString(), Toast.LENGTH_LONG);
            toast2.setGravity(Gravity.TOP | Gravity.LEFT, 50, 50);
            toast2.show();


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.customsites.de/post.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("JsonString", obj.toString()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            
            ContentValues newValues2 = new ContentValues();

            newValues.put(TrackDBOpenHelper.SYNCED, "1");

            db.update(TrackDBOpenHelper.DATABASE_TABLE_TRACKS, newValues2, updatewhere, id_track_array);
        }
        
        Toast toast1 = Toast.makeText(Tracks.this, "All tracks already syncronized", Toast.LENGTH_LONG);
            toast1.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            toast1.show();
        
        }


        if (!(this.isOnline() == true)) {

            Toast toast = Toast.makeText(Tracks.this, "For syncronising your track, please activate Internet Access", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 50, 50);
            toast.show();

        }
    }

    @Override
    public void onRestart() {

        super.onRestart();
        listContent.invalidate();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}