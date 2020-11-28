package daksh.soundly.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import daksh.soundly.Databases.DataBaseFunctions;
import daksh.soundly.Databases.Util;
import daksh.soundly.R;

public class Settings extends AppCompatActivity {

    RelativeLayout deleteAllPlaylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Settings");
        deleteAllPlaylist=(RelativeLayout) findViewById(R.id.delete_all_playlist);
        deleteAllPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Mytask().execute();
            }
        });
    }
}

class Mytask extends AsyncTask<Void,Void,Void>
{
    @Override
    protected Void doInBackground(Void... voids) {
        DataBaseFunctions.deletaAllPlaylist();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        Toast.makeText(Util.getAppContext(),"Done",Toast.LENGTH_SHORT).show();

    }
}
// skip online songs
// clear all playlists
// clear all favourites
// Edit profile...2 3 options
