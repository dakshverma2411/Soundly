package daksh.soundly.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daksh.soundly.Databases.DataBaseFunctions;
import daksh.soundly.Model.Playlist;
import daksh.soundly.Model.Song;
import daksh.soundly.R;

public class AddToPlaylistAdapter extends RecyclerView.Adapter<AddToPlaylistAdapter.ViewHolder> {

    Context context;
    ArrayList<Playlist> playlists;
    Song song;

    public AddToPlaylistAdapter(Context context, ArrayList<Playlist> playlists,Song song) {
        this.context = context;
        this.playlists = playlists;
        this.song=song;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item,parent,false);
        return new AddToPlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Playlist playlist=playlists.get(position);
        holder.playlist_name.setText(playlist.getName().replace("_"," "));
        final int num=playlist.getNumberOfsong();
        holder.numSongs.setText(String.valueOf("Songs : "+num));
        holder.playlist_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseFunctions.setContext(context);
                if(DataBaseFunctions.addSongToPlaylist(playlist,song))
                {
                    Toast.makeText(context,"Song added to playlist",Toast.LENGTH_SHORT).show();
                    holder.numSongs.setText("Songs : "+String.valueOf(num+1));
                }
                else
                {
                    Toast.makeText(context,"Song is already present in the playlist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView playlist_name;
        TextView numSongs;
        ImageView more;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlist_name=(TextView) itemView.findViewById(R.id.playlist_item_name);
            numSongs=(TextView) itemView.findViewById(R.id.playlist_item_songs_number);
            more=(ImageView) itemView.findViewById(R.id.playlist_item_more);
        }
    }
}
