package com.batson.reliefweb.Adaptor;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.batson.reliefweb.R;
import com.batson.reliefweb.Model.JobModel;
import com.efaso.admob_advanced_native_recyvlerview.AdmobNativeAdAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Titus Batson
 * Website: titusbatson.com
 * Date: 10/03/2020
 */

public class JobsAdaptor extends RecyclerView.Adapter<JobsAdaptor.ViewHolder> implements Filterable {

    private List<JobModel.Data> data;
    private List<JobModel.Data> datafiltered;


    public JobsAdaptor(List<JobModel.Data> data) {

        this.data = data;
        this.datafiltered = new ArrayList<>(data);
    }

    @Override

    public JobsAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);

        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(JobsAdaptor.ViewHolder holder, int position) {
        holder.textReportTitle.setText(data.get(position).getFields().getTitle());
        holder.txtorg.setText(data.get(position).getFields().getSource().get(0).getName());

        holder.closing.setText(data.get(position).getFields().getDate().getCreated().substring(0, 10));
        if(data.get(position).getFields().getCareer_categories() != null) {
            holder.txtcategory.setText(data.get(position).getFields().getCareer_categories().get(0).getCat());
        }
        if(data.get(position).getFields().getCountry() != null) {
            holder.country.setText(data.get(position).getFields().getCountry().get(0).getName());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textReportTitle,txtcategory, txtorg, country, closing;

        public ViewHolder(View itemView) {
            super(itemView);
            textReportTitle = itemView.findViewById(R.id.text_report_title);
            txtorg = itemView.findViewById(R.id.org);
            txtcategory = itemView.findViewById(R.id.category);
            closing = itemView.findViewById(R.id.closingdate);
            country = itemView.findViewById(R.id.country);
        }
    }
    @Override
    public Filter getFilter() {
        return datafilter;

    }
    private Filter datafilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<JobModel.Data> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(datafiltered);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (JobModel.Data item : datafiltered) {
                    if (item.getFields().getTitle().toLowerCase().contains(filterPattern) || item.getFields().getSource().get(0).getName().toLowerCase().contains(filterPattern)|| item.getFields().getCareer_categories().get(0).getCat().toLowerCase().contains(filterPattern) || item.getFields().getSource().get(0).getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data.clear();
            data.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
