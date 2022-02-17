package esw.peeplo.studentstudycom.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import esw.peeplo.studentstudycom.dao.SearchDao;
import esw.peeplo.studentstudycom.dao.UserDao;
import esw.peeplo.studentstudycom.models.Search;

@Database(entities = Search.class, version = 1, exportSchema = false)
public abstract class SearchDatabase extends RoomDatabase {

    private static SearchDatabase searchDatabase;

    public static synchronized SearchDatabase getSearchDatabase(Context context){
        if (searchDatabase == null){
            searchDatabase = Room.databaseBuilder(
                    context,
                    SearchDatabase.class,
                    "search_db"
            ).build();
        }
        return searchDatabase;
    }

    public abstract SearchDao searchDao();

}

