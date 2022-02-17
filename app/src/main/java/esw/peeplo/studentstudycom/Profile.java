package esw.peeplo.studentstudycom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import esw.peeplo.studentstudycom.adapters.ScheduleAdapter;
import esw.peeplo.studentstudycom.adapters.SearchAdapter;
import esw.peeplo.studentstudycom.databinding.ActivityProfileBinding;
import esw.peeplo.studentstudycom.models.CourseRegistry;
import esw.peeplo.studentstudycom.models.Schedule;
import esw.peeplo.studentstudycom.models.Search;
import esw.peeplo.studentstudycom.models.User;
import esw.peeplo.studentstudycom.util.Common;
import esw.peeplo.studentstudycom.util.Methods;
import esw.peeplo.studentstudycom.viewmodels.RegistryViewModel;
import esw.peeplo.studentstudycom.viewmodels.ScheduleViewModel;
import esw.peeplo.studentstudycom.viewmodels.SearchViewModel;
import io.paperdb.Paper;

public class Profile extends AppCompatActivity {

    //binding
    private ActivityProfileBinding activity;

    //values
    private String matric;
    private User currentUser;

    //view models
    private SearchViewModel searchViewModel;
    private ScheduleViewModel scheduleViewModel;
    private RegistryViewModel registryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        //values
        matric = Paper.book().read(Common.USER_ID);
        currentUser = Paper.book().read(Common.CURRENT_USER);

        //init
        initialize();
    }

    private void initialize() {

        //set defaults
        setDefaults();

        //back
        activity.backButton.setOnClickListener(v -> onBackPressed());

    }

    private void setDefaults() {

        //view models
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        registryViewModel = new ViewModelProvider(this).get(RegistryViewModel.class);

        //set current user
        activity.setCurrentUser(currentUser);

        //search count
        searchViewModel.getAllUserEntries(matric).observe(this, response -> {

            activity.setSearches(response.size());

        });

        //other evaluation
        registryViewModel.getUserRegistry(matric).observe(this, response -> {

            long totalMins = 0;
            long focused = 0;
            long distracted = 0;

            for (CourseRegistry registry : response){

                totalMins += registry.getFocused();
                focused += registry.getFocused();
                distracted += registry.getDistracted();

            }

            long totalHours = focused + distracted;

            //hours studied
            activity.setStudied(new DecimalFormat("#.##").format((double) totalMins / 60));

            //indexes
            if (totalHours > 0) {
                double focusPercentage = (double) ((focused / totalHours) * 100);
                double distractPercentage = (double) ((distracted / totalHours) * 100);

                activity.setConcentration(new DecimalFormat("#.##").format(focusPercentage));
                activity.setDistraction(new DecimalFormat("#.##").format(distractPercentage));
            } else {
                activity.setConcentration(new DecimalFormat("#.##").format(0));
                activity.setDistraction(new DecimalFormat("#.##").format(0));
            }

        });

    }

    @Override
    public void onBackPressed() {
        //close
        finish();

        //closing anim
        Methods.slideRight(this);
    }
}