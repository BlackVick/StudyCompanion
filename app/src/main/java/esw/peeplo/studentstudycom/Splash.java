package esw.peeplo.studentstudycom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import esw.peeplo.studentstudycom.auth.StudyStyleEvaluation;
import esw.peeplo.studentstudycom.auth.Welcome;
import esw.peeplo.studentstudycom.models.User;
import esw.peeplo.studentstudycom.util.Common;
import esw.peeplo.studentstudycom.util.Methods;
import io.paperdb.Paper;

public class Splash extends AppCompatActivity {

    //value
    private String userId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //get value
        userId = Paper.book().read(Common.USER_ID);
        currentUser = Paper.book().read(Common.CURRENT_USER);

        //check
        if (userId != null && !TextUtils.isEmpty(userId) && currentUser != null){

            if (TextUtils.isEmpty(currentUser.getStudy_style())){

                //go to style evaluation
                Intent evaluationIntent = new Intent(this, StudyStyleEvaluation.class);
                evaluationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(evaluationIntent);
                finish();
                Methods.slideLeft(this);

            } else {

                //go to dashboard
                Intent homeIntent = new Intent(this, Dashboard.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
                finish();
                Methods.slideLeft(this);

            }

        } else {

            //wait 2 seconds
            new Handler(Looper.getMainLooper()).postDelayed(() -> {

                startActivity(new Intent(this, Welcome.class));
                finish();
                Methods.slideLeft(this);

            }, 2000);

        }
    }
}