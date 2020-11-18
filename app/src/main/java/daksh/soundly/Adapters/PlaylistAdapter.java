package daksh.soundly.Adapters;

import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import daksh.soundly.Databases.DataBaseFunctions;
import daksh.soundly.Databases.PlaylistDatabase;
import daksh.soundly.Model.Playlist;
import daksh.soundly.Model.Song;
import daksh.soundly.R;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    Context context;
    public static ArrayList<Playlist> playlists;
    public static BottomSheetDialog bottomSheetDialog;

    public PlaylistAdapter(Context context, ArrayList<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item,parent,false);
        return new PlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Playlist playlist=playlists.get(position);
        Log.i("adapter",playlist.getName());
        holder.playlist_name.setText(playlist.getName().replace("_"," "));
        holder.playlist_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog=new BottomSheetDialog(
                        context,R.style.BottomSheetDialogTheme
                );
                View bottomSheetView=LayoutInflater.from(context)
                        .inflate(
                                R.layout.playlist_bottom_sheet,
                                (LinearLayout) view.findViewById(R.id.bottom_sheet_container)
                        );
                TextView playlist_name_bottom_sheet=(TextView) bottomSheetView.findViewById(R.id.playlist_name_bottom_sheet);
                playlist_name_bottom_sheet.setText(playlist.getName().replace("_"," "));
                RecyclerView recyclerView=(RecyclerView) bottomSheetView.findViewById(R.id.bottom_sheet_recycler_view);
                PlaylistDatabase playlistDatabase=new PlaylistDatabase(context.getApplicationContext());
                ArrayList<Song> songs=playlistDatabase.getAllSongs(playlist.getName());
                PlaylistSongsAdapter adapter=new PlaylistSongsAdapter(context,songs,playlist);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(adapter);

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });
        holder.numSongs.setVisibility(View.GONE);
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=new PopupMenu(holder.more.getContext(),holder.more);
                popupMenu.inflate(R.menu.playlist_popup);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId())
                        {
                            case R.id.delete_playlist:
                                DataBaseFunctions.deletePlaylist(playlist);
                                playlists.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,playlists.size());
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
