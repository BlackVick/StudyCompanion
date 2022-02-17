package esw.peeplo.studentstudycom.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import esw.peeplo.studentstudycom.dao.RegistryDao;
import esw.peeplo.studentstudycom.dao.ScheduleDao;
import esw.peeplo.studentstudycom.models.CourseRegistry;

@Database(entities = CourseRegistry.class, version = 1, exportSchema = false)
public abstract class RegistryDatabase extends RoomDatabase {

    private static RegistryDatabase registryDatabase;

    public static synchronized RegistryDatabase getRegistryDatabase(Context context){
        if (registryDatabase == null){
            registryDatabase = Room.databaseBuilder(
                    context,
                    RegistryDatabase.class,
                    "registry_db"
            ).build();
        }
        return registryDatabase;
    }

    public abstract RegistryDao registryDao();

}

