package esw.peeplo.studentstudycom.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import esw.peeplo.studentstudycom.Dashboard;
import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.adapters.QuestionnaireAdapter;
import esw.peeplo.studentstudycom.adapters.SearchAdapter;
import esw.peeplo.studentstudycom.databinding.ActivityStudyStyleEvaluationBinding;
import esw.peeplo.studentstudycom.dialogs.InfoDialog;
import esw.peeplo.studentstudycom.dialogs.LoadingDialog;
import esw.peeplo.studentstudycom.interfaces.InfoClickListener;
import esw.peeplo.studentstudycom.models.Answer;
import esw.peeplo.studentstudycom.models.AnsweredQuestion;
import esw.peeplo.studentstudycom.models.Question;
import esw.peeplo.studentstudycom.models.User;
import esw.peeplo.studentstudycom.util.Common;
import esw.peeplo.studentstudycom.util.Methods;
import esw.peeplo.studentstudycom.viewmodels.UserViewModel;
import io.paperdb.Paper;

public class StudyStyleEvaluation extends AppCompatActivity implements InfoClickListener {

    //binding
    private ActivityStudyStyleEvaluationBinding activity;

    //dialogs
    private LoadingDialog loadingDialog;
    private InfoDialog infoDialog;

    //values
    private String matric;
    private User currentUser;

    //data
    private List<Question> allQuestions = new ArrayList<>();
    private List<Answer> allAnswers = new ArrayList<>();

    //questionnaire
    private QuestionnaireAdapter adapter;
    private List<Question> answeredQuestions = new ArrayList<>();
    private List<AnsweredQuestion> questionAnswers = new ArrayList<>();
    private List<Question> currentQuestionList = new ArrayList<>();
    private Question currentQuestion;
    private AnsweredQuestion currentAnswer;
    private String style = "";

    //view model
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = DataBindingUtil.setContentView(this, R.layout.activity_study_style_evaluation);

        //values
        matric = Paper.book().read(Common.USER_ID);

