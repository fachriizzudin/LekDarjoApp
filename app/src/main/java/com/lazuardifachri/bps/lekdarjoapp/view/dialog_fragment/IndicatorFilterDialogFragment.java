package com.lazuardifachri.bps.lekdarjoapp.view.dialog_fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.DialogFilterIndicatorBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.Category;
import com.lazuardifachri.bps.lekdarjoapp.util.Parameters;

import java.util.List;

public class IndicatorFilterDialogFragment extends DialogFragment {

    DialogFilterIndicatorBinding binding;
    List<Category> categories;

    public interface OnFilterSelected {
        void sendInput(int categoryId, String monthString, int year);
    }

    IndicatorFilterDialogFragment.OnFilterSelected onFilterSelected;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            switch (bundle.getInt("code")) {
                case 1:
                    categories = Parameters.getInstance().getSocialCategories();
                    break;
                case 2:
                    categories = Parameters.getInstance().getEconomyCategories();
                    break;
                case 3:
                    categories = Parameters.getInstance().getAgricultureCategories();
            }
        }

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        binding = DialogFilterIndicatorBinding.inflate(inflater);

        View dialogView = binding.getRoot();

        Spinner categorySpinner = binding.dialogCategorySpinner;
        categorySpinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, categories));
        categorySpinner.setSelection(0);

        NumberPicker monthNumberPicker = binding.monthDialogNumberPicker;
        monthNumberPicker.setMinValue(0);
        monthNumberPicker.setMaxValue(12);
        monthNumberPicker.setValue(0);

        NumberPicker yearNumberPicker = binding.yearDialogNumberPicker;
        yearNumberPicker.setMinValue(2000);
        yearNumberPicker.setMaxValue(2100);
        yearNumberPicker.setValue(2020);

        binding.dialogSearchButton.setOnClickListener(v -> {
            Category category = (Category) categorySpinner.getSelectedItem();
            int categoryId = category.getId();

            int month = monthNumberPicker.getValue();

            String monthString;
            if (month < 10) {
                monthString = "0" + month;
            } else {
                monthString = String.valueOf(month);
            }
            int year = yearNumberPicker.getValue();

            onFilterSelected.sendInput(categoryId, monthString, year);
            dismiss();
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView).setTitle(R.string.filterTitle);

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onFilterSelected = (IndicatorFilterDialogFragment.OnFilterSelected) getTargetFragment();
        } catch (ClassCastException e){
            Log.e("TAG", "onAttach: ClassCastException : " + e.getMessage() );
        }
    }
}
