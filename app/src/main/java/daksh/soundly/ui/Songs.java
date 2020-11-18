package daksh.soundly.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daksh.soundly.Adapters.SongsAdapter;
import daksh.soundly.MainActivity;
import daksh.soundly.Model.Song;
import daksh.soundly.R;

public class Songs extends Fragment {


    RecyclerView recyclerView;
    SongsAdapter adapter;

    Button reload;
    View rootView;
    EditText search;
    ArrayList<Song> songs=null;
    int PERMISSION_CODE=101;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_songs,container,false);
        rootView=view;
        reload=(Button) rootView.findViewById(R.id.reload);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSongs();
            }
        });
        search=(EditText) rootView.findViewById(R.id.songs_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
        recyclerView = (RecyclerView) rootView.findViewById(R.id.songs_recyclerview);
        MutableLiveData<Song> liveCurrSong=MainActivity.musicPlayerService.getLiveCurrSong();
        liveCurrSong.observe(this, new Observer<Song>() {
            @Override
            public void onChanged(Song song) {
                if(adapter!=null)
                {
                    adapter.notifyDataSetChanged();
                }

            }
        });
        return view;
    }

    public void filter(String s)
    {
        ArrayList<Song> filteredList=new ArrayList<>();
        if(songs!=null)
        {
            for(Song song:songs)
            {
                if(song.getTitle().toLowerCase().contains(s))
                {
                    filteredList.add(song);
                }
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if(songs==null) {
            getSongs();
        }
        else
        {
            adapter = new SongsAdapter(rootView.getContext(), songs);
            recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            recyclerView.setAdapter(adapter);
        }



    }




    public void getSongs()
    {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M)
        {

            songs=new ArrayList<>();
            ContentResolver contentResolver=rootView.getContext().getContentResolver();
            Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor=contentResolver.query(uri,null,null,null,null);
            if(cursor==null)
            {
                Log.i("content resolver","query failed");
            }
            else if(!cursor.moveToFirst())
            {
                Log.i("content provider", "no media available");
            }
            else
            {
                int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int artistColumn=cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int albumcolumn=cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int durationColumn=cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int idColumn=cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                do{
                    String title=cursor.getString(titleColumn);
                    String artist=cursor.getString(artistColumn);
                    String album=cursor.getString(albumcolumn);
                    int duration=cursor.getInt(durationColumn);
                    long id=cursor.getLong(idColumn);
                    Uri songUri= ContentUris.withAppendedId(
                            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                    songs.add(new Song(title,artist,album,duration,songUri));
                    Log.i("songslist",songUri.toString());
                }while(cursor.moveToNext());
            }
            adapter = new SongsAdapter(rootView.getContext(), songs);
            recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            recyclerView.setAdapter(adapter);
        }
        else
        {
            if(ContextCompat.checkSelfPermission(rootView.getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            {
                songs=new ArrayList<>();
                ContentResolver contentResolver=rootView.getContext().getContentResolver();
                Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Cursor cursor=contentResolver.query(uri,null,null,null,null);
                if(cursor==null)
                {
                    Log.i("content resolver","query failed");
                }
                else if(!cursor.moveToFirst())
                {
                    Log.i("content provider", "no media available");
                }
                else
                {
                    int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int artistColumn=cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int albumcolumn=cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int durationColumn=cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                    int idColumn=cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    do{
                        String title=cursor.getString(titleColumn);
                        String artist=cursor.getString(artistColumn);
                        String album=cursor.getString(albumcolumn);
                        int duration=cursor.getInt(durationColumn);
                        long id=cursor.getLong(idColumn);
                        Uri songUri= ContentUris.withAppendedId(
                                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                        songs.add(new Song(title,artist,album,duration,songUri));
                    }while(cursor.moveToNext());
                }

                adapter = new SongsAdapter(rootView.getContext(), songs);
                recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
                recyclerView.setAdapter(adapter);
            }
            else
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
            }
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==101) {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(rootView.getContext(),"granted",Toast.LENGTH_LONG).show();
                songs=new ArrayList<>();
                ContentResolver contentResolver=rootView.getContext().getContentResolver();
                Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Cursor cursor=contentResolver.query(uri,null,null,null,null);
                if(cursor==null)
                {
                    Log.i("content resolver","query failed");
                }
                else if(!cursor.moveToFirst())
                {
                    Log.i("content provider", "no media available");
                }
                else
                {
                    int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int artistColumn=cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int albumcolumn=cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int idColumn=cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    int durationColumn=cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                    do{
                        String title=cursor.getString(titleColumn);
                        String artist=cursor.getString(artistColumn);
                        String album=cursor.getString(albumcolumn);
                        int duration=cursor.getInt(durationColumn);
                        long id=cursor.getLong(idColumn);
                        Uri songUri= ContentUris.withAppendedId(
                                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                        songs.add(new Song(title,artist,album,duration,songUri));
                    }while(cursor.moveToNext());
                }


                adapter = new SongsAdapter(rootView.getContext(), songs);
                recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
                recyclerView.setAdapter(adapter);
            }
            else
            {
                Toast.makeText(rootView.getContext(),"denied",Toast.LENGTH_LONG).show();
            }
        }
    }
}
