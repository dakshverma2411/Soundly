package daksh.soundly.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Queue;

import daksh.soundly.MainActivity;
import daksh.soundly.Model.Song;
import daksh.soundly.R;


public class EditQueueAdapter extends RecyclerView.Adapter<EditQueueAdapter.ViewHolder> {

    ArrayList<Song> songs;
    Context context;

    public EditQueueAdapter(ArrayList<Song> songs, Context context) {
        this.songs = songs;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new EditQueueAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song=songs.get(position);
        holder.move.setVisibility(View.GONE);
        holder.more.setVisibility(View.GONE);
        String name=song.getTitle();
        String singer=song.getArtist();
        if(name.length()>20)
        {
            name=name.substring(0,20)+"...";
        }
        if(singer.length()>20)
        {
            singer=singer.substring(0,20)+"...";
        }
        holder.title.setText(name);
        holder.artist.setText(singer);


    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView artist;
        ImageView more;
        ImageView move;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=(TextView) itemView.findViewById(R.id.item_song_name);
            move=(ImageView) itemView.findViewById(R.id.player_cover_image);
            artist=(TextView) itemView.findViewById(R.id.item_song_singer);
            more=(ImageView) itemView.findViewById(R.id.item_more);
        }
    }
}
