package esw.peeplo.studentstudycom.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.adapters.CourseSelectAdapter;
import esw.peeplo.studentstudycom.databinding.ActivityRegisterBinding;
import esw.peeplo.studentstudycom.dialogs.InfoDialog;
import esw.peeplo.studentstudycom.interfaces.InfoClickListener;
import esw.peeplo.studentstudycom.models.Courses;
import esw.peeplo.studentstudycom.models.User;
import esw.peeplo.studentstudycom.services.ScheduleService;
import esw.peeplo.studentstudycom.util.Common;
import esw.peeplo.studentstudycom.util.Methods;
import esw.peeplo.studentstudycom.viewmodels.UserViewModel;
import io.paperdb.Paper;

public class Register extends AppCompatActivity implements InfoClickListener {

    //binding
    private ActivityRegisterBinding activity;

    //course data
    private List<String> courseList = new ArrayList<>();
    private CourseSelectAdapter adapter;

    //dialogs
    private InfoDialog infoDialog;

    //image upload
    private static final int VERIFY_PERMISSIONS_REQUEST = 757;
    private boolean isPermissionGranted = false;
    private static final int GALLERY_REQUEST_CODE = 665;
    private Uri imageUri;
    private String imageLink = "";

    //room
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = DataBindingUtil.setContentView(this, R.layout.activity_register);

