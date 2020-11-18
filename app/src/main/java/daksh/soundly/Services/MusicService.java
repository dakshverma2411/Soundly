package daksh.soundly.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Queue;

import daksh.soundly.MainActivity;
import daksh.soundly.Model.Song;
import daksh.soundly.R;
import daksh.soundly.ui.Player;

import static daksh.soundly.App.CHANNEL_ID;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener{

    public static ArrayList<Song> queueSongs;
    public static Song currentSong;
    public static boolean repeat;


    private static final String INTENT_PATH_TAG = "song_path";
    public static MediaPlayer mediaPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        playSong();
        Intent notificationIntent=new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification notification=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Soundly")
                .setContentText("playing song")
                .setSmallIcon(R.drawable.player_logo)
                .setNotificationSilent()
                .setContentIntent(pendingIntent)
                .build();

        startForeground(111,notification);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(queueSongs.size()>0)
        {

        }
        else
        {

        }
    }

    private void playSong() {

        Uri songURI=currentSong.getPathToSong();
        Log.i("service_song",songURI.toString());
        try {
            mediaPlayer.setDataSource(this,songURI);
        } catch (IOException e) {
            Log.i("service_song","error");
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    @Override
    public void onDestroy() {

        mediaPlayer.stop();
        mediaPlayer.release();
        super.onDestroy();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        super.onTaskRemoved(rootIntent);
        stopForeground(true);
        stopSelf();

    }



}
