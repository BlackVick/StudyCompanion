package esw.peeplo.studentstudycom.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import esw.peeplo.studentstudycom.converters.CourseConverter;
import esw.peeplo.studentstudycom.dao.UserDao;
import esw.peeplo.studentstudycom.models.User;

@Database(entities = User.class, version = 1, exportSchema = false)
@TypeConverters(CourseConverter.class)
public abstract class UserDatabase extends RoomDatabase {

    private static UserDatabase userDatabase;

    public static synchronized UserDatabase getUserDatabase(Context context){
        if (userDatabase == null){
            userDatabase = Room.databaseBuilder(
                    context,
                    UserDatabase.class,
                    "user_db"
            ).build();
        }
        return userDatabase;
    }

    public abstract UserDao userDao();

}

