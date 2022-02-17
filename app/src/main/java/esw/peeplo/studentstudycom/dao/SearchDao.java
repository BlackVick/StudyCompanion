package esw.peeplo.studentstudycom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import esw.peeplo.studentstudycom.models.Courses;
import esw.peeplo.studentstudycom.models.Search;
import esw.peeplo.studentstudycom.models.User;

@Dao
public interface SearchDao {

    //add new entry
    @Insert()
    void addNewEntry(Search search);

    //list user entry
    @Query("SELECT * FROM searches WHERE owner = :matric")
    LiveData<List<Search>> getAllUserEntries(String matric);

    //get all entries for course
    @Query("SELECT * FROM searches WHERE course = :course")
    LiveData<List<Search>> getAllCourseEntries(String course);

    //check if entry exist
    @Query("SELECT * FROM searches WHERE params = :param AND course = :course")
    LiveData<Search> doesEntryExist(String param, String course);

    //clear my search history
    @Query("DELETE FROM searches WHERE owner = :matric")
    void clearMySearchHistory(String matric);

    //clear search list
    @Query("DELETE FROM searches")
    void nukeTable();
}
