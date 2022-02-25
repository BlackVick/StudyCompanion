package esw.peeplo.studentstudycom.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import esw.peeplo.studentstudycom.Dashboard;
import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.adapters.QuestionnaireAdapter;
import esw.peeplo.studentstudycom.databinding.ActivityStudyStyleEvaluationBinding;
import esw.peeplo.studentstudycom.dialogs.InfoDialog;
import esw.peeplo.studentstudycom.dialogs.LoadingDialog;
import esw.peeplo.studentstudycom.interfaces.InfoClickListener;
import esw.peeplo.studentstudycom.models.Answer;
import esw.peeplo.studentstudycom.models.AnsweredQuestion;
import esw.peeplo.studentstudycom.models.AutoSchedule;
import esw.peeplo.studentstudycom.models.Question;
import esw.peeplo.studentstudycom.models.Schedule;
import esw.peeplo.studentstudycom.models.User;
import esw.peeplo.studentstudycom.util.Common;
import esw.peeplo.studentstudycom.util.Methods;
import esw.peeplo.studentstudycom.viewmodels.ScheduleViewModel;
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

    //auto scheduling
    private List<AutoSchedule> progressiveSchedule = new ArrayList<>();
    private List<AutoSchedule> oneWeekSchedule = new ArrayList<>();
    private List<AutoSchedule> obsessedSchedule = new ArrayList<>();
    private List<AutoSchedule> crammerSchedule = new ArrayList<>();

    //view model
    private UserViewModel userViewModel;
    private ScheduleViewModel scheduleViewModel;

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

        //init auto scheduling
        initAutoSchedules();

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
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

        //loading
        activity.setIsLoading(false);
        activity.setHasPrevious(false);
        activity.setIsFinal(false);

        //get current user
        getCurrentUser();

    }

    private void initAutoSchedules() {

        //progressive learner time slots
        progressiveSchedule.add(new AutoSchedule(Common.DAY_SUN, "08:00", "10:00"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_TUE, "08:00", "10:00"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_THU, "08:00", "10:00"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_SAT, "08:00", "10:00"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_MON, "08:00", "10:00"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_WED, "08:00", "10:00"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_FRI, "08:00", "10:00"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_SUN, "10:05", "11:05"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_MON, "10:05", "11:05"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_TUE, "10:05", "11:05"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_WED, "10:05", "11:05"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_THU, "10:05", "11:05"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_FRI, "10:05", "11:05"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_SAT, "10:05", "11:05"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_SUN, "11:10", "12:10"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_MON, "11:10", "12:10"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_TUE, "11:10", "12:10"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_WED, "11:10", "12:10"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_THU, "11:10", "12:10"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_FRI, "11:10", "12:10"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_SAT, "11:10", "12:10"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_SUN, "12:15", "13:15"));
        progressiveSchedule.add(new AutoSchedule(Common.DAY_MON, "12:15", "13:15"));

        //one week learner
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_SUN, "08:00", "12:00"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_TUE, "08:00", "12:00"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_THU, "08:00", "12:00"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_SAT, "08:00", "12:00"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_MON, "08:00", "12:00"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_WED, "08:00", "12:00"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_FRI, "08:00", "12:00"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_SUN, "12:05", "14:05"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_MON, "12:05", "14:05"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_TUE, "12:05", "14:05"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_WED, "12:05", "14:05"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_THU, "12:05", "14:05"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_FRI, "12:05", "14:05"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_SAT, "12:05", "14:05"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_SUN, "14:10", "16:10"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_MON, "14:10", "16:10"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_TUE, "14:10", "16:10"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_WED, "14:10", "16:10"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_THU, "14:10", "16:10"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_FRI, "14:10", "16:10"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_SAT, "14:10", "16:10"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_SUN, "16:15", "18:15"));
        oneWeekSchedule.add(new AutoSchedule(Common.DAY_MON, "16:15", "18:15"));

        //obsessed learner
        obsessedSchedule.add(new AutoSchedule(Common.DAY_SUN, "08:00", "15:00"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_TUE, "08:00", "15:00"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_THU, "08:00", "15:00"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_SAT, "08:00", "15:00"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_MON, "08:00", "15:00"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_WED, "08:00", "15:00"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_FRI, "08:00", "15:00"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_SUN, "15:05", "18:35"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_MON, "15:05", "18:35"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_TUE, "15:05", "18:35"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_WED, "15:05", "18:35"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_THU, "15:05", "18:35"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_FRI, "15:05", "18:35"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_SAT, "15:05", "18:35"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_SUN, "18:40", "21:40"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_MON, "18:40", "21:40"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_TUE, "18:40", "21:40"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_WED, "18:40", "21:40"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_THU, "18:40", "21:40"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_FRI, "18:40", "21:40"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_SAT, "18:40", "21:40"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_SUN, "21:45", "00:45"));
        obsessedSchedule.add(new AutoSchedule(Common.DAY_MON, "21:45", "00:45"));

        //crammer
        crammerSchedule.add(new AutoSchedule(Common.DAY_SUN, "08:00", "13:00"));
        crammerSchedule.add(new AutoSchedule(Common.DAY_SUN, "13:00", "18:00"));
        crammerSchedule.add(new AutoSchedule(Common.DAY_SUN, "18:00", "23:00"));
        crammerSchedule.add(new AutoSchedule(Common.DAY_MON, "08:00", "13:00"));
        crammerSchedule.add(new AutoSchedule(Common.DAY_MON, "13:00", "18:00"));
        crammerSchedule.add(new AutoSchedule(Common.DAY_MON, "18:00", "23:00"));
        crammerSchedule.add(new AutoSchedule(Common.DAY_TUE, "08:00", "13:00"));
        crammerSchedule.add(new AutoSchedule(Common.DAY_TUE, "13:00", "18:00"));
        crammerSchedule.add(new AutoSchedule(Common.DAY_TUE, "18:00", "23:00"));
        crammerSchedule.add(new AutoSchedule(Common.DAY_WED, "08:00", "13:00"));
        crammerSchedule.add(new AutoSchedule(Common.DAY_WED, "13:00", "18:00"));
        crammerSchedule.add(new AutoSchedule(Common.DAY_WED, "18:00", "23:00"));
        crammerSchedule.add(new AutoSchedule(Common.DAY_THU, "08:00", "13:00"));
        crammerSchedule.add(new AutoSchedule(Common.DAY_THU, "13:00", "18:00"));
        crammerSchedule.add(new AutoSchedule(Common.DAY_THU, "18:00", "23:00"));

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

            //schedules courses
            scheduleCourses(style);

        } else

        if (result > 60 && result <= 70){

            style = Common.STYLE_ONE_WEEK;

            //schedules courses
            scheduleCourses(style);

        } else

        if (result > 70 && result <= 85){

            style = Common.STYLE_OBSESSED;

            //schedules courses
            scheduleCourses(style);

        } else

        if (result > 85){

            style = Common.STYLE_CRAMMER;

            //schedules courses
            scheduleCourses(style);

        }

        //save
        new Thread(() -> {
            //update
            userViewModel.updateUserEvaluation(matric, style);

            //get latest
            User tempUser = userViewModel.getCurrentUser(matric);
            if (tempUser != null){
                currentUser = tempUser;

                //save user
                Paper.book().write(Common.CURRENT_USER, currentUser);
            }

        }).start();

        //stop loading
        loadingDialog.cancelDialog();

        //show info
        String[] params = {"Study Evaluation", "Your study evaluation is complete and you fall into " + style + " category of learners.", "Acknowledged"};
        infoDialog = new InfoDialog(this, this, params, this);
        infoDialog.showDialog();

    }

    private void scheduleCourses(String style) {

        //get the courses
        List<String> courses = currentUser.getCourses().getCourses();

        new Thread(() -> {

            switch (style){

                case Common.STYLE_PROGRESSIVE:

                    //loop all course
                    for (int i = 0; i < courses.size(); i++){

                        String theCourse = courses.get(i);
                        AutoSchedule currentAutoSchedule = progressiveSchedule.get(i);

                        //create schedule
                        scheduleViewModel.addNewSchedule(new Schedule(matric, theCourse, currentAutoSchedule.getDay(), currentAutoSchedule.getStart(), currentAutoSchedule.getStop()));

                    }
                    break;

                case Common.STYLE_ONE_WEEK:

                    //loop all course
                    for (int i = 0; i < courses.size(); i++){

                        String theCourse = courses.get(i);
                        AutoSchedule currentAutoSchedule = oneWeekSchedule.get(i);

                        //create schedule
                        scheduleViewModel.addNewSchedule(new Schedule(matric, theCourse, currentAutoSchedule.getDay(), currentAutoSchedule.getStart(), currentAutoSchedule.getStop()));

                    }
                    break;

                case Common.STYLE_OBSESSED:

                    //loop all course
                    for (int i = 0; i < courses.size(); i++){

                        String theCourse = courses.get(i);
                        AutoSchedule currentAutoSchedule = obsessedSchedule.get(i);

                        //create schedule
                        scheduleViewModel.addNewSchedule(new Schedule(matric, theCourse, currentAutoSchedule.getDay(), currentAutoSchedule.getStart(), currentAutoSchedule.getStop()));

                    }
                    break;

                case Common.STYLE_CRAMMER:

                    //loop all course
                    for (int i = 0; i < courses.size(); i++){

                        String theCourse = courses.get(i);
                        AutoSchedule currentAutoSchedule = crammerSchedule.get(i);

                        //create schedule
                        scheduleViewModel.addNewSchedule(new Schedule(matric, theCourse, currentAutoSchedule.getDay(), currentAutoSchedule.getStart(), currentAutoSchedule.getStop()));

                    }
                    break;

            }

        }).start();

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
        allQuestions.add(new Question(1, "Do you study most on weekend?", getAnswers(1)));
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

        new Thread(() -> {
            User tempUser = userViewModel.getCurrentUser(matric);
            if (tempUser != null){
                currentUser = tempUser;
            }

        }).start();

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

        startActivity(new Intent(this, SchedulePreset.class));
        finish();
        Methods.slideLeft(this);

    }

}