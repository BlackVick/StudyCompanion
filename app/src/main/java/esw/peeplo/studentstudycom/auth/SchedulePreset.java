package esw.peeplo.studentstudycom.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import esw.peeplo.studentstudycom.CourseDetail;
import esw.peeplo.studentstudycom.Dashboard;
import esw.peeplo.studentstudycom.Profile;
import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.adapters.CourseAdapter;
import esw.peeplo.studentstudycom.adapters.SchedulePresetAdapter;
import esw.peeplo.studentstudycom.databinding.ActivitySchedulePresetBinding;
import esw.peeplo.studentstudycom.dialogs.ChoiceDialog;
import esw.peeplo.studentstudycom.dialogs.InfoDialog;
import esw.peeplo.studentstudycom.interfaces.InfoClickListener;
import esw.peeplo.studentstudycom.models.CourseRegistry;
import esw.peeplo.studentstudycom.models.User;
import esw.peeplo.studentstudycom.services.ScheduleService;
import esw.peeplo.studentstudycom.util.Common;
import esw.peeplo.studentstudycom.util.Methods;
import esw.peeplo.studentstudycom.viewmodels.RegistryViewModel;
import esw.peeplo.studentstudycom.viewmodels.UserViewModel;
import io.paperdb.Paper;

public class SchedulePreset extends AppCompatActivity implements InfoClickListener {

    //binding
    private ActivitySchedulePresetBinding activity;

    //dialogs
    private InfoDialog infoDialog;

    //values
    private String matric;
    private User currentUser;

    //course data
    private List<String> courseList = new ArrayList<>();
    private SchedulePresetAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = DataBindingUtil.setContentView(this, R.layout.activity_schedule_preset);

        //values
        matric = Paper.book().read(Common.USER_ID);
        currentUser = Paper.book().read(Common.CURRENT_USER);

        //init
        initialize();
    }

    private void initialize() {

        //show info
        showInformation();

        //load courses
        loadCourses();

        //back
        activity.backButton.setOnClickListener(v -> onBackPressed());

        //continue
        activity.continueBtn.setOnClickListener(v -> {

            startActivity(new Intent(this, Dashboard.class));
            finish();
            Methods.slideLeft(this);

        });

    }

    private void showInformation() {

        String[] params = {"Schedule Preset", "We have preset your course study schedules based on your study style evaluation. Please note that, new schedules can be set manually in app.", "Okay"};
        infoDialog = new InfoDialog(this, this, params, this);
        infoDialog.showDialog();

    }

    private void loadCourses() {

        activity.coursesRecycler.setHasFixedSize(true);

        //smooth scroll
        activity.coursesRecycler.setNestedScrollingEnabled(false);

        //set adapter
        adapter = new SchedulePresetAdapter(courseList, this, this);
        activity.coursesRecycler.setAdapter(adapter);

        //fetch courses
        getCourses();

    }

    private void getCourses() {

        //populate list
        courseList.addAll(currentUser.getCourses().getCourses());
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        //finish
        finish();

        //close anim
        Methods.slideRight(this);
    }

    @Override
    public void onButtonClick() {

        //close
        infoDialog.cancelDialog();

    }

}