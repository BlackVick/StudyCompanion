package esw.peeplo.studentstudycom.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import esw.peeplo.studentstudycom.Dashboard;
import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.databinding.ActivityLoginBinding;
import esw.peeplo.studentstudycom.dialogs.InfoDialog;
import esw.peeplo.studentstudycom.interfaces.InfoClickListener;
import esw.peeplo.studentstudycom.models.Courses;
import esw.peeplo.studentstudycom.models.User;
import esw.peeplo.studentstudycom.services.ScheduleService;
import esw.peeplo.studentstudycom.util.Common;
import esw.peeplo.studentstudycom.util.Methods;
import esw.peeplo.studentstudycom.viewmodels.UserViewModel;
import io.paperdb.Paper;

public class Login extends AppCompatActivity implements InfoClickListener {

    //binding
    private ActivityLoginBinding activity;

    //dialogs
    private InfoDialog infoDialog;

    //room
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = DataBindingUtil.setContentView(this, R.layout.activity_login);

        //init
        initialize();
    }

    private void initialize() {

        //set defaults
        setDefaults();

        //back
        activity.backButton.setOnClickListener(v -> onBackPressed());

        //register
        activity.registerLink.setOnClickListener(v -> {
            startActivity(new Intent(this, Register.class));
            finish();
            Methods.slideRight(this);
        });

        //login
        activity.loginBtn.setOnClickListener(v -> {
            validateParams();
        });

    }

    private void setDefaults() {

        //setup view model
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        //loading
        activity.setIsLoading(false);
    }

    private void validateParams() {

        String matric = activity.userMatric.getText().toString().trim();
        String pass = activity.userPassword.getText().toString().trim();

        if (TextUtils.isEmpty(matric)){

            activity.userMatric.requestFocus();
            activity.userMatric.setError("required");

        } else

        if (TextUtils.isEmpty(pass)){

            activity.userPassword.requestFocus();
            activity.userPassword.setError("required");

        } else {

            loginUser(matric, pass);

        }
    }

    private void loginUser(String matric, String pass) {

        //start loading
        activity.setIsLoading(true);

        //check if user exist
        new Thread(() -> {

            User existingUser = userViewModel.doesUserExist(matric);

            if (existingUser != null){

                //login
                User loggedInUser = userViewModel.loginUser(matric, pass);

                if (loggedInUser != null) {

                    //process data
                    processData(loggedInUser);

                } else {

                    //stop loading
                    activity.setIsLoading(false);

                    //show error
                    String[] params = {"Password", "You have provided a wrong password for user. Try again", "Okay"};
                    infoDialog = new InfoDialog(this, this, params, this);
                    infoDialog.showDialog();

                }

            } else {

                //stop loading
                activity.setIsLoading(false);

                //show error
                String[] params = {"No User", "No student with this matric number (" + matric + ") exists in the database.", "Okay"};
                infoDialog = new InfoDialog(this, this, params, this);
                infoDialog.showDialog();

            }

        }).start();

    }

    private void processData(User loggedInUser) {

        //stop loading
        activity.setIsLoading(false);

        //save to local
        Paper.book().write(Common.USER_ID, loggedInUser.getMatric_num());
        Paper.book().write(Common.CURRENT_USER, loggedInUser);

        //start schedule service
        if (!Methods.isServiceRunning(this, ScheduleService.class)) {
            Intent scheduleIntent = new Intent(this, ScheduleService.class);
            ContextCompat.startForegroundService(this, scheduleIntent);
        }

        //check if student has evaluation
        if (TextUtils.isEmpty(loggedInUser.getStudy_style())){

            //evaluate study style
            Intent studyStyleIntent = new Intent(this, StudyStyleEvaluation.class);
            studyStyleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(studyStyleIntent);
            finish();
            Methods.slideLeft(this);

        } else {

            //go home
            Intent homeIntent = new Intent(this, Dashboard.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
            Methods.slideLeft(this);

        }

    }

    @Override
    public void onBackPressed() {
        //close activity
        finish();

        //closing animation
        Methods.slideRight(this);
    }

    @Override
    public void onButtonClick() {
        infoDialog.cancelDialog();
    }
}