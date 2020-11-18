package daksh.soundly.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daksh.soundly.Adapters.PlaylistAdapter;
import daksh.soundly.Databases.AllPlaylists;
import daksh.soundly.Databases.PlaylistDatabase;
import daksh.soundly.Model.Playlist;
import daksh.soundly.Model.Song;
import daksh.soundly.R;

public class Playlists extends Fragment {

    View rootView;
    Button createPlaylist;
    RecyclerView recyclerView;
    AllPlaylists allPlaylists;
    public static ArrayList<String> playlist_names;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_playlist,container,false);
        allPlaylists=new AllPlaylists(getContext().getApplicationContext());
        createPlaylist=(Button) rootView.findViewById(R.id.playlist_create);
        recyclerView=(RecyclerView) rootView.findViewById(R.id.playlist_recycler_view);
        ArrayList<Playlist> playlists=getPlaylists();
        setRecyclerView(playlists);
        createPlaylist.setOnClickListener(onClickListener);

        return rootView;
    }


    public void setRecyclerView(ArrayList<Playlist> playlists)
    {
        PlaylistAdapter adapter=new PlaylistAdapter(rootView.getContext(),playlists);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.setAdapter(adapter);
    }

    ArrayList<Playlist> getPlaylists()
    {
        ArrayList<Playlist> playlists=allPlaylists.getAllPlaylists();
        for(int i=0;i<playlists.size();i++)
        {
            Log.i("playlists",playlists.get(i).getName());
        }
        return playlists;
    }


    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            View popupView=getActivity().getLayoutInflater().inflate(R.layout.playlist_create_popup,null);
            builder.setView(popupView);
            final AlertDialog alertDialog=builder.create();
            alertDialog.show();
            final TextView playlist_name=(TextView) popupView.findViewById(R.id.editTextTextPersonName);
            Button add=(Button) popupView.findViewById(R.id.add_button);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!playlist_name.getText().toString().isEmpty()) {
                        String playlist_title=playlist_name.getText().toString();
                        if(playlist_title.contains("_"))
                        {
                            Toast.makeText(getContext(),"Playlist name should only contain alphabets and numbers",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            playlist_title = playlist_title.replace(" ", "_");
                            if (allPlaylists.ifAlreadyPresent(playlist_title)) {
                                Toast.makeText(getContext(), "Playlist with this name already present", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("name",playlist_title);
                                allPlaylists.addPlaylist(playlist_title);
                                PlaylistDatabase playlistDatabase = new PlaylistDatabase(rootView.getContext().getApplicationContext());
                                playlistDatabase.createTable(playlist_title);
                                ArrayList<Playlist> playlists=getPlaylists();
                                setRecyclerView(playlists);
                                alertDialog.dismiss();
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(),"Please enter a playlist name",Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    };
}
