package android.pack;

import android.app.TabActivity; 
import android.content.Intent; 
import android.os.Bundle; 
import android.widget.TabHost; 
import android.widget.TabHost.TabSpec; 
  
public class MainActivity extends TabActivity { 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.main); 
  
        TabHost tabHost = getTabHost(); 
  
        // Tab for Photos 
        TabSpec new_track = tabHost.newTabSpec("New Track"); 
        // setting Title and Icon for the Tab 
        new_track.setIndicator("New Track", getResources().getDrawable(R.drawable.nticon)); 
        Intent photosIntent = new Intent(this, NewTrack.class); 
        new_track.setContent(photosIntent); 
  
        // Tab for Songs 
        TabSpec tracks = tabHost.newTabSpec("Tracks"); 
        tracks.setIndicator("Tracks", getResources().getDrawable(R.drawable.ticon)); 
        Intent songsIntent = new Intent(this, Tracks.class); 
        tracks.setContent(songsIntent); 
  
        // Tab for Videos 
        TabSpec account = tabHost.newTabSpec("Account"); 
        account.setIndicator("Account", getResources().getDrawable(R.drawable.aicon)); 
        Intent videosIntent = new Intent(this, Account.class); 
        account.setContent(videosIntent); 
  
        // Adding all TabSpec to TabHost 
        tabHost.addTab(new_track); // Adding photos tab 
        tabHost.addTab(tracks); // Adding songs tab 
        tabHost.addTab(account); // Adding videos tab 
    }
}
