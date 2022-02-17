package esw.peeplo.studentstudycom.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import esw.peeplo.studentstudycom.databases.ScheduleDatabase;
import esw.peeplo.studentstudycom.databases.SearchDatabase;
import esw.peeplo.studentstudycom.models.Schedule;
import esw.peeplo.studentstudycom.models.Search;

public class ScheduleViewModel extends AndroidViewModel {

    private ScheduleDatabase database;

    public ScheduleViewModel(@NonNull Application application){
        super(application);

        database = ScheduleDatabase.getScheduleDatabase(application);
    }

    public void addNewSchedule(Schedule schedule){
        database.scheduleDao().addNewSchedule(schedule);
    }

    public LiveData<List<Schedule>> getUserSchedules(String matric){
        return database.scheduleDao().getAllUserSchedules(matric);
    }

    public LiveData<List<Schedule>> getCourseSchedules(String matric, String course){
        return database.scheduleDao().getAllUserCourseSchedules(matric, course);
    }

    public LiveData<List<Schedule>> doesScheduleExist(String matric, String course, String day){
        return database.scheduleDao().doesScheduleExist(matric, course, day);
    }

    public void removeSchedule(String matric, int id){
        database.scheduleDao().removeSchedule(matric, id);
    }

    public void nukeTable(){
        database.scheduleDao().nukeTable();
    }

}
