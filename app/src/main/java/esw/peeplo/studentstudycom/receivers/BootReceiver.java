package esw.peeplo.studentstudycom.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import esw.peeplo.studentstudycom.services.ScheduleService;
import esw.peeplo.studentstudycom.util.Common;
import esw.peeplo.studentstudycom.util.Methods;
import io.paperdb.Paper;

public class BootReceiver extends BroadcastReceiver {

    //user
    private String currentUser = Paper.book().read(Common.USER_ID);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (currentUser != null && !TextUtils.isEmpty(currentUser)) {

                //start schedule service
                if (!Methods.isServiceRunning(context, ScheduleService.class)) {
                    Intent scheduleIntent = new Intent(context, ScheduleService.class);
                    ContextCompat.startForegroundService(context, scheduleIntent);
                }

            }
        }
    }
}