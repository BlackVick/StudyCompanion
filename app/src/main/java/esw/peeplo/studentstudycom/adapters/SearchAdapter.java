package esw.peeplo.studentstudycom.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.databinding.SearchItemBinding;
import esw.peeplo.studentstudycom.interfaces.SearchClickListener;
import esw.peeplo.studentstudycom.models.Search;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.CourseViewHolder>{

    private List<Search> searchList;
    private LayoutInflater layoutInflater;
    private SearchClickListener listener;
    private Activity activity;
    private Context context;

    public SearchAdapter(List<Search> searchList, Activity activity, Context context, SearchClickListener listener) {
        this.searchList = searchList;
        this.activity = activity;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        SearchItemBinding binding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.search_item,
                parent,
                false
        );

        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        holder.bindSearch(searchList.get(position));
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder{

        private SearchItemBinding binding;

        public CourseViewHolder(SearchItemBinding binding){

            super(binding.getRoot());
            this.binding = binding;

        }

        public void bindSearch(Search search){

            //bind data
            binding.setParam(search.getParams());
            binding.executePendingBindings();

            //click
            binding.getRoot().setOnClickListener(v -> {
                listener.onSearchClick(search.getParams());
            });

        }

    }

}
