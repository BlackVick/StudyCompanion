package esw.peeplo.studentstudycom.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.auth.Register;
import esw.peeplo.studentstudycom.databinding.CourseSelectItemBinding;

public class CourseSelectAdapter extends RecyclerView.Adapter<CourseSelectAdapter.CourseSelectViewHolder>{

    private List<String> courseList;
    private LayoutInflater layoutInflater;
    private Activity activity;
    private Context context;

    public CourseSelectAdapter(List<String> courseList, Activity activity, Context context) {
        this.courseList = courseList;
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public CourseSelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        CourseSelectItemBinding binding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.course_select_item,
                parent,
                false
        );

        return new CourseSelectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseSelectViewHolder holder, int position) {
        holder.bindCourse(courseList.get(position));
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    class CourseSelectViewHolder extends RecyclerView.ViewHolder{

        private CourseSelectItemBinding binding;

        public CourseSelectViewHolder(CourseSelectItemBinding binding){

            super(binding.getRoot());
            this.binding = binding;

        }

        public void bindCourse(String course){

            //bind data
            binding.setCourse(course);
            binding.executePendingBindings();

            //remove
            binding.removeCourse.setOnClickListener(v -> {
                if (activity instanceof Register){
                    ((Register)activity).removeCourse(course);
                }
            });

        }

    }

}
