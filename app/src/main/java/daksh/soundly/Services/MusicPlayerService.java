package daksh.soundly.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import daksh.soundly.Databases.Util;
import daksh.soundly.MainActivity;
import daksh.soundly.Model.Playlist;
import daksh.soundly.Model.Song;
import daksh.soundly.R;
import daksh.soundly.Serializer.UriSerializer;
import daksh.soundly.ui.Player;

import static daksh.soundly.App.CHANNEL_ID;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener{

    public ArrayList<Song> songs;
    public Song currSong=null;
    public int currSongPosition;
    public boolean isSet=false;
    public boolean isPlaying=false;
    public static boolean queueOpened=false;
    public MediaPlayer mediaPlayer;

    public void setSongs(ArrayList<Song> songs) {
        Log.i("service","songs set");
        this.songs=songs;
        SharedPreferences prefs= Util.getAppContext().getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
//        editor.putInt("current_song_position",currSongPosition);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriSerializer())
                .create();
        String json=gson.toJson(songs);
        editor.putString("queue",json);
        editor.commit();
    }
    public void addToQueue(Song song)
    {
        this.songs.add(currSongPosition,song);
        currSongPosition++;
        SharedPreferences prefs= Util.getAppContext().getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
//        editor.putInt("current_song_position",currSongPosition);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriSerializer())
                .create();
        String json=gson.toJson(songs);
        editor.putString("queue",json);
        editor.commit();
    }
    public void changePosition(int position)
    {
        currSongPosition=position;
        SharedPreferences prefs= Util.getAppContext().getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putInt("current_song_position",currSongPosition);
        editor.commit();
    }
    public void setCurrSongPosition(int i)  {
        if(isSet)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=new MediaPlayer();
        }

        this.currSongPosition=i;
        SharedPreferences prefs= Util.getAppContext().getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putInt("current_song_position",currSongPosition);
        editor.commit();
        currSong=songs.get(i);
        Log.i("service",String.valueOf(i));
        try {
            mediaPlayer.setDataSource(this, currSong.getPathToSong());
            Log.i("currSong",currSong.getPathToSong().toString());
        }
        catch (Exception e) {

        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isSet=true;
        Player.seekBar.setMax(currSong.getDuration()/1000);
        mediaPlayer.setOnCompletionListener(this);
    }

    public ArrayList<Song> getSongs()
    {
        if(songs!=null)
        {
            return songs;
        }
        else
        {
            return new ArrayList<Song>();
        }
    }

    public int getCurrSongPosition()
    {
        if(isSet)
        {
            return mediaPlayer.getCurrentPosition();
        }
        else
        {
            return 0;
        }

    }
    public int getCurrentSongInQueue()
    {
        if(songs!=null)
        {
            return currSongPosition;
        }
        else
        {
            return -1;
        }
    }


    public void playMusic() {

        Song currentSong=songs.get(currSongPosition);
        String title=currentSong.getTitle();
        if(title.length()>15)
        {
            title=title.substring(0,15)+"...";
        }
        String artist=currentSong.getArtist();
        if(artist.length()>15)
        {
            artist=artist.substring(0,15)+"...";
        }
        Player.player_title.setText(title);
        Player.player_artist.setText(artist);
        mediaPlayer.start();
        isPlaying=true;
    }
    public void pauseMusic() {
        mediaPlayer.pause();
        isPlaying=false;
    }

    public void seekplus() {
        if(isSet) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
        }
    }
    public void seekminus() {
        if(isSet) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
        }
    }
    public void seekTo(int pos)
    {
        if(isSet)
        {
            mediaPlayer.seekTo(pos);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.i("service","song complete");
        if(currSongPosition==songs.size()-1)
        {
            setCurrSongPosition(0);
        }
        else
        {
            setCurrSongPosition(currSongPosition+1);
        }
        playMusic();
        if(queueOpened)
        {
            Player.adapter.notifyDataSetChanged();
        }
//
    }

    public void nextSong()
    {
        if(isSet) {
            if (currSongPosition == songs.size() - 1) {
                setCurrSongPosition(0);
            } else {
                setCurrSongPosition(currSongPosition + 1);
            }
            Song currentSong=songs.get(currSongPosition);
            String title=currentSong.getTitle();
            if(title.length()>15)
            {
                title=title.substring(0,15)+"...";
            }
            String artist=currentSong.getArtist();
            if(artist.length()>15)
            {
                artist=artist.substring(0,15)+"...";
            }
            Player.player_title.setText(title);
            Player.player_artist.setText(title);
            if(isPlaying) {
                playMusic();
            }
        }
    }


    public void prevSong() {
        if(isSet) {
            if (currSongPosition == 0) {
                setCurrSongPosition(songs.size() - 1);
            } else {
                setCurrSongPosition(currSongPosition - 1);
            }
            Song currentSong=songs.get(currSongPosition);
            String title=currentSong.getTitle();
            if(title.length()>15)
            {
                title=title.substring(0,15)+"...";
            }
            String artist=currentSong.getArtist();
            if(artist.length()>15)
            {
                artist=artist.substring(0,15)+"...";
            }
            Player.player_title.setText(title);
            Player.player_artist.setText(artist);
            Player.title=currSong.getTitle();
            if(isPlaying) {
                playMusic();
            }
        }
    }

    public class MyBinder extends Binder {
        public MusicPlayerService getService()
        {
            return MusicPlayerService.this;
        }
    }

    public MyBinder binder=new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer=new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction()=="PAUSE_SONG")
        {
            if(isPlaying)
            {
                mediaPlayer.pause();
                isPlaying=false;
            }
        }
        else
        {
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );

            // foreground service
            Intent notificationIntent=new Intent(this, MainActivity.class);
            notificationIntent.putExtra("notification","fromNotification");
            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0);

            Intent pIntent=new Intent(this,MusicPlayerService.class);
            pIntent.setAction("PAUSE_SONG");
            PendingIntent pPendingIntent=PendingIntent.getService(this,100,pIntent,0);

            Notification notification=new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setContentTitle("Soundly")
                    .setContentText("playing song")
                    .addAction(new NotificationCompat.Action(R.drawable.player_logo,"Pause",pPendingIntent))
                    .setSmallIcon(R.drawable.player_logo)
                    .setNotificationSilent()
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(111,notification);
        }

        return START_NOT_STICKY;

    }


    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        currSong=null;
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopForeground(true);
        stopSelf();
    }

    public boolean isSongPlaying() {
        return isPlaying;
    }

    public Song getCurrSong()
    {
        return currSong;
    }


}
