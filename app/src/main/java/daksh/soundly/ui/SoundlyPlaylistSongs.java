package daksh.soundly.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import daksh.soundly.R;

public class SoundlyPlaylistSongs extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundly_playlist_songs);
        recyclerView=(RecyclerView) findViewById(R.id.soundly_playlist_recycler_view);
        progressBar=(ProgressBar) findViewById(R.id.soundly_playlist_progress_bar);
        Intent intent=getIntent();
        String collection=intent.getStringExtra(Soundly.COLLECTION);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(collection);

    }
}