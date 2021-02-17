package com.lazuardifachri.bps.lekdarjoapp.view.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.ItemDateBinding;
import com.lazuardifachri.bps.lekdarjoapp.databinding.ItemStatisticalNewsBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.StatisticalNews;
import com.lazuardifachri.bps.lekdarjoapp.view.listener.StatisticalNewsCLickListener;
import com.lazuardifachri.bps.lekdarjoapp.view.list_fragment.StatisticalNewsListFragmentDirections;

import java.util.ArrayList;
import java.util.List;

public class StatisticalNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StatisticalNewsCLickListener {

    private ArrayList<ObjectList> objectLists;

    public StatisticalNewsAdapter(ArrayList<ObjectList> objectLists) {
        this.objectLists = objectLists;
    }

    public void setDataChange(List<ObjectList> asList) {
        this.objectLists.clear();
        this.objectLists.addAll(asList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ObjectList.TYPE_OBJECT:
                ItemStatisticalNewsBinding newsView = DataBindingUtil.inflate(inflater, R.layout.item_statistical_news, parent, false);
                viewHolder = new StatisticalNewsObjectViewHolder(newsView);
                break;
            case ObjectList.TYPE_DATE:
                ItemDateBinding dateView = DataBindingUtil.inflate(inflater, R.layout.item_date, parent, false);
                viewHolder = new DateObjectViewHolder(dateView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case ObjectList.TYPE_OBJECT:
                StatisticalNewsObject newsObject = (StatisticalNewsObject) objectLists.get(position);
                StatisticalNewsObjectViewHolder newsObjectViewHolder = (StatisticalNewsObjectViewHolder) viewHolder;
                newsObjectViewHolder.bind(newsObject.getStatisticalNewsModel());
                newsObjectViewHolder.newsBinding.setListener(this);
                break;
            case ObjectList.TYPE_DATE:
                DateObject dateObject = (DateObject) objectLists.get(position);
                DateObjectViewHolder dateObjectViewHolder = (DateObjectViewHolder) viewHolder;
                dateObjectViewHolder.bind(dateObject.getDate());
                break;
        }


    }

    @Override
    public void onStatisticalNewsClick(View v) {
        Log.d("news", "click");
        String uuidString = ((TextView) v.findViewById(R.id.newsUuId)).getText().toString();
        Log.d("newsuuid", uuidString);
        int uuid = Integer.parseInt(uuidString);
        if (uuid != 0) {
            StatisticalNewsListFragmentDirections.ActionStatisticalNewsListToDetail action = StatisticalNewsListFragmentDirections.actionStatisticalNewsListToDetail();
            action.setUuid(uuid);
            Navigation.findNavController(v).navigate(action);
        }
    }

    @Override
    public int getItemCount() {
        if (objectLists != null) {
            return objectLists.size();
        }
        return 0;
    }


    @Override
    public int getItemViewType(int position) {
        return objectLists.get(position).getType();
    }


    class DateObjectViewHolder extends RecyclerView.ViewHolder {

        ItemDateBinding dateBinding;

        public DateObjectViewHolder(@NonNull ItemDateBinding itemView) {
            super(itemView.getRoot());
            this.dateBinding = itemView;
        }

        public void bind(final String date) {
            dateBinding.dateText.setText(date);
        }
    }

    class StatisticalNewsObjectViewHolder extends RecyclerView.ViewHolder {

        ItemStatisticalNewsBinding newsBinding;

        public StatisticalNewsObjectViewHolder(@NonNull ItemStatisticalNewsBinding itemView) {
            super(itemView.getRoot());
            this.newsBinding = itemView;
        }

        public void bind(final StatisticalNews statisticalNews) {
            newsBinding.setStatisticalNews(statisticalNews);
        }
    }
}
