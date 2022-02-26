package esw.peeplo.studentstudycom;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import esw.peeplo.studentstudycom.adapters.ScheduleAdapter;
import esw.peeplo.studentstudycom.adapters.SearchAdapter;
import esw.peeplo.studentstudycom.auth.Register;
import esw.peeplo.studentstudycom.databinding.ActivityCourseDetailBinding;
import esw.peeplo.studentstudycom.databinding.EditScheduleDialogBinding;
import esw.peeplo.studentstudycom.databinding.ScheduleDialogBinding;
import esw.peeplo.studentstudycom.dialogs.ChoiceDialog;
import esw.peeplo.studentstudycom.dialogs.InfoDialog;
import esw.peeplo.studentstudycom.helpers.StudyHelper;
import esw.peeplo.studentstudycom.interfaces.ChoiceClickListener;
import esw.peeplo.studentstudycom.interfaces.InfoClickListener;
import esw.peeplo.studentstudycom.interfaces.SearchClickListener;
import esw.peeplo.studentstudycom.models.CourseRegistry;
import esw.peeplo.studentstudycom.models.Schedule;
import esw.peeplo.studentstudycom.models.Search;
import esw.peeplo.studentstudycom.models.User;
import esw.peeplo.studentstudycom.services.ScheduleService;
import esw.peeplo.studentstudycom.util.Common;
import esw.peeplo.studentstudycom.util.Methods;
import esw.peeplo.studentstudycom.viewmodels.RegistryViewModel;
import esw.peeplo.studentstudycom.viewmodels.ScheduleViewModel;
import esw.peeplo.studentstudycom.viewmodels.SearchViewModel;
import io.paperdb.Paper;

public class CourseDetail extends AppCompatActivity implements InfoClickListener, ChoiceClickListener, SearchClickListener {

    //binding
    private ActivityCourseDetailBinding activity;

    //dialogs
    private InfoDialog infoDialog;
    private ChoiceDialog choiceDialog;
    private AlertDialog scheduleDialog;

    //values
    private String matric;
    private User currentUser;
    private String course;

    //course data
    private List<Search> tempSearchList = new ArrayList<>();
    private List<Search> searchList = new ArrayList<>();
    private SearchAdapter searchAdapter;

    //schedule data
    private List<Schedule> scheduleList = new ArrayList<>();
    private ScheduleAdapter scheduleAdapter;

    //view models
    private SearchViewModel searchViewModel;
    private ScheduleViewModel scheduleViewModel;
    private RegistryViewModel registryViewModel;

    //study
    private boolean isSearching = false;
    private StudyHelper helper;

    //notification
    NotificationManager notificationManager;
    private static final int VERIFY_PERMISSIONS_REQUEST = 757;
    private boolean isPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = DataBindingUtil.setContentView(this, R.layout.activity_course_detail);

        //values
        matric = Paper.book().read(Common.USER_ID);
        currentUser = Paper.book().read(Common.CURRENT_USER);
        course = getIntent().getStringExtra(Common.COURSE_DATA);
        helper = Paper.book().read(Common.STUDY_HELP);

