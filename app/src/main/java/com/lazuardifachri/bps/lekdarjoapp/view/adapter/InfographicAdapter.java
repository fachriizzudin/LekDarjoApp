package com.lazuardifachri.bps.lekdarjoapp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.ItemInfographicBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.Infographic;
import com.lazuardifachri.bps.lekdarjoapp.view.listener.InfographicClickListener;
import com.lazuardifachri.bps.lekdarjoapp.view.list_fragment.InfographicListFragmentDirections;

import java.util.ArrayList;
import java.util.List;

public class InfographicAdapter extends RecyclerView.Adapter<InfographicAdapter.InfographicViewHolder> implements Filterable, InfographicClickListener {

    private List<Infographic> infographicList;
    private List<Infographic> filteredInfographicList;

    public InfographicAdapter(List<Infographic> infographicList) {
        this.infographicList = infographicList;
        this.filteredInfographicList = infographicList;
    }

    public void updateInfographic(List<Infographic> infographics) {
        this.infographicList.clear();
        this.infographicList.addAll(infographics);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InfographicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemInfographicBinding view = DataBindingUtil.inflate(inflater, R.layout.item_infographic, parent, false);
        return new InfographicAdapter.InfographicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfographicViewHolder holder, int position) {
        holder.itemView.setInfographic(filteredInfographicList.get(position));
        holder.itemView.setListener(this);
    }

    @Override
    public int getItemCount() {
        return filteredInfographicList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charSequenceString = constraint.toString();
                if (charSequenceString.isEmpty()) {
                    filteredInfographicList = infographicList;
                } else {
                    ArrayList<Infographic> filteredList = new ArrayList<>();
                    for (Infographic pub : infographicList) {
                        if (pub.getTitle().toLowerCase().contains(charSequenceString.toLowerCase())) {
                            filteredList.add(pub);
                        }
                        filteredInfographicList = filteredList;
                    }

                }
                FilterResults results = new FilterResults();
                results.values = filteredInfographicList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredInfographicList = (ArrayList<Infographic>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onInfographicClick(View v) {
        String uuidString = ((TextView) v.findViewById(R.id.infographicUuId)).getText().toString();
        int uuid = Integer.parseInt(uuidString);
        if (uuid != 0) {
            InfographicListFragmentDirections.InfographicActionListToDetail action = InfographicListFragmentDirections.infographicActionListToDetail();
            action.setUuid(uuid);
            Navigation.findNavController(v).navigate(action);
        }
    }


    class InfographicViewHolder extends RecyclerView.ViewHolder {
        ItemInfographicBinding itemView;

        public InfographicViewHolder(@NonNull ItemInfographicBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;
        }
    }
}
