package esw.peeplo.studentstudycom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import esw.peeplo.studentstudycom.models.Schedule;
import esw.peeplo.studentstudycom.models.Search;

@Dao
public interface ScheduleDao {

    //add new schedule
    @Insert()
    void addNewSchedule(Schedule schedule);

    //list direct user schedules
    @Query("SELECT * FROM schedules WHERE owner = :matric")
    List<Schedule> getAllUserDirectSchedules(String matric);

    //list user schedules
    @Query("SELECT * FROM schedules WHERE owner = :matric")
    LiveData<List<Schedule>> getAllUserSchedules(String matric);

    //get all user schedules for course
    @Query("SELECT * FROM schedules WHERE owner = :matric AND course = :course")
    LiveData<List<Schedule>> getAllUserCourseSchedules(String matric, String course);

    //check if schedule exist
    @Query("SELECT * FROM schedules WHERE owner = :matric AND course = :course AND day = :day")
    LiveData<List<Schedule>> doesScheduleExist(String matric, String course, String day);

    //remove schedule
    @Query("DELETE FROM schedules WHERE owner = :matric AND id = :id")
    void removeSchedule(String matric, int id);

    //clear schedule list
    @Query("DELETE FROM schedules")
    void nukeTable();
}
