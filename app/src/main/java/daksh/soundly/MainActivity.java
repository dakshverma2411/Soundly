package daksh.soundly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import daksh.soundly.Databases.Util;
import daksh.soundly.Model.Song;
import daksh.soundly.Serializer.UriDeserializer;
import daksh.soundly.Services.MusicPlayerService;
import daksh.soundly.ui.Favourites;
import daksh.soundly.ui.Player;
import daksh.soundly.ui.Playlists;
import daksh.soundly.ui.RegisterActivity;
import daksh.soundly.ui.Settings;
import daksh.soundly.ui.Songs;
import daksh.soundly.ui.Soundly;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    FirebaseAuth mAuth;
    Toolbar toolbar;
    View headerView;
    Fragment songsFragment=new Songs();
    BottomNavigationView bottomNavigationView;
    public static final String SHARED_PREFERENCES="prefs";

    // Music service

    public static MusicPlayerService musicPlayerService;
    public static boolean isBound=false;
    public static ServiceConnection serviceConnection;
    public static Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView=(NavigationView) findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation, R.string.close_navigation);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        headerView=navigationView.getHeaderView(0);
        TextView email=(TextView) headerView.findViewById(R.id.nav_header_email);
        email.setText(mAuth.getCurrentUser().getEmail());
        bottomNavigationView=(BottomNavigationView) findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        serviceIntent=new Intent(this,MusicPlayerService.class);
        startService(serviceIntent);
        if(!isBound)
        {
            bindMusicService();
        }

        FragmentManager fragmentManager= getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container,new Soundly()).commit();

    }



    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.favourites:
                    startActivity(new Intent(MainActivity.this,Favourites.class));
                    break;

                case R.id.settings:
                    startActivity(new Intent(MainActivity.this, Settings.class));
                    break;

                case R.id.logout_drawer:
                    if(isBound)
                    {
                        unbindService(serviceConnection);
                        isBound=false;
                    }
                    musicPlayerService.stopSelf();
//                    musicPlayerService.closeService();
//                    musicPlayerService=null;
//                    serviceConnection=null;
//                    musicPlayerService.onTaskRemoved(serviceIntent);
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                    finish();
                    break;

            }
            return true;
        }
    };

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.i("player_fragment","onSavedInstanceState");
    }

    private void bindMusicService() {
        if(serviceConnection==null)
        {
            serviceConnection=new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    MusicPlayerService.MyBinder myBinder=(MusicPlayerService.MyBinder) iBinder;
                    musicPlayerService=myBinder.getService();
                    // code removed from here
                    isBound=true;

                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    isBound=false;
                }
            };
        }
        bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment=null;
            switch (item.getItemId()) {
                case R.id.soundly:
                    fragment=new Soundly();
                    toolbar.setTitle("Soundly");
                    break;
                case R.id.player:
                    fragment=new Player();
                    toolbar.setTitle("Player");
                    break;
                case R.id.songs:
                    fragment=songsFragment;
                    toolbar.setTitle("All Songs");
                    break;
                case R.id.playlists:
                    fragment=new Playlists();
                    toolbar.setTitle("My Playlist");
                    break;
            }

            FragmentManager fragmentManager= getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isBound)
        {
            unbindService(serviceConnection);
            isBound=false;
        }
    }

}


//                    Fragment fragment=new Player();
//                    FragmentManager fragmentManager= getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
//                    fragmentTransaction.add(R.id.container,fragment);
//                    fragmentTransaction.commitNowAllowingStateLoss();
//                    Player.player_title.setText("-");
//                    Player.player_artist.setText("-");


//                    SharedPreferences prefs= Util.getAppContext().getSharedPreferences(MainActivity.SHARED_PREFERENCES,MODE_PRIVATE);
//                    if(prefs!=null)
//                    {
//                        int pos=prefs.getInt("current_song_position",-1);
//                        String json=prefs.getString("queue",null);
//
//                        if(json!=null && pos!=-1)
//                        {
//                            Gson gson = new GsonBuilder()
//                                    .registerTypeAdapter(Uri.class, new UriDeserializer())
//                                    .create();
//
//                            Type type=new TypeToken<ArrayList<Song>>() {}.getType();
//                            ArrayList<Song> songs=gson.fromJson(json,type);
//
//
//                            if(songs!=null || songs.size()==0) {
//
//                                Log.i("sharedPrefs",String.valueOf(songs.size())+" "+pos);
//                                musicPlayerService.setSongs(songs);
//                                musicPlayerService.setCurrSongPosition(pos);
//                            }
//                        }
//                    }