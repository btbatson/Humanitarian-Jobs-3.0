package com.batson.reliefweb.Adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.batson.reliefweb.R;
import com.batson.reliefweb.Model.JobCategoryModel;

import java.util.List;

/**
 * Titus Batson
 * Website: titusbatson.com
 * Date: 10/03/2020
 */

public class JobtypeAdaptor extends RecyclerView.Adapter<JobtypeAdaptor.ViewHolder>  {

    private List<JobCategoryModel.Jobtype> jobtype;


    public JobtypeAdaptor(List<JobCategoryModel.Jobtype> jobtype) {

        this.jobtype = jobtype;
    }

    @Override

    public JobtypeAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(JobtypeAdaptor.ViewHolder holder, int position) {
        holder.cat_name.setText(jobtype.get(position).getFields().name);


    }

    @Override
    public int getItemCount() {
        return jobtype.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cat_name;

        public ViewHolder(View itemView) {
            super(itemView);
            cat_name = itemView.findViewById(R.id.cat_name);

        }
    }


}