        //initialization
        initialize();
    }

    private void initialize() {

        //set defaults
        setDefaults();

        //build questionnaire
        buildQuestionnaire();

        //load layout
        loadQuestions();

        //back
        activity.backButton.setOnClickListener(v -> onBackPressed());

        //skip
        activity.skipEval.setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard.class));
            Methods.slideLeft(this);
        });

        //next
        activity.nextBtn.setOnClickListener(v -> {

            if (activity.getIsFinal()){

                evaluateStudent();

            } else {

                nextQuestion();

            }
        });

        //prev
        activity.previousBtn.setOnClickListener(v -> {
            previousQuestion();
        });

    }

    private void setDefaults() {

        //setup view model
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        //loading
        activity.setIsLoading(false);
        activity.setHasPrevious(false);
        activity.setIsFinal(false);

        //get current user
        getCurrentUser();

    }

    private void loadQuestions() {

        activity.questionRecycler.setHasFixedSize(true);

        //smooth scroll
        activity.questionRecycler.setNestedScrollingEnabled(false);

        //set adapter
        adapter = new QuestionnaireAdapter(currentQuestionList, this, this);
        activity.questionRecycler.setAdapter(adapter);

        //get first question
        getFirstQuestion();

    }

    private void getFirstQuestion() {

        //current question
        currentQuestion = getQuestion(1);

        //current answer
        currentAnswer = new AnsweredQuestion();
        currentAnswer.setQuestion_id(String.valueOf(currentQuestion.getId()));

        //add to list
        currentQuestionList.add(currentQuestion);
        adapter.notifyDataSetChanged();

    }

    private void nextQuestion() {

        if (currentAnswer.getAnswer_id() != null){

            //answered question and answer
            answeredQuestions.add(currentQuestion);
            questionAnswers.add(currentAnswer);

            //clear data
            currentQuestionList.clear();
            adapter.notifyDataSetChanged();

            //current question
            currentQuestion = getQuestion(Integer.parseInt(currentAnswer.getNext_question()));

            //current answer
            currentAnswer = new AnsweredQuestion();
            currentAnswer.setQuestion_id(String.valueOf(currentQuestion.getId()));

            //add to list
            currentQuestionList.add(currentQuestion);
            adapter.notifyDataSetChanged();

            //check state
            checkStateofQuestionnaire();

        } else {

            Toast.makeText(this, "Question Not Answered!", Toast.LENGTH_SHORT).show();

        }



    }

    private void previousQuestion() {

        //current question
        currentQuestion = answeredQuestions.get(answeredQuestions.size() - 1);

        //current answer
        currentAnswer = new AnsweredQuestion();
        currentAnswer.setQuestion_id(String.valueOf(currentQuestion.getId()));

        //remove last answered
        answeredQuestions.remove(answeredQuestions.size() - 1);
        questionAnswers.remove(questionAnswers.size() - 1);

        //clear data
        currentQuestionList.clear();
        adapter.notifyDataSetChanged();

        //add to list
        currentQuestionList.add(currentQuestion);
        adapter.notifyDataSetChanged();

        //check state
        checkStateofQuestionnaire();

    }

    private void checkStateofQuestionnaire() {

        if (answeredQuestions.size() < 1){

            activity.setHasPrevious(false);
            activity.setIsFinal(false);

        } else

        if (answeredQuestions.size() > 0 && answeredQuestions.size() < 6){

            activity.setHasPrevious(true);
            activity.setIsFinal(false);

        } else

        if (answeredQuestions.size() == 6){

            activity.setHasPrevious(true);
            activity.setIsFinal(true);

        }

    }

    private void evaluateStudent() {

        //loading
        loadingDialog = new LoadingDialog(this, this, "Evaluating . . .");
        loadingDialog.showDialog();

        //answered question and answer
        answeredQuestions.add(currentQuestion);
        questionAnswers.add(currentAnswer);

        //result
        int result = 0;


        //loop all answers
        for (AnsweredQuestion answer : questionAnswers){

            result += answer.getScore();

        }

        //evaluate
        if (result <= 60){

            style = Common.STYLE_PROGRESSIVE;

        } else

        if (result > 60 && result <= 70){

            style = Common.STYLE_ONE_WEEK;

        } else

        if (result > 70 && result <= 85){

            style = Common.STYLE_OBSESSED;

        } else

        if (result > 85){

            style = Common.STYLE_CRAMMER;

        }

        //save
        new Thread(() -> {
            //update
            userViewModel.updateUserEvaluation(matric, style);

        }).start();

        //get latest
        userViewModel.getCurrentUser(matric).observe(this, response -> {

            currentUser = response;

            //save user
            Paper.book().write(Common.CURRENT_USER, currentUser);

        });

        //stop loading
        loadingDialog.cancelDialog();

        //show info
        String[] params = {"Study Evaluation", "Your study evaluation is complete and you fall into " + style + " category of learners.", "Acknowledged"};
        infoDialog = new InfoDialog(this, this, params, this);
        infoDialog.showDialog();

    }

    public void selectAnswer(Answer answer){

        currentAnswer.setAnswer_id(String.valueOf(answer.getId()));
        currentAnswer.setScore(answer.getScore());
        currentAnswer.setNext_question(String.valueOf(answer.getNext()));

    }

    private void buildQuestionnaire() {

        //answers
        allAnswers.add(new Answer(1, 1, 15, "Yes", 2));
        allAnswers.add(new Answer(2, 1, 5, "No", 3));
        allAnswers.add(new Answer(3, 2, 5, "1 Hr", 4));
        allAnswers.add(new Answer(4, 2, 10, "2 Hrs", 4));
        allAnswers.add(new Answer(5, 2, 15, "4 Hrs", 4));
        allAnswers.add(new Answer(6, 2, 20, "6 Hrs or more", 4));
        allAnswers.add(new Answer(7, 3, 15, "1 Day", 4));
        allAnswers.add(new Answer(8, 3, 10, "2 Days", 4));
        allAnswers.add(new Answer(9, 3, 2, "3 Days", 4));
        allAnswers.add(new Answer(10, 3, 5, "5 Days or more", 4));
        allAnswers.add(new Answer(11, 4, 5, "1 Hr", 5));
        allAnswers.add(new Answer(12, 4, 10, "2 Hrs", 5));
        allAnswers.add(new Answer(13, 4, 15, "4 Hrs", 5));
        allAnswers.add(new Answer(14, 4, 20, "6 Hrs or more", 5));
        allAnswers.add(new Answer(15, 5, 20, "Yes", 6));
        allAnswers.add(new Answer(16, 5, 5, "No", 6));
        allAnswers.add(new Answer(17, 6, 15, "One Week to Exam", 7));
        allAnswers.add(new Answer(18, 6, 10, "Study Obsessively", 7));
        allAnswers.add(new Answer(19, 6, 5, "Study Religiously", 7));
        allAnswers.add(new Answer(20, 6, 20, "Cram Overnight", 7));
        allAnswers.add(new Answer(21, 7, 5, "1 Hr", 8));
        allAnswers.add(new Answer(22, 7, 10, "2 Hrs", 8));
        allAnswers.add(new Answer(23, 7, 15, "4 Hrs", 8));
        allAnswers.add(new Answer(24, 7, 20, "6 Hrs or more", 8));
        allAnswers.add(new Answer(25, 8, 15, "5 Mins", 0));
        allAnswers.add(new Answer(26, 8, 10, "15 Mins", 0));
        allAnswers.add(new Answer(27, 8, 5, "30 Mins", 0));
        allAnswers.add(new Answer(28, 8, 20, "1 Hr or more", 0));

        //questions
        allQuestions.add(new Question(1, "Do you study most on the week?", getAnswers(1)));
        allQuestions.add(new Question(2, "How many hours cumulatively on the weekend can you study?", getAnswers(2)));
        allQuestions.add(new Question(3, "How frequently can you study in a week?", getAnswers(3)));
        allQuestions.add(new Question(4, "How many hours in a day can you dedicate to studying?", getAnswers(4)));
        allQuestions.add(new Question(5, "Would you consider yourself someone with a good memory?", getAnswers(5)));
        allQuestions.add(new Question(6, "What below describes your preferred style of studying?", getAnswers(6)));
        allQuestions.add(new Question(7, "How many hours of intensive studying can you do before you need a break?", getAnswers(7)));
        allQuestions.add(new Question(8, "What duration of break do you consider to be enough?", getAnswers(8)));

    }

    private Question getQuestion(int id){

        //filter
        Optional<Question> currentQuestion = allQuestions.stream().filter((q) -> q.getId() == id).findFirst();

        return currentQuestion.get();
    }

    private List<Answer> getAnswers(int id){

        return allAnswers.stream().filter((a) -> a.getQuestion_id() == id).collect(Collectors.toList());

    }

    private void getCurrentUser() {

        userViewModel.getCurrentUser(matric).observe(this, user -> {

            if (user != null){
                currentUser = user;
            }

        });

    }

    @Override
    public void onBackPressed() {
        //close page
        finish();

        //closing anim
        Methods.slideRight(this);
    }

    @Override
    public void onButtonClick() {
        //close
        infoDialog.cancelDialog();

        startActivity(new Intent(this, Dashboard.class));
        Methods.slideLeft(this);

    }

}