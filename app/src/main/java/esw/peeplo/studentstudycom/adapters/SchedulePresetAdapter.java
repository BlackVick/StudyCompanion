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
import java.util.ArrayList;
import java.util.List;

import esw.peeplo.studentstudycom.Dashboard;
import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.auth.SchedulePreset;
import esw.peeplo.studentstudycom.databinding.RegisteredCourseItemBinding;
import esw.peeplo.studentstudycom.databinding.SchedulePresetItemBinding;
import esw.peeplo.studentstudycom.interfaces.CourseClickListener;
import esw.peeplo.studentstudycom.models.CourseRegistry;
import esw.peeplo.studentstudycom.models.Schedule;
import esw.peeplo.studentstudycom.util.Common;
import esw.peeplo.studentstudycom.viewmodels.RegistryViewModel;
import esw.peeplo.studentstudycom.viewmodels.ScheduleViewModel;
import io.paperdb.Paper;

public class SchedulePresetAdapter extends RecyclerView.Adapter<SchedulePresetAdapter.SchedulePresetViewHolder>{

    private List<String> courseList;
    private LayoutInflater layoutInflater;
    private Activity activity;
    private Context context;

    //view models
    private ScheduleViewModel scheduleViewModel;
    private String matric;

    public SchedulePresetAdapter(List<String> courseList, Activity activity, Context context) {
        this.courseList = courseList;
        this.activity = activity;
        this.context = context;

        //data
        scheduleViewModel = new ViewModelProvider((SchedulePreset)context).get(ScheduleViewModel.class);
        matric = Paper.book().read(Common.USER_ID);
    }

    @NonNull
    @Override
    public SchedulePresetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        SchedulePresetItemBinding binding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.schedule_preset_item,
                parent,
                false
        );

        return new SchedulePresetViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SchedulePresetViewHolder holder, int position) {
        holder.bindCourse(courseList.get(position));
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    class SchedulePresetViewHolder extends RecyclerView.ViewHolder{

        private SchedulePresetItemBinding binding;

        public SchedulePresetViewHolder(SchedulePresetItemBinding binding){

            super(binding.getRoot());
            this.binding = binding;

        }

        public void bindCourse(String course){

            //bind data
            binding.setCurrentCourse(course);
            binding.executePendingBindings();

            //get schedules
            new Thread(() -> {

                List<Schedule> scheduleList = scheduleViewModel.getDirectCourseSchedules(matric, course);

                if (scheduleList != null){
                    binding.setCurrentSchedule(scheduleList.get(0));
                }

            }).start();

        }

    }

}
