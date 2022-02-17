package esw.peeplo.studentstudycom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import esw.peeplo.studentstudycom.models.CourseRegistry;
import esw.peeplo.studentstudycom.models.Schedule;

@Dao
public interface RegistryDao {

    //add new registry
    @Insert()
    void addNewRegistry(CourseRegistry registry);

    //list user registry
    @Query("SELECT * FROM registry WHERE owner = :matric")
    LiveData<List<CourseRegistry>> getAllUserRegistry(String matric);

    //get all user registry for course
    @Query("SELECT * FROM registry WHERE owner = :matric AND course = :course")
    LiveData<List<CourseRegistry>> getAllUserCourseRegistry(String matric, String course);

    //clear registry
    @Query("DELETE FROM registry")
    void nukeTable();
}