        //init
        initialize();
    }

    private void initialize() {

        //set defaults
        setDefaults();

        //load searches
        loadSearches();

        //load schedules
        loadSchedules();

        //check permissions
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager.isNotificationPolicyAccessGranted()){
            isPermissionGranted = true;
        } else {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS), 0);
        }

        //back
        activity.backButton.setOnClickListener(v -> onBackPressed());

        //elastic search
        activity.searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!TextUtils.isEmpty(s.toString().trim())){

                    //clear list
                    searchList.clear();

                    checkSearchList(s.toString());

                } else {

                    //clear list
                    searchList.clear();
                    searchAdapter.notifyDataSetChanged();

                }

            }
        });

        //search
        activity.searchBtn.setOnClickListener(v -> {

            validate();

        });

        //add schedules
        activity.addBtn.setOnClickListener(v -> loadScheduleDialog());

        //start or stop studying
        activity.studyBtn.setOnClickListener(v -> {

            if (activity.getIsStudying()){

                //stop studying
                stopStudying();

            } else {

                //start studying
                startStudying();

            }

            //set state
            activity.setIsStudying(!activity.getIsStudying());

        });

    }

    private void setDefaults() {

        //studying
        activity.setIsStudying(helper != null);

        //view models
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        registryViewModel = new ViewModelProvider(this).get(RegistryViewModel.class);

        //set current course
        activity.setCourse(course);

        //search count
        searchViewModel.getAllUserEntries(matric).observe(this, response -> {

            activity.setSearches(response.size());

        });

        //other evaluation
        registryViewModel.getCourseRegistry(matric, course).observe(this, response -> {

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK){

            if (notificationManager.isNotificationPolicyAccessGranted()){
                isPermissionGranted = true;
            }

        }

    }

    private void validate() {

        String param = activity.searchEdt.getText().toString().trim();

        if (TextUtils.isEmpty(param)){

            activity.searchEdt.requestFocus();
            activity.searchEdt.setError("empty query!");

        } else {

            getSearchResult(param);

        }

    }

    private void getSearchResult(String param) {

        //state
        isSearching = true;

        //sort
        searchViewModel.doesEntryExist(param, course).observe(this, search -> {

            if (search == null){

                //create search
                Search newSearch = new Search(matric, course, param);

                //add new entry
                new Thread(() -> searchViewModel.addNewEntry(newSearch)).start();

            }

        });

        //build link
        String formattedQuery = param.replaceAll(" ", "+");
        String searchLink = "https://www.google.com/search?q=" + formattedQuery;

        //get search results
        Intent searchIntent = new Intent(this, QuerySearch.class);
        searchIntent.putExtra(Common.SEARCH_DATA, searchLink);
        startActivity(searchIntent);
        Methods.slideLeft(this);

    }

    private void checkSearchList(String word) {

        List<Search> tempList = tempSearchList.stream().filter((p) -> p.getParams().toLowerCase().contains(word.toLowerCase())).collect(Collectors.toList());
        Log.d("Searches", new Gson().toJson(searchList));
        searchList.clear();
        searchList.addAll(tempList);
        searchAdapter.notifyDataSetChanged();

    }

    private void loadScheduleDialog() {

        //bind dialog
        scheduleDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        ScheduleDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout. schedule_dialog, null, false);
        scheduleDialog.setView(binding.getRoot());

        //add view properties
        scheduleDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        scheduleDialog.getWindow().setGravity(Gravity.BOTTOM);
        scheduleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = scheduleDialog.getWindow().getAttributes();
        scheduleDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        scheduleDialog.show();

        //default
        binding.setIsLoading(false);
        binding.setIsSun(false);
        binding.setIsMon(false);
        binding.setIsTue(false);
        binding.setIsWed(false);
        binding.setIsThu(false);
        binding.setIsFri(false);
        binding.setIsSat(false);

        //close
        binding.closeDialog.setOnClickListener(v -> {

            //dismiss dialog
            scheduleDialog.dismiss();

        });

        //day select
        binding.sunButton.setOnClickListener(v -> binding.setIsSun(!binding.getIsSun()));
        binding.monButton.setOnClickListener(v -> binding.setIsMon(!binding.getIsMon()));
        binding.tueButton.setOnClickListener(v -> binding.setIsTue(!binding.getIsTue()));
        binding.wedButton.setOnClickListener(v -> binding.setIsWed(!binding.getIsWed()));
        binding.thuButton.setOnClickListener(v -> binding.setIsThu(!binding.getIsThu()));
        binding.friButton.setOnClickListener(v -> binding.setIsFri(!binding.getIsFri()));
        binding.satButton.setOnClickListener(v -> binding.setIsSat(!binding.getIsSat()));

        //start time
        binding.startTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(CourseDetail.this, (timePicker, selectedHour, selectedMinute) -> {
                binding.startTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }, hour, minute, true);
            mTimePicker.setTitle("Select Start Time");
            mTimePicker.show();
        });

        //stop time
        binding.stopTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(CourseDetail.this, (timePicker, selectedHour, selectedMinute) -> {
                binding.stopTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }, hour, minute, true);
            mTimePicker.setTitle("Select Stop Time");
            mTimePicker.show();
        });

        //add
        binding.addBtn.setOnClickListener(v -> {

            //validate
            String start = binding.startTime.getText().toString().trim();
            String stop = binding.stopTime.getText().toString().trim();

            //check
            if (start.equals("CLICK TO SET TIME")){

                Toast.makeText(this, "Set Start Time", Toast.LENGTH_SHORT).show();

            } else

            if (stop.equals("CLICK TO SET TIME")){

                Toast.makeText(this, "Set Stop Time", Toast.LENGTH_SHORT).show();

            } else

            if (!binding.getIsSun() && !binding.getIsMon() && !binding.getIsTue() && !binding.getIsWed() && !binding.getIsThu() && !binding.getIsFri() && !binding.getIsSat()){

                Toast.makeText(this, "Please select days for scheduler to remind you", Toast.LENGTH_LONG).show();

            } else {

                if (binding.getIsSun()){

                    //create schedule
                    Schedule newSchedule = new Schedule(matric, course, Common.DAY_SUN, start, stop);

                    //check if it overlaps
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        if (!doesTimeFallWithin(newSchedule)) {
                            //add schedule
                            new Thread(() -> {scheduleViewModel.addNewSchedule(newSchedule);}).start();
                        } else {
                            Toast.makeText(this, "Schedule Entries that overlap with other set schedule times have been ignored.", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        //add schedule
                        new Thread(() -> {scheduleViewModel.addNewSchedule(newSchedule);}).start();

                    }


                }

                if (binding.getIsMon()){

                    //create schedule
                    Schedule newSchedule = new Schedule(matric, course, Common.DAY_MON, start, stop);

                    //check if it overlaps
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        if (!doesTimeFallWithin(newSchedule)) {
                            //add schedule
                            new Thread(() -> {scheduleViewModel.addNewSchedule(newSchedule);}).start();
                        } else {
                            Toast.makeText(this, "Schedule Entries that overlap with other set schedule times have been ignored.", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        //add schedule
                        new Thread(() -> {scheduleViewModel.addNewSchedule(newSchedule);}).start();

                    }


                }

                if (binding.getIsTue()){

                    //create schedule
                    Schedule newSchedule = new Schedule(matric, course, Common.DAY_TUE, start, stop);

                    //check if it overlaps
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        if (!doesTimeFallWithin(newSchedule)) {
                            //add schedule
                            new Thread(() -> {scheduleViewModel.addNewSchedule(newSchedule);}).start();
                        } else {
                            Toast.makeText(this, "Schedule Entries that overlap with other set schedule times have been ignored.", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        //add schedule
                        new Thread(() -> {scheduleViewModel.addNewSchedule(newSchedule);}).start();

                    }


                }

                if (binding.getIsWed()){

                    //create schedule
                    Schedule newSchedule = new Schedule(matric, course, Common.DAY_WED, start, stop);

                    //check if it overlaps
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        if (!doesTimeFallWithin(newSchedule)) {
                            //add schedule
                            new Thread(() -> {scheduleViewModel.addNewSchedule(newSchedule);}).start();
                        } else {
                            Toast.makeText(this, "Schedule Entries that overlap with other set schedule times have been ignored.", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        //add schedule
                        new Thread(() -> {scheduleViewModel.addNewSchedule(newSchedule);}).start();

                    }


                }

                if (binding.getIsThu()){

                    //create schedule
                    Schedule newSchedule = new Schedule(matric, course, Common.DAY_THU, start, stop);

                    //check if it overlaps
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        if (!doesTimeFallWithin(newSchedule)) {
                            //add schedule
                            new Thread(() -> {scheduleViewModel.addNewSchedule(newSchedule);}).start();
                        } else {
                            Toast.makeText(this, "Schedule Entries that overlap with other set schedule times have been ignored.", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        //add schedule
                        new Thread(() -> {scheduleViewModel.addNewSchedule(newSchedule);}).start();

                    }


                }

                if (binding.getIsFri()){

                    //create schedule
                    Schedule newSchedule = new Schedule(matric, course, Common.DAY_FRI, start, stop);

                    //check if it overlaps
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        if (!doesTimeFallWithin(newSchedule)) {
                            //add schedule
                            new Thread(() -> {scheduleViewModel.addNewSchedule(newSchedule);}).start();
                        } else {
                            Toast.makeText(this, "Schedule Entries that overlap with other set schedule times have been ignored.", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        //add schedule
                        new Thread(() -> {scheduleViewModel.addNewSchedule(newSchedule);}).start();

                    }


                }

                if (binding.getIsSat()){

                    //create schedule
                    Schedule newSchedule = new Schedule(matric, course, Common.DAY_SAT, start, stop);

                    //check if it overlaps
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        if (!doesTimeFallWithin(newSchedule)) {
                            //add schedule
                            new Thread(() -> {scheduleViewModel.addNewSchedule(newSchedule);}).start();
                        } else {
                            Toast.makeText(this, "Schedule Entries that overlap with other set schedule times have been ignored.", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        //add schedule
                        new Thread(() -> {scheduleViewModel.addNewSchedule(newSchedule);}).start();

                    }


                }

                //clear data
                scheduleList.clear();

                //fetch list again
                fetchAllSchedules();

                //close dialog
                scheduleDialog.dismiss();

                //stop schedule service
                if (Methods.isServiceRunning(this, ScheduleService.class)) {
                    stopService(new Intent(this, ScheduleService.class));
                }

                //start schedule service
                if (!Methods.isServiceRunning(this, ScheduleService.class)) {
                    Intent scheduleIntent = new Intent(this, ScheduleService.class);
                    ContextCompat.startForegroundService(this, scheduleIntent);
                }

            }

        });

    }

    private boolean doesTimeFallWithin(Schedule schedule){

        //result
        boolean result = false;

        ZoneId zone = ZoneId.of("Africa/Lagos");
        ZonedDateTime zdt = ZonedDateTime.now(zone).plusDays(2).plusMinutes(30);

        for (Schedule saved : scheduleList) {

            if (saved.getDay().equals(schedule.getDay())) {

                LocalTime rangeStart = LocalTime.parse(saved.getStart());
                LocalTime rangeEnd = LocalTime.parse(saved.getStop());

                LocalTime newStart = LocalTime.parse(schedule.getStart());
                LocalTime newEnd = LocalTime.parse(schedule.getStop());
                if ((!newStart.isBefore(rangeStart) && newStart.isBefore(rangeEnd)) || (!newEnd.isBefore(rangeStart) && newEnd.isBefore(rangeEnd))) {
                    result = true;
                    break;
                }

            }

        }

        return result;

    }

    private boolean doesUpdateTimeFallWithin(Schedule schedule){

        //result
        boolean result = false;

        for (Schedule saved : scheduleList) {

            if (saved.getDay().equals(schedule.getDay()) && (saved.getId() < schedule.getId() || saved.getId() > schedule.getId())) {

                LocalTime rangeStart = LocalTime.parse(saved.getStart());
                LocalTime rangeEnd = LocalTime.parse(saved.getStop());

                LocalTime newStart = LocalTime.parse(schedule.getStart());
                LocalTime newEnd = LocalTime.parse(schedule.getStop());
                if ((!newStart.isBefore(rangeStart) && newStart.isBefore(rangeEnd)) || (!newEnd.isBefore(rangeStart) && newEnd.isBefore(rangeEnd))) {
                    result = true;
                    break;
                }

            }

        }

        return result;

    }

    private void loadSearches() {

        activity.searchRecycler.setHasFixedSize(true);

        //smooth scroll
        activity.searchRecycler.setNestedScrollingEnabled(false);

        //set adapter
        searchAdapter = new SearchAdapter(searchList, this, this, this);
        activity.searchRecycler.setAdapter(searchAdapter);

        //fetch all entries
        fetchAllEntries();

    }

    private void fetchAllEntries() {

        //get full course search list
        searchViewModel.getAllCourseEntries(course).observe(this, response -> {

            tempSearchList.addAll(response);

            Log.d("Searches", new Gson().toJson(tempSearchList));

        });

    }

    private void loadSchedules() {

        activity.scheduleRecycler.setHasFixedSize(true);

        //smooth scroll
        activity.scheduleRecycler.setNestedScrollingEnabled(false);

        //set adapter
        scheduleAdapter = new ScheduleAdapter(scheduleList, this, this);
        activity.scheduleRecycler.setAdapter(scheduleAdapter);

        //fetch all schedules
        fetchAllSchedules();

    }

    private void fetchAllSchedules() {

        scheduleViewModel.getCourseSchedules(matric, course).observe(this, response -> {

            for (Schedule schedule : response) {

                if (!scheduleList.contains(schedule)) {
                    scheduleList.add(schedule);
                }

            }
            scheduleAdapter.notifyDataSetChanged();

        });

    }

    public void loadEditScheduleDialog(Schedule schedule) {

        //bind dialog
        scheduleDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        EditScheduleDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.edit_schedule_dialog, null, false);
        scheduleDialog.setView(binding.getRoot());

        //add view properties
        scheduleDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        scheduleDialog.getWindow().setGravity(Gravity.BOTTOM);
        scheduleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = scheduleDialog.getWindow().getAttributes();
        scheduleDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        scheduleDialog.show();

        //default
        binding.setIsLoading(false);
        binding.setIsSun(schedule.getDay().equals(Common.DAY_SUN));
        binding.setIsMon(schedule.getDay().equals(Common.DAY_MON));
        binding.setIsTue(schedule.getDay().equals(Common.DAY_TUE));
        binding.setIsWed(schedule.getDay().equals(Common.DAY_WED));
        binding.setIsThu(schedule.getDay().equals(Common.DAY_THU));
        binding.setIsFri(schedule.getDay().equals(Common.DAY_FRI));
        binding.setIsSat(schedule.getDay().equals(Common.DAY_SAT));
        binding.startTime.setText(schedule.getStart());
        binding.stopTime.setText(schedule.getStop());

        //close
        binding.closeDialog.setOnClickListener(v -> {

            //dismiss dialog
            scheduleDialog.dismiss();

        });

        //start time
        binding.startTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(CourseDetail.this, (timePicker, selectedHour, selectedMinute) -> {
                binding.startTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }, hour, minute, true);
            mTimePicker.setTitle("Select Start Time");
            mTimePicker.show();
        });

        //stop time
        binding.stopTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(CourseDetail.this, (timePicker, selectedHour, selectedMinute) -> {
                binding.stopTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }, hour, minute, true);
            mTimePicker.setTitle("Select Stop Time");
            mTimePicker.show();
        });

        //add
        binding.updateBtn.setOnClickListener(v -> {

            //validate
            String start = binding.startTime.getText().toString().trim();
            String stop = binding.stopTime.getText().toString().trim();

            //check
            if (start.equals("CLICK TO SET TIME")){

                Toast.makeText(this, "Set Start Time", Toast.LENGTH_SHORT).show();

            } else

            if (stop.equals("CLICK TO SET TIME")){

                Toast.makeText(this, "Set Stop Time", Toast.LENGTH_SHORT).show();

            } else {

                //create schedule
                schedule.setStart(start);
                schedule.setStop(stop);

                //check if it overlaps
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    if (!doesUpdateTimeFallWithin(schedule)) {
                        //add schedule
                        new Thread(() -> {scheduleViewModel.updateSchedule(start, stop, schedule.getId());}).start();
                    } else {
                        Toast.makeText(this, "Schedule Entry time overlaps with other set schedule time.", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    //add schedule
                    new Thread(() -> {scheduleViewModel.updateSchedule(start, stop, schedule.getId());}).start();

                }

                //clear data
                scheduleList.clear();

                //fetch list again
                fetchAllSchedules();

                //close dialog
                scheduleDialog.dismiss();

                //stop schedule service
                if (Methods.isServiceRunning(this, ScheduleService.class)) {
                    stopService(new Intent(this, ScheduleService.class));
                }

                //start schedule service
                if (!Methods.isServiceRunning(this, ScheduleService.class)) {
                    Intent scheduleIntent = new Intent(this, ScheduleService.class);
                    ContextCompat.startForegroundService(this, scheduleIntent);
                }

            }

        });

    }

    public void deleteSchedule(Schedule schedule, int position){

        new Thread(() -> {

            //delete schedule
            scheduleViewModel.removeSchedule(matric, schedule.getId());

            //remove from list
            runOnUiThread(() -> {
                scheduleList.remove(position);
                scheduleAdapter.notifyDataSetChanged();
            });

        }).start();

    }

    @Override
    public void onBackPressed() {

        //helper
        if (helper != null){
            isSearching = false;
        }

        //finish
        startActivity(new Intent(this, Dashboard.class));
        finish();

        //close anim
        Methods.slideRight(this);
    }

    @Override
    public void onPositiveClick() {

        //close
        choiceDialog.cancelDialog();

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
    public void onSearchClick(String search) {

        activity.searchEdt.setText(search);

    }

    private void startStudying() {


        helper = new StudyHelper(matric, course, getCurrentSchedule(), true, System.currentTimeMillis(), 0);
        Paper.book().write(Common.STUDY_HELP, helper);

        //block notifications
        lockNotification(true);

    }

    private void stopStudying() {

        if (helper != null){

            helper.setStop(System.currentTimeMillis());

            //get minutes
            long minutesFocused = helper.getStop()/(60 * 1000 ) - helper.getStart()/(60 * 1000);

            //create registry
            CourseRegistry newReg = new CourseRegistry(matric, course, helper.getSchedule(), 0, minutesFocused);

            //save registry
            new Thread(() -> registryViewModel.addNewRegistry(newReg)).start();

            //delete save
            Paper.book().delete(Common.STUDY_HELP);

            //clear helper
            helper = null;
        }

        //release notifications
        lockNotification(false);

    }

    private void lockNotification(boolean lock){

        if (isPermissionGranted) {

            if (lock){

                int currentFilter = notificationManager.getCurrentInterruptionFilter();
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                notificationManager.setInterruptionFilter(currentFilter);

            } else {

                int currentFilter = notificationManager.getCurrentInterruptionFilter();
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                notificationManager.setInterruptionFilter(currentFilter);

            }

        }

    }

    private String getCurrentSchedule() {

        //result
        String result = Common.REG_UNSCHEDULED;

        //set zone
        ZoneId zone = ZoneId.of("Africa/Lagos");

        //loop
        for (Schedule theSchedule : scheduleList){

            LocalTime rangeStart = LocalTime.parse(theSchedule.getStart());
            LocalTime rangeEnd = LocalTime.parse(theSchedule.getStop());

            LocalTime newStart = LocalTime.parse(new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime()));
            if ((!newStart.isBefore(rangeStart) && newStart.isBefore(rangeEnd))) {
                result = String.valueOf(theSchedule.getId());
                break;
            }

        }

        return result;

    }

    @Override
    protected void onPause() {
        super.onPause();

        //check if helper is null
        if (helper != null){

            if (activity.getIsStudying() && isSearching){

                Paper.book().write(Common.STUDY_HELP, helper);

                isSearching = false;

            } else

            if (activity.getIsStudying() && !isSearching){

                helper.setStop(System.currentTimeMillis());

                //get minutes
                long minutesFocused = helper.getStop()/(60 * 1000 ) - helper.getStart()/(60 * 1000);

                //create registry
                CourseRegistry newReg = new CourseRegistry(matric, course, helper.getSchedule(), 0, minutesFocused);

                //save registry
                new Thread(() -> registryViewModel.addNewRegistry(newReg)).start();

                //delete save
                Paper.book().delete(Common.STUDY_HELP);

                //release notification
                lockNotification(false);

                //restart
                helper = new StudyHelper(matric, course, getCurrentSchedule(), false, System.currentTimeMillis(), 0);
                Paper.book().write(Common.STUDY_HELP, helper);

            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        helper = Paper.book().read(Common.STUDY_HELP);
        activity.setIsStudying(helper != null);

        if (helper != null){

            if (activity.getIsStudying() && !helper.isStudying()){

                helper.setStop(System.currentTimeMillis());

                //lock notification
                lockNotification(true);

                //get minutes
                long minutesDistracted = helper.getStop()/(60 * 1000 ) - helper.getStart()/(60 * 1000);

                //create registry
                CourseRegistry newReg = new CourseRegistry(matric, course, helper.getSchedule(), minutesDistracted, 0);

                //save registry
                new Thread(() -> registryViewModel.addNewRegistry(newReg)).start();

                //delete save
                Paper.book().delete(Common.STUDY_HELP);

                //restart
                helper = new StudyHelper(matric, course, getCurrentSchedule(), true, System.currentTimeMillis(), 0);
                Paper.book().write(Common.STUDY_HELP, helper);

            }

        }
    }

}