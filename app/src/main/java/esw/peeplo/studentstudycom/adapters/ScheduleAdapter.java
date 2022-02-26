package esw.peeplo.studentstudycom.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import esw.peeplo.studentstudycom.CourseDetail;
import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.databinding.RegisteredCourseItemBinding;
import esw.peeplo.studentstudycom.databinding.ScheduleItemBinding;
import esw.peeplo.studentstudycom.models.Schedule;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>{

    private List<Schedule> scheduleList;
    private LayoutInflater layoutInflater;
    private Activity activity;
    private Context context;

    public ScheduleAdapter(List<Schedule> scheduleList, Activity activity, Context context) {
        this.scheduleList = scheduleList;
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ScheduleItemBinding binding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.schedule_item,
                parent,
                false
        );

        return new ScheduleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        holder.bindSchedule(scheduleList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder{

        private ScheduleItemBinding binding;

        public ScheduleViewHolder(ScheduleItemBinding binding){

            super(binding.getRoot());
            this.binding = binding;

        }

        public void bindSchedule(Schedule schedule, int position){

            //bind data
            binding.setCurrentSchedule(schedule);
            binding.executePendingBindings();

            //delete schedule
            binding.deleteSchedule.setOnClickListener(v -> {

                if (activity instanceof CourseDetail){
                    ((CourseDetail)activity).deleteSchedule(schedule, position);
                }

            });

            //edit schedule
            binding.editSchedule.setOnClickListener(v -> {

                if (activity instanceof CourseDetail){
                    ((CourseDetail)activity).loadEditScheduleDialog(schedule);
                }

            });

        }

    }

}
