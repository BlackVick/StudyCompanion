package esw.peeplo.studentstudycom.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import esw.peeplo.studentstudycom.CourseDetail;
import esw.peeplo.studentstudycom.Dashboard;
import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.databases.ScheduleDatabase;
import esw.peeplo.studentstudycom.models.Schedule;
import esw.peeplo.studentstudycom.util.Common;
import esw.peeplo.studentstudycom.util.Methods;
import esw.peeplo.studentstudycom.viewmodels.ScheduleViewModel;
import esw.peeplo.studentstudycom.viewmodels.SearchViewModel;
import io.paperdb.Paper;

public class ScheduleService extends Service {

    //values
    private String matric;

    //database
    private ScheduleDatabase database;

    //data
    private List<Schedule> fullScheduleList = new ArrayList<>();
    private List<Schedule> calledNotification = new ArrayList<>();

    //handler and runnables
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    public ScheduleService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //values
        matric = Paper.book().read(Common.USER_ID);

        //init database
        database = ScheduleDatabase.getScheduleDatabase(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //check if user is logged in
        if (matric != null && !TextUtils.isEmpty(matric)) {

            //fetch all user schedules
            fetchSchedules();

        } else {

            //kill service
            stopSelf();

        }

        //notification
        Intent farmDetailIntent = new Intent(getApplicationContext(), Dashboard.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, farmDetailIntent, 0);


        Notification notification = new NotificationCompat.Builder(getApplicationContext(), Common.FOREGROUND_NOTIFICATION_CHANNEL)
                .setContentTitle("Study Companion")
                .setContentText("Study Companion schedule engine Running.")
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();

        startForeground(30000, notification);

        return START_STICKY;
    }

    private void fetchSchedules() {

        //get list
        new Thread(() -> {

            //populate list
            fullScheduleList.addAll(database.scheduleDao().getAllUserDirectSchedules(matric));

            //check schedules
            checkForSchedule();

        }).start();

    }

    private void checkForSchedule() {

        //loop through today schedule
        for (Schedule schedule : fullScheduleList){

            //check time
            if ((schedule.getDay().equals(Methods.today())) && (schedule.getStart().equals(getCurrentTime()) || doesTimeFallWithin(schedule))){

                //activate reminder
                activateReminder(schedule);

            }
        }

        //repeat check
        handler.postDelayed(runnable = () -> {
            checkForSchedule();
        }, 35000);

    }

    @SuppressLint("SimpleDateFormat")
    private String getCurrentTime(){
        return new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
    }

    @SuppressLint("SimpleDateFormat")
    private boolean doesTimeFallWithin(Schedule schedule){

        //result
        boolean result = false;

        //get start and end time boundary for schedule
        LocalTime rangeStart = LocalTime.parse(schedule.getStart());
        LocalTime rangeEnd = LocalTime.parse(schedule.getStop());

        //get current time
        LocalTime newStart = LocalTime.parse(new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime()));

        //check
        if ((!newStart.isBefore(rangeStart) && newStart.isBefore(rangeEnd))) {
            result = true;
        }

        return result;

    }

    private void activateReminder(Schedule schedule) {

        //check if user already open activity
        if (!calledNotification.contains(schedule)) {

            //called notification
            calledNotification.add(schedule);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
            mp.start();

            //build notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Common.DEFAULT_NOTIFICATION_CHANNEL);
            Intent notificationIntent = new Intent(this, CourseDetail.class);
            notificationIntent.putExtra(Common.COURSE_DATA, schedule.getCourse());

            //add flags
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            //create pending intent
            PendingIntent notificationPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            //build
            builder.setSmallIcon(R.drawable.ic_stat_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.drawable.gradient_logo))
                    .setSound(Uri.parse("android.resource://"
                            + getApplicationContext().getPackageName() + "/" + R.raw.alarm))
                    .setContentTitle("Time To Study")
                    .setContentText("From: Study Companion")
                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                    .setAutoCancel(true)
                    .setSubText("Time to study")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Hello, it is time for you to study your " + schedule.getCourse() + " course. Lets get to it.").setSummaryText("Hello, it is time for you to study your \" + schedule.getCourse() + \" course. Lets get to it."))
                    .setOnlyAlertOnce(false);
            builder.setContentIntent(notificationPendingIntent);

            //set manager
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(schedule.getId(), builder.build());

        }

    }

    //24 hours = 86400000
    //6 hours = 21600000
    //2 hours = 7200000
    //30 minutes = 1800000
    //10 minutes = 600000
    //20 minutes = 1200000
    //5 minutes = 300000
    //1 minute = 60000

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
