package esw.peeplo.studentstudycom.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import esw.peeplo.studentstudycom.databases.RegistryDatabase;
import esw.peeplo.studentstudycom.databases.ScheduleDatabase;
import esw.peeplo.studentstudycom.models.CourseRegistry;
import esw.peeplo.studentstudycom.models.Schedule;

public class RegistryViewModel extends AndroidViewModel {

    private RegistryDatabase database;

    public RegistryViewModel(@NonNull Application application){
        super(application);

        database = RegistryDatabase.getRegistryDatabase(application);
    }

    public void addNewRegistry(CourseRegistry registry){
        database.registryDao().addNewRegistry(registry);
    }

    public LiveData<List<CourseRegistry>> getUserRegistry(String matric){
        return database.registryDao().getAllUserRegistry(matric);
    }

    public LiveData<List<CourseRegistry>> getCourseRegistry(String matric, String course){
        return database.registryDao().getAllUserCourseRegistry(matric, course);
    }

    public void nukeTable(){
        database.registryDao().nukeTable();
    }

}
