package daksh.soundly.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import daksh.soundly.Adapters.SoundlyPlaylistSongAdapter;
import daksh.soundly.Model.Song;
import daksh.soundly.R;

public class SoundlyPlaylistSongs extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    SoundlyPlaylistSongAdapter adapter;
    FirebaseFirestore firestore;
    String collection;
    ArrayList<Song> songs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundly_playlist_songs);
        recyclerView=(RecyclerView) findViewById(R.id.soundly_playlist_recycler_view);
        progressBar=(ProgressBar) findViewById(R.id.soundly_playlist_progress_bar);
        Intent intent=getIntent();
        collection=intent.getStringExtra(Soundly.COLLECTION);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(collection.replace("_"," "));
        songs=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new SoundlyPlaylistSongAdapter(songs,this,7);
        recyclerView.setAdapter(adapter);
        getSongs();

    }
    void getSongs()
    {
        firestore=FirebaseFirestore.getInstance();
        firestore.collection(collection).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    progressBar.setVisibility(View.GONE);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name=document.getString("title");
                        String artist=document.getString("artist");
                        String album=document.getString("album");
                        String path=document.getString("pathToSong");
//                        String path="google";
                        int duration=Integer.parseInt(String.valueOf(document.getLong("duration")));
                        int online=1;
                        Song song=new Song(name,artist,album,duration, Uri.parse(path),online);
                        songs.add(song);
                        Log.i("soundly_song",song.getTitle());
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}

/* https://stackoverflow.com/questions/29106484/how-to-add-a-button-at-the-end-of-recyclerview */