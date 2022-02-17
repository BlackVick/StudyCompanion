package esw.peeplo.studentstudycom.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import esw.peeplo.studentstudycom.CourseDetail;
import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.auth.StudyStyleEvaluation;
import esw.peeplo.studentstudycom.databinding.QuestionnaireItemBinding;
import esw.peeplo.studentstudycom.models.Question;
import esw.peeplo.studentstudycom.models.Schedule;

public class QuestionnaireAdapter extends RecyclerView.Adapter<QuestionnaireAdapter.QuestionnaireViewHolder>{

    private List<Question> questionList;
    private LayoutInflater layoutInflater;
    private Activity activity;
    private Context context;

    public QuestionnaireAdapter(List<Question> questionList, Activity activity, Context context) {
        this.questionList = questionList;
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public QuestionnaireViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        QuestionnaireItemBinding binding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.questionnaire_item,
                parent,
                false
        );

        return new QuestionnaireViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionnaireViewHolder holder, int position) {
        holder.bindQuestion(questionList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    class QuestionnaireViewHolder extends RecyclerView.ViewHolder{

        private QuestionnaireItemBinding binding;

        public QuestionnaireViewHolder(QuestionnaireItemBinding binding){

            super(binding.getRoot());
            this.binding = binding;

        }

        public void bindQuestion(Question question, int position){

            //bind data
            binding.setCurrentQuestion(question);
            binding.executePendingBindings();

            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_left);
            binding.getRoot().startAnimation(animation);

            //clear check
            binding.answerRadio.clearCheck();

            //picking answer
            binding.answerRadio.setOnCheckedChangeListener((group, checkedId) -> {

                switch (checkedId){
                    case R.id.radioA:
                        ((StudyStyleEvaluation)activity).selectAnswer(question.getAnswers().get(0));
                        break;

                    case R.id.radioB:
                        ((StudyStyleEvaluation)activity).selectAnswer(question.getAnswers().get(1));
                        break;

                    case R.id.radioC:
                        ((StudyStyleEvaluation)activity).selectAnswer(question.getAnswers().get(2));
                        break;

                    case R.id.radioD:
                        ((StudyStyleEvaluation)activity).selectAnswer(question.getAnswers().get(3));
                        break;
                }

            });


        }

    }

}
