package esw.peeplo.studentstudycom.util;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import esw.peeplo.studentstudycom.R;
import io.paperdb.Paper;

public class AppClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //context paper db init
        Paper.init(getApplicationContext());

        //picasso cache mode
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        //create default channel
        createNotificationChannel();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //foreground
            NotificationChannel foregroundChannel = new NotificationChannel(
                    Common.FOREGROUND_NOTIFICATION_CHANNEL,
                    "ForegroundChannel",
                    NotificationManager.IMPORTANCE_NONE
            );
            foregroundChannel.setShowBadge(false);
            foregroundChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager foreManager = getSystemService(NotificationManager.class);
            foreManager.createNotificationChannel(foregroundChannel);

            //normal
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.alarm);

            NotificationChannel serviceChannel = new NotificationChannel(
                    Common.DEFAULT_NOTIFICATION_CHANNEL,
                    "DefaultChannel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            serviceChannel.enableVibration(true);
            serviceChannel.enableLights(true);
            serviceChannel.setShowBadge(false);
            serviceChannel.setSound(sound, attributes);
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}


