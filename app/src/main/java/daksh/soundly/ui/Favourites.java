package daksh.soundly.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import daksh.soundly.Databases.FavouritesDB;
import daksh.soundly.Databases.Util;
import daksh.soundly.Model.Song;
import daksh.soundly.R;

public class Favourites extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Favourites");
        FavouritesDB favouritesDB=new FavouritesDB(Util.getAppContext());
        ArrayList<Song> songs=favouritesDB.getAllSongs();
        Log.i("fav_songs",String.valueOf(songs.size()));
    }
}