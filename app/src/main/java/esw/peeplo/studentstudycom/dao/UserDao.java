package esw.peeplo.studentstudycom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import java.util.UUID;

import esw.peeplo.studentstudycom.models.Courses;
import esw.peeplo.studentstudycom.models.Schedule;
import esw.peeplo.studentstudycom.models.StudentEval;
import esw.peeplo.studentstudycom.models.User;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

@Dao
public interface UserDao {

    //add new user
    @Insert()
    void addNewUser(User file);

    //list all users
    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();

    //check if user exist
    @Query("SELECT * FROM users WHERE matric_num = :matric")
    User doesUserExist(String matric);

    //login user
    @Query("SELECT * FROM users WHERE matric_num = :matric AND password = :password")
    User loginUser(String matric, String password);

    //get current user
    @Query("SELECT * FROM users WHERE matric_num = :matric")
    User getCurrentUser(String matric);

    //update user courses
    @Query("UPDATE users SET courses = :courses WHERE id = :id AND matric_num = :matric")
    void updateUserCourses(int id, String matric, Courses courses);

    //update user evaluation
    @Query("UPDATE users SET study_style = :evaluation WHERE matric_num = :matric")
    void updateUserEvaluation(String matric, String evaluation);

    //delete user from list
    @Query("DELETE FROM users WHERE id = :id")
    void deleteUser(int id);

    //clear users list
    @Query("DELETE FROM users")
    void nukeTable();
}
