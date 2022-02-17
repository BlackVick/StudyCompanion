package esw.peeplo.studentstudycom.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.databinding.ActivityWelcomeBinding;
import esw.peeplo.studentstudycom.util.Methods;

public class Welcome extends AppCompatActivity {

    //binding
    private ActivityWelcomeBinding activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = DataBindingUtil.setContentView(this, R.layout.activity_welcome);

        //init
        initialize();
    }

    private void initialize() {

        //back
        activity.backButton.setOnClickListener(v -> onBackPressed());

        //register
        activity.registerBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, Register.class));
            Methods.slideLeft(this);
        });

        //login
        activity.loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, Login.class));
            Methods.slideLeft(this);
        });
    }

    @Override
    public void onBackPressed() {

        //close activity
        finish();

        //close animation
        Methods.slideRight(this);
    }
}