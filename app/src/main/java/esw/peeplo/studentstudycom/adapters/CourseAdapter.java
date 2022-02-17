package esw.peeplo.studentstudycom.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.List;

import esw.peeplo.studentstudycom.CourseDetail;
import esw.peeplo.studentstudycom.Dashboard;
import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.auth.Register;
import esw.peeplo.studentstudycom.databinding.CourseSelectItemBinding;
import esw.peeplo.studentstudycom.databinding.RegisteredCourseItemBinding;
import esw.peeplo.studentstudycom.interfaces.CourseClickListener;
import esw.peeplo.studentstudycom.models.CourseRegistry;
import esw.peeplo.studentstudycom.util.Common;
import esw.peeplo.studentstudycom.viewmodels.RegistryViewModel;
import esw.peeplo.studentstudycom.viewmodels.ScheduleViewModel;
import esw.peeplo.studentstudycom.viewmodels.SearchViewModel;
import io.paperdb.Paper;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder>{

    private List<String> courseList;
    private LayoutInflater layoutInflater;
    private CourseClickListener listener;
    private Activity activity;
    private Context context;

    //view models
    private ScheduleViewModel scheduleViewModel;
    private RegistryViewModel registryViewModel;
    private String matric;

    public CourseAdapter(List<String> courseList, Activity activity, Context context, CourseClickListener listener) {
        this.courseList = courseList;
        this.activity = activity;
        this.context = context;
        this.listener = listener;

        //data
        scheduleViewModel = new ViewModelProvider((Dashboard)context).get(ScheduleViewModel.class);
        registryViewModel = new ViewModelProvider((Dashboard)context).get(RegistryViewModel.class);
        matric = Paper.book().read(Common.USER_ID);
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        RegisteredCourseItemBinding binding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.registered_course_item,
                parent,
                false
        );

        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        holder.bindCourse(courseList.get(position));
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder{

        private RegisteredCourseItemBinding binding;

        public CourseViewHolder(RegisteredCourseItemBinding binding){

            super(binding.getRoot());
            this.binding = binding;

        }

        public void bindCourse(String course){

            //bind data
            binding.setCurrentCourse(course);
            binding.executePendingBindings();

            //total hours read
            registryViewModel.getCourseRegistry(matric, course).observe(((Dashboard)activity), response -> {

                Log.d("Registry", new Gson().toJson(response));

                long totalMins = 0;

                for (CourseRegistry registry : response){

                    totalMins += registry.getFocused();

                }

                binding.setHoursStudied(new DecimalFormat("#.##").format((double) totalMins / 60));

            });

            //get total schedules
            scheduleViewModel.getCourseSchedules(matric, course).observe(((Dashboard)activity), response -> {
                binding.setSchedulesSet(response.size());
            });

            //click
            binding.getRoot().setOnClickListener(v -> {
                listener.onCourseClick(course);
            });

        }

    }

}
