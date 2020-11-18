package daksh.soundly;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import daksh.soundly.Databases.Util;
import daksh.soundly.Services.MusicPlayerService;
import daksh.soundly.Services.MusicService;

public class App extends Application {
    public static final String CHANNEL_ID="musicChannel";


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Util.setContext(getApplicationContext());
        MusicPlayerService musicPlayerService=new MusicPlayerService();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Intent intent=new Intent(getApplicationContext(), MusicService.class);
        stopService(intent);
    }

    public void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel=new NotificationChannel(
                    CHANNEL_ID,
                    "Soundly Music Services",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}
