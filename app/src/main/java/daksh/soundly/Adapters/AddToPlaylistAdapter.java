package daksh.soundly.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Playlist playlist=playlists.get(position);
        holder.playlist_name.setText(playlist.getName().replace("_"," "));
        holder.numSongs.setText(String.valueOf(playlist.getNumberOfsong()+" Songs"));
        holder.playlist_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseFunctions.setContext(context);
                DataBaseFunctions.addSongToPlaylist(playlist,song);
                SongsAdapter.bottomSheetDialog.dismiss();
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
