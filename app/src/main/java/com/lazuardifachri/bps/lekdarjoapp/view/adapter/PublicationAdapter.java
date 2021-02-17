package com.lazuardifachri.bps.lekdarjoapp.view.adapter;

import android.util.Log;
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
import com.lazuardifachri.bps.lekdarjoapp.databinding.ItemPublicationBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.Publication;
import com.lazuardifachri.bps.lekdarjoapp.view.listener.PublicationClickListener;
import com.lazuardifachri.bps.lekdarjoapp.view.list_fragment.PublicationListFragmentDirections;

import java.util.ArrayList;
import java.util.List;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder> implements PublicationClickListener, Filterable {

    private final ArrayList<Publication> publicationList;
    private ArrayList<Publication> filteredPublicationList;

    public PublicationAdapter(ArrayList<Publication> publicationList) {
        this.publicationList = publicationList;
        this.filteredPublicationList = publicationList;
    }

    public void updatePublication(List<Publication> newPublicationList) {
        this.publicationList.clear();
        this.publicationList.addAll(newPublicationList);
        // tells the system that the data for this adapter has changed
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PublicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemPublicationBinding view = DataBindingUtil.inflate(inflater, R.layout.item_publication, parent, false);
        return new PublicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicationViewHolder holder, int position) {
        holder.itemView.setPublication(filteredPublicationList.get(position));
        holder.itemView.setListener(this);
    }

    @Override
    public void onPublicationClick(View v) {
        String uuidString = ((TextView) v.findViewById(R.id.pubUuId)).getText().toString();
        int uuid = Integer.parseInt(uuidString);
        Log.d("uuidstring", uuidString);
        if (uuid != 0) {
            PublicationListFragmentDirections.PublicationActionListToDetail action = PublicationListFragmentDirections.publicationActionListToDetail();
            action.setUuid(uuid);
            Navigation.findNavController(v).navigate(action);
        }
    }

    @Override
    public int getItemCount() {
        return filteredPublicationList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charSequenceString = constraint.toString();
                if (charSequenceString.isEmpty()) {
                    filteredPublicationList = publicationList;
                } else {
                    ArrayList<Publication> filteredList = new ArrayList<>();
                    for (Publication pub : publicationList) {
                        if (pub.getTitle().toLowerCase().contains(charSequenceString.toLowerCase())) {
                            filteredList.add(pub);
                        }
                        filteredPublicationList = filteredList;
                    }

                }
                FilterResults results = new FilterResults();
                results.values = filteredPublicationList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredPublicationList = (ArrayList<Publication>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class PublicationViewHolder extends RecyclerView.ViewHolder {
        ItemPublicationBinding itemView;

        public PublicationViewHolder(@NonNull ItemPublicationBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;
        }
    }
}