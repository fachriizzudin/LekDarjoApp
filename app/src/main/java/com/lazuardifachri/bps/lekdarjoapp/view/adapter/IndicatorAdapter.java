package com.lazuardifachri.bps.lekdarjoapp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.ItemIndicatorBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.Indicator;
import com.lazuardifachri.bps.lekdarjoapp.view.IndicatorItemListener;

import java.util.ArrayList;
import java.util.List;

public class IndicatorAdapter extends RecyclerView.Adapter<IndicatorAdapter.IndicatorViewHolder> implements Filterable {

    private final ArrayList<Indicator> indicatorList;
    private ArrayList<Indicator> filteredIndicatorList;

    private IndicatorItemListener listener;

    public IndicatorAdapter(ArrayList<Indicator> indicatorList, IndicatorItemListener listener) {
        this.indicatorList = indicatorList;
        this.filteredIndicatorList = indicatorList;
        this.listener = listener;
    }

    public void updateIndicator(List<Indicator> newIndicatorList) {
        this.indicatorList.clear();
        this.indicatorList.addAll(newIndicatorList);
        // tells the system that the data for this adapter has changed
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IndicatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemIndicatorBinding view = DataBindingUtil.inflate(inflater, R.layout.item_indicator, parent, false);
        return new IndicatorAdapter.IndicatorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IndicatorViewHolder holder, int position) {
        holder.itemView.setIndicator(filteredIndicatorList.get(position));
        holder.itemView.indicatorDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Indicator indicator = filteredIndicatorList.get(position);
                listener.onIndicatorDownloadClick(v, holder.itemView.progressBar, indicator);
            }
        });
        listener.checkIfFileExist(holder.itemView.indicatorDownloadButton, filteredIndicatorList.get(position));
    }

    @Override
    public int getItemCount() {
        if (!filteredIndicatorList.isEmpty()) {
            return filteredIndicatorList.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charSequenceString = constraint.toString();
                if (charSequenceString.isEmpty()) {
                    filteredIndicatorList = indicatorList;
                } else {
                    ArrayList<Indicator> filteredList = new ArrayList<>();
                    for (Indicator indicator : indicatorList) {
                        if (indicator.getTitle().toLowerCase().contains(charSequenceString.toLowerCase())) {
                            filteredList.add(indicator);
                        }
                        filteredIndicatorList = filteredList;
                    }

                }
                FilterResults results = new FilterResults();
                results.values = filteredIndicatorList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredIndicatorList = (ArrayList<Indicator>) results.values;
                notifyDataSetChanged();
            }
        };
    }



    class IndicatorViewHolder extends RecyclerView.ViewHolder{
        private ItemIndicatorBinding itemView;

        public IndicatorViewHolder(@NonNull ItemIndicatorBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;
        }

    }


}
