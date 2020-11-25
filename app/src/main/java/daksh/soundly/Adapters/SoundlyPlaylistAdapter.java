package daksh.soundly.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daksh.soundly.Model.Playlist;
import daksh.soundly.R;
import daksh.soundly.ui.Player;
import daksh.soundly.ui.Soundly;
import daksh.soundly.ui.SoundlyPlaylistSongs;

public class SoundlyPlaylistAdapter extends RecyclerView.Adapter<SoundlyPlaylistAdapter.ViewHolder> {

    Context context;
    ArrayList<Playlist> collections;

    public SoundlyPlaylistAdapter(Context context, ArrayList<Playlist> collections) {
        this.context = context;
        this.collections = collections;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.soundly_list_item,parent,false);
        return new SoundlyPlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Playlist playlist=collections.get(position);
        holder.title.setText(playlist.getName().replace("_"," "));
        holder.songsNumber.setText(String.valueOf(playlist.getNumberOfsong())+" Songs");
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, SoundlyPlaylistSongs.class);
                intent.putExtra(Soundly.COLLECTION,playlist.getName().replace("_"," "));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView songsNumber;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=(TextView) itemView.findViewById(R.id.soundly_item_name);
            songsNumber=(TextView) itemView.findViewById(R.id.soundly_item_songs_number);
            cardView=(CardView) itemView.findViewById(R.id.soundly_card_view);
        }
    }

}
