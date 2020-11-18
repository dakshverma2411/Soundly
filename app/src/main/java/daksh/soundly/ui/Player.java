package daksh.soundly.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import daksh.soundly.Adapters.QueueAdapter;
import daksh.soundly.Databases.Util;
import daksh.soundly.MainActivity;
import daksh.soundly.Model.Song;
import daksh.soundly.R;
import daksh.soundly.Serializer.UriDeserializer;
import daksh.soundly.Services.MusicPlayerService;
import daksh.soundly.Services.MusicService;

import static android.content.Context.MODE_PRIVATE;
import static daksh.soundly.MainActivity.musicPlayerService;

public class Player extends Fragment  {

    public static QueueAdapter adapter;
    View rootView;
    public static TextView player_title;
    ImageView player_play;
    public static TextView player_artist;
    ImageView player_forward;
    ImageView player_rewind;
    ImageView player_next;
    ImageView player_previous;
    ImageView player_queue;
    public static SeekBar seekBar;

    public static String title="-";
    public static String artist="-";
    Timer timer;
    Handler mHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_player,container,false);
        seekBar=(SeekBar) rootView.findViewById(R.id.player_seekbar);
        player_title=(TextView) rootView.findViewById(R.id.player_title);
        player_artist=(TextView) rootView.findViewById(R.id.player_artist);
        player_play=(ImageView) rootView.findViewById(R.id.player_play);
        player_forward=(ImageView) rootView.findViewById(R.id.player_forward);
        player_rewind=(ImageView) rootView.findViewById(R.id.player_rewind);
        player_next=(ImageView) rootView.findViewById(R.id.player_next);
        player_previous = (ImageView) rootView.findViewById(R.id.player_previous);
        player_queue=(ImageView) rootView.findViewById(R.id.player_queue);
        player_queue.setOnClickListener(queueClickListener);


        if(musicPlayerService.isSet)
        {
            seekBar.setMax(musicPlayerService.getCurrSong().getDuration()/1000);
        }
        else
        {
            seekBar.setMax(0);
        }

//        mHandler = new Handler();
//        getActivity().runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                    int mCurrentPosition = musicPlayerService.getCurrSongPosition() / 1000;
//                    seekBar.setProgress(mCurrentPosition);
//                mHandler.postDelayed(this, 1000);
//            }
//        });
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int mCurrentPosition = musicPlayerService.getCurrSongPosition() / 1000;
                seekBar.setProgress(mCurrentPosition);
            }
        },0,1000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b)
                {
                    musicPlayerService.seekTo(i*1000);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        player_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicPlayerService.nextSong();
            }
        });

        player_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicPlayerService.prevSong();
            }
        });


        if(musicPlayerService.isSongPlaying())
        {
            player_play.setImageResource(R.drawable.ic_pause);
        }
        else
        {
            player_play.setImageResource(R.drawable.ic_player_play_logo);
        }
        player_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicPlayerService.isSongPlaying()) {
                    musicPlayerService.pauseMusic();
                    player_play.setImageResource(R.drawable.ic_player_play_logo);
                }
                else
                {
                    musicPlayerService.playMusic();
                    player_play.setImageResource(R.drawable.ic_pause);
                }
            }
        });

        player_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicPlayerService.seekplus();
            }
        });

        player_rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicPlayerService.seekminus();
            }
        });

        if(musicPlayerService.isSet==true)
        {
            String title=musicPlayerService.getCurrSong().getTitle();
            if(title.length()>15)
            {
                title=title.substring(0,15)+"...";
            }
            String artist=musicPlayerService.getCurrSong().getArtist();
            if(artist.length()>15)
            {
                artist=artist.substring(0,15)+"...";
            }
            player_title.setText(title);
            player_artist.setText(artist);
        }


        return rootView;

    }

    View.OnClickListener queueClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            musicPlayerService.queueOpened=true;
            final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(getActivity(),R.style.BottomSheetDialogTheme);

            View bottomSheetView=LayoutInflater.from(Util.getAppContext())
                    .inflate(
                            R.layout.queue_bottom_sheet,
                            (LinearLayout) view.findViewById(R.id.queue_bottom_sheet_container)
                    );

            RecyclerView recyclerView=(RecyclerView) bottomSheetView.findViewById(R.id.queue_bottom_sheet_recycler_view);
            TextView edit=(TextView) bottomSheetView.findViewById(R.id.queue_edit_button);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();
                    startActivity(new Intent(getActivity(),EditQueue.class));
                }
            });

            ArrayList<Song> queueSongs=musicPlayerService.getSongs();
            int pos=musicPlayerService.getCurrSongPosition();
            adapter=new QueueAdapter(queueSongs,Util.getAppContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(Util.getAppContext()));
            recyclerView.setAdapter(adapter);
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
