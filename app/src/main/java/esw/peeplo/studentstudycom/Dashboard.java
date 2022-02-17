package esw.peeplo.studentstudycom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import esw.peeplo.studentstudycom.adapters.CourseAdapter;
import esw.peeplo.studentstudycom.auth.Welcome;
import esw.peeplo.studentstudycom.databinding.ActivityDashboardBinding;
import esw.peeplo.studentstudycom.dialogs.ChoiceDialog;
import esw.peeplo.studentstudycom.dialogs.InfoDialog;
import esw.peeplo.studentstudycom.interfaces.ChoiceClickListener;
import esw.peeplo.studentstudycom.interfaces.CourseClickListener;
import esw.peeplo.studentstudycom.interfaces.InfoClickListener;
import esw.peeplo.studentstudycom.models.CourseRegistry;
import esw.peeplo.studentstudycom.models.User;
import esw.peeplo.studentstudycom.services.ScheduleService;
import esw.peeplo.studentstudycom.util.Common;
import esw.peeplo.studentstudycom.util.Methods;
import esw.peeplo.studentstudycom.viewmodels.RegistryViewModel;
import esw.peeplo.studentstudycom.viewmodels.UserViewModel;
import io.paperdb.Paper;

public class Dashboard extends AppCompatActivity implements InfoClickListener, ChoiceClickListener, CourseClickListener {

    //binding
    private ActivityDashboardBinding activity;

    //dialogs
    private InfoDialog infoDialog;
    private ChoiceDialog choiceDialog;

    //values
    private String matric;
    private User currentUser;
    private boolean isLogout = false;

    //course data
    private List<String> courseList = new ArrayList<>();
    private CourseAdapter adapter;

    //view models
    private UserViewModel userViewModel;
    private RegistryViewModel registryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        //values
        matric = Paper.book().read(Common.USER_ID);
        currentUser = Paper.book().read(Common.CURRENT_USER);

        //init
        initialize();
    }

    private void initialize() {

        //set defaults
        setDefaults();
        
        //load courses
        loadCourses();

        //profile menu
        activity.userAvatar.setOnClickListener(v -> loadProfileMenu());

    }

    private void loadCourses() {

        activity.coursesRecycler.setHasFixedSize(true);

        //smooth scroll
        activity.coursesRecycler.setNestedScrollingEnabled(false);

        //set adapter
        adapter = new CourseAdapter(courseList, this, this, this);
        activity.coursesRecycler.setAdapter(adapter);

        //fetch courses
        getCourses();

    }

    private void getCourses() {

        //populate list
        courseList.addAll(currentUser.getCourses().getCourses());
        adapter.notifyDataSetChanged();

    }

    @SuppressLint("RestrictedApi")
    private void loadProfileMenu() {

        //show popup menu
        PopupMenu popup = new PopupMenu(this, activity.userAvatar);
        popup.inflate(R.menu.profile_menu);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_profile:
                    //go to profile page
                    startActivity(new Intent(Dashboard.this, Profile.class));
                    Methods.slideLeft(Dashboard.this);
                    return true;

                case R.id.action_logout:
                    //state
                    isLogout = true;

                    //show error
                    String[] params = {
                            "Logout",
                            "Are you sure you want to logout of your Study Companion account?",
                            "No",
                            "Yes"
                    };
                    choiceDialog = new ChoiceDialog(Dashboard.this, Dashboard.this, params, Dashboard.this, false);
                    choiceDialog.showDialog();
                    return true;
                default:
                    return false;
            }
        });

        //popup helper
        MenuPopupHelper menuHelper = new MenuPopupHelper(this, (MenuBuilder) popup.getMenu(), activity.userAvatar);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();

    }

    private void setDefaults() {

        //view models
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        registryViewModel = new ViewModelProvider(this).get(RegistryViewModel.class);

        //set user
        activity.setCurrentUser(currentUser);

        //other evaluation
        registryViewModel.getUserRegistry(matric).observe(this, response -> {

            long totalMins = 0;

            for (CourseRegistry registry : response){

                totalMins += registry.getFocused();

            }

            activity.setStudied(new DecimalFormat("#.##").format((double) totalMins / 60));

        });

        //start schedule service
        if (!Methods.isServiceRunning(this, ScheduleService.class)) {
            Intent scheduleIntent = new Intent(this, ScheduleService.class);
            ContextCompat.startForegroundService(this, scheduleIntent);
        }

    }

    private void logout(){

        //stop schedule service
        if (Methods.isServiceRunning(this, ScheduleService.class)) {
            stopService(new Intent(this, ScheduleService.class));
        }

        //destroy local db
        Paper.book().destroy();

        //nuke
        //new Thread(() -> userViewModel.nukeTable()).start();

        //go to welcome
        Intent logoutIntent = new Intent(this, Welcome.class);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(logoutIntent);
        finish();
        Methods.slideRight(this);

    }

    @Override
    public void onBackPressed() {
        //finish
        finish();

        //close anim
        Methods.slideRight(this);
    }

    @Override
    public void onPositiveClick() {

        //close
        choiceDialog.cancelDialog();

        //logout
        logout();

    }

    @Override
    public void onNegativeClick() {

        //close
        choiceDialog.cancelDialog();

    }

    @Override
    public void onButtonClick() {

        //close
        infoDialog.cancelDialog();

    }

    @Override
    public void onCourseClick(String course) {

        Intent courseIntent = new Intent(this, CourseDetail.class);
        courseIntent.putExtra(Common.COURSE_DATA, course);
        startActivity(courseIntent);
        Methods.slideLeft(this);

    }
}