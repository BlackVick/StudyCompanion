package esw.peeplo.studentstudycom.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import esw.peeplo.studentstudycom.databases.SearchDatabase;
import esw.peeplo.studentstudycom.databases.UserDatabase;
import esw.peeplo.studentstudycom.models.Courses;
import esw.peeplo.studentstudycom.models.Search;
import esw.peeplo.studentstudycom.models.User;

public class SearchViewModel extends AndroidViewModel {

    private SearchDatabase database;

    public SearchViewModel(@NonNull Application application){
        super(application);

        database = SearchDatabase.getSearchDatabase(application);
    }

    public void addNewEntry(Search search){
        database.searchDao().addNewEntry(search);
    }

    public LiveData<List<Search>> getAllUserEntries(String matric){
        return database.searchDao().getAllUserEntries(matric);
    }

    public LiveData<List<Search>> getAllCourseEntries(String course){
        return database.searchDao().getAllCourseEntries(course);
    }

    public LiveData<Search> doesEntryExist(String param, String course){
        return database.searchDao().doesEntryExist(param, course);
    }

    public void clearMySearchHistory(String matric){
        database.searchDao().clearMySearchHistory(matric);
    }

    public void nukeTable(){
        database.searchDao().nukeTable();
    }

}
