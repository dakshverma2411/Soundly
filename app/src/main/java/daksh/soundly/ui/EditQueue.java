package daksh.soundly.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import daksh.soundly.Adapters.EditQueueAdapter;
import daksh.soundly.MainActivity;
import daksh.soundly.Model.Song;
import daksh.soundly.R;

public class EditQueue extends AppCompatActivity {

    ArrayList<Song> songs;
    RecyclerView recyclerView;
    Song currentSong;
    EditQueueAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_queue);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Edit Queue");

        currentSong=getCurrentSong();
        int pos=MainActivity.musicPlayerService.getCurrentSongInQueue();

        songs=getSongs(pos);
        recyclerView=findViewById(R.id.edit_queue_recycler_view);
        adapter=new EditQueueAdapter(songs,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public Song getCurrentSong()
    {
        return MainActivity.musicPlayerService.getCurrSong();
    }

    public ArrayList<Song> getSongs(int pos)
    {
        ArrayList<Song> songs=new ArrayList<>();
        int start;
        ArrayList<Song> queueSongs=MainActivity.musicPlayerService.getSongs();
        int n=queueSongs.size();
        if(pos==queueSongs.size()-1)
        {
            start=0;
        }
        else
        {
            start=pos+1;
        }
        for(int i=start;i!=pos;i=(i+1)%n)
        {
            songs.add(queueSongs.get(i));
        }

        return songs;
    }

    ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition=viewHolder.getAdapterPosition();
            int toPosition=target.getAdapterPosition();
            Collections.swap(songs,fromPosition,toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            songs.remove(viewHolder.getAdapterPosition());
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.edit_queue_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.done:
                songs.add(0,currentSong);
                MainActivity.musicPlayerService.changePosition(0);
                MainActivity.musicPlayerService.setSongs(songs);
                Toast.makeText(this,"Queue updated",Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.cancel:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}