        //init
        initialize();
    }

    private void initialize() {

        //set defaults
        setDefaults();

        //check permissions
        checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,  VERIFY_PERMISSIONS_REQUEST);

        //populate courses
        populateCourses();

        //load course recycler
        loadCourseRecycler();

        //back
        activity.backButton.setOnClickListener(v -> onBackPressed());

        //login
        activity.loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, Login.class));
            finish();
            Methods.slideRight(this);
        });

        //avatar
        activity.userAvatar.setOnClickListener(v -> {
            if (isPermissionGranted) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_REQUEST_CODE);
            } else {
                //show error
                String[] params = {"Permission Denied", "Student StudyCom currently have no permission to access device media, please go to settings and grant Student StudyCom storage permission.", "Okay"};
                infoDialog = new InfoDialog(this, this, params, this);
                infoDialog.showDialog();
            }
        });

        //register
        activity.registerBtn.setOnClickListener(v -> {
            validateParams();
        });

    }

    private void setDefaults() {

        //setup view model
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        //loading
        activity.setIsLoading(false);
    }

    public void checkPermissions(String permission, String permission2, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this, permission2) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permissions
            ActivityCompat.requestPermissions(this, new String[] { permission, permission2 }, requestCode);
        } else {
            isPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
        } else {
            Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){

            if (data.getData() != null) {
                imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(Register.this);
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                //get data
                imageUri = result.getUri();

                //set image
                Picasso.get()
                        .load(Uri.parse(imageUri.toString()))
                        .config(Bitmap.Config.RGB_565)
                        .fit().centerCrop()
                        .into(activity.userAvatar);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void populateCourses() {

        final List<String> genderList = new ArrayList<>();
        genderList.add(0, "Courses");
        genderList.add( Common.COURSE_1);
        genderList.add( Common.COURSE_2);
        genderList.add( Common.COURSE_3);
        genderList.add( Common.COURSE_4);
        genderList.add( Common.COURSE_5);
        genderList.add( Common.COURSE_6);
        genderList.add( Common.COURSE_7);
        genderList.add( Common.COURSE_8);
        genderList.add( Common.COURSE_9);
        genderList.add( Common.COURSE_10);
        genderList.add( Common.COURSE_11);
        genderList.add( Common.COURSE_12);
        genderList.add( Common.COURSE_13);
        genderList.add( Common.COURSE_14);
        genderList.add( Common.COURSE_15);

        //adapter
        final ArrayAdapter<String> dataAdapterCourse;
        dataAdapterCourse = new ArrayAdapter(this, R.layout.custom_spinner_list_item, genderList);
        dataAdapterCourse.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);

        //attach adapter
        activity.courseSpinner.setAdapter(dataAdapterCourse);
        dataAdapterCourse.notifyDataSetChanged();

        //selection
        activity.courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!parent.getItemAtPosition(position).toString().equals("Courses")) {

                    if (!courseList.contains(parent.getItemAtPosition(position).toString())){
                        courseList.add(parent.getItemAtPosition(position).toString());
                        adapter.notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void removeCourse(String course){

        if (courseList.contains(course)){
            courseList.remove(course);
            adapter.notifyDataSetChanged();
        }

    }

    private void loadCourseRecycler() {

        activity.courseRecycler.setHasFixedSize(true);
        activity.courseRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new CourseSelectAdapter(courseList, this, this);
        activity.courseRecycler.setAdapter(adapter);

    }

    private void validateParams() {

        String matric = activity.userMatric.getText().toString().trim();
        String firstName = activity.firstName.getText().toString().trim();
        String lastName = activity.lastName.getText().toString().trim();
        String dept = activity.userDept.getText().toString().trim();
        String pass = activity.userPassword.getText().toString().trim();
        String conf = activity.confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(matric)){

            activity.userMatric.requestFocus();
            activity.userMatric.setError("required");

        } else

        if (TextUtils.isEmpty(firstName)){

            activity.firstName.requestFocus();
            activity.firstName.setError("required");

        } else

        if (TextUtils.isEmpty(lastName)){

            activity.lastName.requestFocus();
            activity.lastName.setError("required");

        } else

        if (TextUtils.isEmpty(dept)){

            activity.userDept.requestFocus();
            activity.userDept.setError("required");

        } else

        if (courseList.size() < 1){

            //show error
            String[] params = {"Course Registration", "No course has been selected for registration. Please pick your courses and proceed", "Okay"};
            infoDialog = new InfoDialog(this, this, params, this);
            infoDialog.showDialog();

        } else

        if (TextUtils.isEmpty(pass)){

            activity.userPassword.requestFocus();
            activity.userPassword.setError("required");

        } else

        if (pass.length() < 6){

            activity.userPassword.requestFocus();
            activity.userPassword.setError("weak");

        } else

        if (!conf.equals(pass)){

            activity.confirmPassword.requestFocus();
            activity.confirmPassword.setError("mismatch");

        } else {

            registerUser(matric, firstName, lastName, dept, pass);

        }
    }

    private void registerUser(String matric, String firstName, String lastName, String dept, String pass) {

        //start loading
        activity.setIsLoading(true);

        if (imageUri != null){

            File avatarFolder = new File(getExternalFilesDir(""), Common.AVATAR_FOLDER);
            if (!avatarFolder.exists()){
                avatarFolder.mkdirs();
            }

            //avatar file
            File avatarFile = new File(avatarFolder, matric + ".jpg");

            //copy file
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(imageUri.getPath());
                out = new FileOutputStream(avatarFile.getAbsolutePath());

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;

                // write the output file (You have now copied the file)
                out.flush();
                out.close();
                out = null;

            } catch (FileNotFoundException fileError) {
                Log.d("FileError", "File Error: " + fileError.getMessage());
            } catch (Exception e) {
                Log.d("FileError", "Process Error: " + e.getMessage());
            }


            //create image uri
            Uri uri = Uri.fromFile(new File(avatarFile.getAbsolutePath()));
            imageLink = uri.toString();

        }

        //check if user exist
        userViewModel.doesUserExist(matric).observe(this, user -> {

            if (user != null){

                //stop loading
                activity.setIsLoading(false);

                //show error
                String[] params = {"User Exists", "A student with this matric number (" + matric + ") has already registered on this platform.", "Okay"};
                infoDialog = new InfoDialog(this, this, params, this);
                infoDialog.showDialog();

            } else {

                //convert list to gson string
                Courses theCourses = new Courses(courseList);

                //create new user
                User newUser = new User(firstName, lastName, matric, dept, "", imageLink, theCourses, "", pass);

                //update database
                new Thread(() -> userViewModel.addNewUser(newUser)).start();

                //process registration
                processData(newUser);

            }

        });

    }

    private void processData(User newUser) {

        //stop loading
        activity.setIsLoading(false);

        //save to local
        Paper.book().write(Common.USER_ID, newUser.getMatric_num());
        Paper.book().write(Common.CURRENT_USER, newUser);

        //start schedule service
        if (!Methods.isServiceRunning(this, ScheduleService.class)) {
            Intent scheduleIntent = new Intent(this, ScheduleService.class);
            ContextCompat.startForegroundService(this, scheduleIntent);
        }

        //update ui
        Intent studyStyleIntent = new Intent(this, StudyStyleEvaluation.class);
        studyStyleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(studyStyleIntent);
        finish();
        Methods.slideLeft(this);

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