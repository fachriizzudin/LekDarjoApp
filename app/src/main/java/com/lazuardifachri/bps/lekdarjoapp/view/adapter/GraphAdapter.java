package com.lazuardifachri.bps.lekdarjoapp.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.ItemGraphBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.Graph;
import com.lazuardifachri.bps.lekdarjoapp.model.GraphData;
import com.lazuardifachri.bps.lekdarjoapp.util.StringUtil;
import com.lazuardifachri.bps.lekdarjoapp.view.list_fragment.GraphListFragmentDirections;
import com.lazuardifachri.bps.lekdarjoapp.view.listener.GraphCLickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.GraphMetaViewHolder> implements GraphCLickListener {

    private final ArrayList<Graph> graphList;

    public GraphAdapter(ArrayList<Graph> graphList) {
        this.graphList = graphList;
    }

    public void updatePublication(HashSet<Graph> newGraphSet) {
        Log.d("GraphList", "adapter");
        List<Graph> newGraphList = new ArrayList<>(newGraphSet);
        Collections.sort(newGraphList);
        this.graphList.clear();
        this.graphList.addAll(newGraphList);
        // tells the system that the data for this adapter has changed
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GraphMetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemGraphBinding view = DataBindingUtil.inflate(inflater, R.layout.item_graph, parent, false);
        return new GraphMetaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GraphMetaViewHolder holder, int position) {
        Graph graph = graphList.get(position);
        holder.itemView.setGraph(graph);
        holder.itemView.setListener(this);

        // task : proses graph data di sini
        holder.itemView.lastYear.setText(getLastYear(graph.getData()));
        holder.itemView.lastYearData.setText(getLastYearData(graph.getData()));

        if (String.valueOf(getLastYearData(graph.getData())).length() > 5) {
            holder.itemView.lastYearData.setTextSize(30);
        } else {
            holder.itemView.lastYearData.setTextSize(50);
        }

        if (isIncrease(graph.getData())) {
            holder.itemView.upArrow.setImageDrawable(holder.itemView.getRoot().getContext().getResources().getDrawable(R.drawable.ic_up));
        } else {
            holder.itemView.upArrow.setImageDrawable(holder.itemView.getRoot().getContext().getResources().getDrawable(R.drawable.ic_down));
        }
    }

    @Override
    public void onGraphClick(View v) {
        // task : buat navigation untuk ke graphdetail
        String uuidString = ((TextView) v.findViewById(R.id.graphDataUuId)).getText().toString();
        int uuid = Integer.parseInt(uuidString);
        Log.d("uuidstring", uuidString);
        if (uuid != 0) {
            GraphListFragmentDirections.ActionGraphListToGraphDetail action = GraphListFragmentDirections.actionGraphListToGraphDetail();
            action.setUuid(uuid);
            Navigation.findNavController(v).navigate(action);
        }
    }

    @Override
    public int getItemCount() {
        return graphList.size();
    }

    static class GraphMetaViewHolder extends RecyclerView.ViewHolder {
        ItemGraphBinding itemView;

        public GraphMetaViewHolder(@NonNull ItemGraphBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;
        }
    }

    private String getLastYear(List<GraphData> graphDataList) {
        return String.valueOf(graphDataList.get(graphDataList.size()-1).getYear());
    }

    private String getLastYearData(List<GraphData> graphDataList) {
        double value = graphDataList.get(graphDataList.size()-1).getValue();
        int intValue = (int) value;
        if (value - intValue > 0) {
            return StringUtil.formatGraphDouble(value);
        } else if (value < 0) {
            return StringUtil.formatGraphDouble(value);
        } else {
            return StringUtil.formatGraphInt(intValue);
        }
    }

    private boolean isIncrease(List<GraphData> graphDataList) {
        double lastYear = graphDataList.get(graphDataList.size()-1).getValue();
        try {
            // blok try cath untuk kasus data hanya satu
            double secondLastYear = graphDataList.get(graphDataList.size()-2).getValue();
            return lastYear > secondLastYear;
        } catch (Exception e) {
            return true;
        }
    }
}