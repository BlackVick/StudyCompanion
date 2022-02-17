package esw.peeplo.studentstudycom.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import esw.peeplo.studentstudycom.dao.ScheduleDao;
import esw.peeplo.studentstudycom.dao.SearchDao;
import esw.peeplo.studentstudycom.models.Schedule;

@Database(entities = Schedule.class, version = 1, exportSchema = false)
public abstract class ScheduleDatabase extends RoomDatabase {

    private static ScheduleDatabase scheduleDatabase;

    public static synchronized ScheduleDatabase getScheduleDatabase(Context context){
        if (scheduleDatabase == null){
            scheduleDatabase = Room.databaseBuilder(
                    context,
                    ScheduleDatabase.class,
                    "schedule_db"
            ).build();
        }
        return scheduleDatabase;
    }

    public abstract ScheduleDao scheduleDao();

}

