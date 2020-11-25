package daksh.soundly.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.io.UTFDataFormatException;
import java.util.ArrayList;
import java.util.zip.Inflater;

import daksh.soundly.Databases.AllPlaylists;
import daksh.soundly.Databases.DataBaseFunctions;
import daksh.soundly.Databases.PlaylistDatabase;
import daksh.soundly.Databases.Util;
import daksh.soundly.MainActivity;
import daksh.soundly.Model.Playlist;
import daksh.soundly.Model.Song;
import daksh.soundly.QueueSongs.QueueUtils;
import daksh.soundly.R;
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    Context context;
    ArrayList<Song> songList;
    public static View bottomSheetView;
    public static BottomSheetDialog bottomSheetDialog;

    private static final String INTENT_PATH_TAG = "song_path";

    public SongsAdapter(Context context, ArrayList<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public SongsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SongsAdapter.ViewHolder holder, final int position) {
        final Song song=songList.get(position);
        String title=song.getTitle();
        if(MainActivity.musicPlayerService.isSet)
        {
            if(song.getTitle().equals(MainActivity.musicPlayerService.getCurrSong().getTitle()))
            {
                holder.title.setTextColor(Color.parseColor("#1DB954"));
            }
            else
            {

                holder.title.setTextColor(Color.parseColor("#000000"));
            }
        }


        if(title.length()>20)
        {
            title=title.substring(0,20)+"...";
        }
        String artist=song.getArtist();
        if(artist.length()>12)
        {
            artist=artist.substring(0,12)+"...";
        }
        holder.title.setText(title);
        holder.artist.setText(artist);
        final Uri path=song.getPathToSong();
        holder.title.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                MainActivity.musicPlayerService.setSongs(songList);
                MainActivity.musicPlayerService.setCurrSongPosition(position);
                MainActivity.musicPlayerService.playMusic();

            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=new PopupMenu(holder.more.getContext(),holder.more);
                popupMenu.inflate(R.menu.songs_popup);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId())
                        {
                            case R.id.share:
                                Uri sharePath = song.getPathToSong();
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("audio/*");
                                share.putExtra(Intent.EXTRA_STREAM, sharePath);
                                context.startActivity(Intent.createChooser(share, "Share"));
                                break;


                            case R.id.add_to_playlist:
                                bottomSheetDialog=new BottomSheetDialog(
                                        context,R.style.BottomSheetDialogTheme
                                );
                                bottomSheetView=LayoutInflater.from(context)
                                        .inflate(
                                                R.layout.add_to_playlist_bottom_sheet,
                                                (LinearLayout) holder.more.findViewById(R.id.add_to_playlist_bottom_sheet_container)
                                        );
                                bottomSheetView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        bottomSheetDialog.dismiss();
                                    }
                                });
                                TextView newPlaylist=(TextView) bottomSheetView.findViewById(R.id.add_to_playlist_new_button);
                                newPlaylist.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                        LayoutInflater inflater = LayoutInflater.from(context);
                                        View popupView=inflater.inflate(R.layout.playlist_create_popup,null);
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
                                                        Toast.makeText(context,"Playlist name should only contain alphabets and numbers",Toast.LENGTH_SHORT).show();
                                                    }
                                                    else {
                                                        AllPlaylists allPlaylists=new AllPlaylists(Util.getAppContext());
                                                        playlist_title = playlist_title.replace(" ", "_");
                                                        if (allPlaylists.ifAlreadyPresent(playlist_title)) {
                                                            Toast.makeText(context, "Playlist with this name already present", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Log.i("name",playlist_title);
                                                            allPlaylists.addPlaylist(playlist_title);
                                                            PlaylistDatabase playlistDatabase = new PlaylistDatabase(Util.getAppContext().getApplicationContext());
                                                            playlistDatabase.createTable(playlist_title);
                                                            Playlist playlist=new Playlist(playlist_title,0);
                                                            DataBaseFunctions.addSongToPlaylist(playlist,song);
                                                            alertDialog.dismiss();
                                                        }
                                                    }
                                                    bottomSheetDialog.dismiss();
                                                }
                                                else
                                                {
                                                    Toast.makeText(context,"Please enter a playlist name",Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                    }
                                });
                                DataBaseFunctions.setContext(context);
                                ArrayList<Playlist> playlists= DataBaseFunctions.getAllPlaylists();
                                RecyclerView recyclerView=(RecyclerView) bottomSheetView.findViewById(R.id.add_to_playlist_recycler_view);
                                AddToPlaylistAdapter addToPlaylistAdapter=new AddToPlaylistAdapter(context,playlists,song);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                recyclerView.setAdapter(addToPlaylistAdapter);

                                bottomSheetDialog.setContentView(bottomSheetView);
                                bottomSheetDialog.show();
                                break;

                            case R.id.add_to_queue:
                                MainActivity.musicPlayerService.addToQueue(song);
                                Toast.makeText(context,"Song added to queue",Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void filterList(ArrayList<Song> filteredList)
    {
        songList=filteredList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView artist;
        ImageView more;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=(TextView) itemView.findViewById(R.id.item_song_name);
            more=(ImageView) itemView.findViewById(R.id.item_more);
            artist=(TextView) itemView.findViewById(R.id.item_song_singer);
        }
    }

//    View.OnClickListener onClickListener=new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//            AlertDialog.Builder builder=new AlertDialog.Builder(context);
//            LayoutInflater inflater = LayoutInflater.from(context);
//            View popupView=inflater.inflate(R.layout.playlist_create_popup,null);
//            builder.setView(popupView);
//            final AlertDialog alertDialog=builder.create();
//            alertDialog.show();
//            final TextView playlist_name=(TextView) popupView.findViewById(R.id.editTextTextPersonName);
//            Button add=(Button) popupView.findViewById(R.id.add_button);
//            add.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(!playlist_name.getText().toString().isEmpty()) {
//                        String playlist_title=playlist_name.getText().toString();
//                        if(playlist_title.contains("_"))
//                        {
//                            Toast.makeText(context,"Playlist name should only contain alphabets and numbers",Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            AllPlaylists allPlaylists=new AllPlaylists(Util.getAppContext());
//                            playlist_title = playlist_title.replace(" ", "_");
//                            if (allPlaylists.ifAlreadyPresent(playlist_title)) {
//                                Toast.makeText(context, "Playlist with this name already present", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Log.i("name",playlist_title);
//                                allPlaylists.addPlaylist(playlist_title);
//                                PlaylistDatabase playlistDatabase = new PlaylistDatabase(Util.getAppContext().getApplicationContext());
//                                playlistDatabase.createTable(playlist_title);
//                                Playlist playlist=new Playlist(playlist_title,0);
//                                alertDialog.dismiss();
//                            }
//                        }
//                        bottomSheetDialog.dismiss();
//                    }
//                    else
//                    {
//                        Toast.makeText(context,"Please enter a playlist name",Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            });
//
//        }
//    };

}
