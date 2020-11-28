package daksh.soundly.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import daksh.soundly.Databases.DataBaseFunctions;
import daksh.soundly.Databases.PlaylistDatabase;
import daksh.soundly.MainActivity;
import daksh.soundly.Model.Playlist;
import daksh.soundly.Model.Song;
import daksh.soundly.R;

public class PlaylistSongsAdapter extends RecyclerView.Adapter<PlaylistSongsAdapter.ViewHolder> {
    Context context;
    ArrayList<Song> songs;
    Playlist playlist;

    public PlaylistSongsAdapter(Context context, ArrayList<Song> songs,Playlist playlist) {
        this.context = context;
        this.songs = songs;
        this.playlist=playlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new PlaylistSongsAdapter.ViewHolder(view);
}

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Song song=songs.get(position);
        String title=song.getTitle();
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
        holder.title.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                MainActivity.musicPlayerService.setSongs(songs);
                MainActivity.musicPlayerService.setCurrSongPosition(position);
                MainActivity.musicPlayerService.playMusic();
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=new PopupMenu(holder.more.getContext(),holder.more);
                popupMenu.inflate(R.menu.playlist_song_popup);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId())
                        {
                            case R.id.playlist_share:
                                Uri sharePath = song.getPathToSong();
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("audio/*");
                                share.putExtra(Intent.EXTRA_STREAM, sharePath);
                                context.startActivity(Intent.createChooser(share, "Share"));
                                break;

                            case R.id.playlist_remove_from_playlist:
                                DataBaseFunctions.removeSongFromPlaylist(playlist,song);
                                songs.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,songs.size());
                                break;

                            case R.id.playlist_add_to_queue:
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
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
}
