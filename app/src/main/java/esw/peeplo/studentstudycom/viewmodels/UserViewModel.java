package esw.peeplo.studentstudycom.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import esw.peeplo.studentstudycom.databases.UserDatabase;
import esw.peeplo.studentstudycom.models.Courses;
import esw.peeplo.studentstudycom.models.User;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

public class UserViewModel extends AndroidViewModel {

    private UserDatabase database;

    public UserViewModel(@NonNull Application application){
        super(application);

        database = UserDatabase.getUserDatabase(application);
    }

    public void addNewUser(User user){
        database.userDao().addNewUser(user);
    }

    public LiveData<List<User>> getAllUsers(){
        return database.userDao().getAllUsers();
    }

    public LiveData<User> doesUserExist(String matric){
        return database.userDao().doesUserExist(matric);
    }

    public LiveData<User> loginUser(String matric, String password){
        return database.userDao().loginUser(matric, password);
    }

    public LiveData<User> getCurrentUser(String matric){
        return database.userDao().getCurrentUser(matric);
    }

    public void updateUserCourses(int id, String matric, Courses courses){
        database.userDao().updateUserCourses(id, matric, courses);
    }

    public void updateUserEvaluation(String matric, String evaluation){
        database.userDao().updateUserEvaluation(matric, evaluation);
    }

    public void deleteUser(int id){
        database.userDao().deleteUser(id);
    }

    public void nukeTable(){
        database.userDao().nukeTable();
    }

}
