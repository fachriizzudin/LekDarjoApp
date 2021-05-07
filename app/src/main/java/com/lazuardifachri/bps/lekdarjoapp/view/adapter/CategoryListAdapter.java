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
import com.lazuardifachri.bps.lekdarjoapp.databinding.ItemIndicatorCategoryBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.Category;
import com.lazuardifachri.bps.lekdarjoapp.view.list_fragment.IndicatorPagerFragmentDirections;
import com.lazuardifachri.bps.lekdarjoapp.view.listener.CategoryListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder> implements CategoryListener {

    private ArrayList<Category> categories;

    public CategoryListAdapter(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public void setDataChange(List<Category> asList) {
        this.categories.clear();
        this.categories.addAll(asList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemIndicatorCategoryBinding view = DataBindingUtil.inflate(inflater, R.layout.item_indicator_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.categoryItem.setCategory(categories.get(position));
        holder.categoryItem.setListener(this);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public void onCategoryClick(View view) {
        Log.d("indicator", "clicked");
        String idString = ((TextView) view.findViewById(R.id.categoryId)).getText().toString();
        int id = Integer.parseInt(idString);
        IndicatorPagerFragmentDirections.ActionPagerToListIndicator action = IndicatorPagerFragmentDirections.actionPagerToListIndicator();
        action.setId(id);
        Navigation.findNavController(view).navigate(action);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItemIndicatorCategoryBinding categoryItem;

        public CategoryViewHolder(@NonNull ItemIndicatorCategoryBinding itemView) {
            super(itemView.getRoot());
            this.categoryItem = itemView;
        }
    }
}
