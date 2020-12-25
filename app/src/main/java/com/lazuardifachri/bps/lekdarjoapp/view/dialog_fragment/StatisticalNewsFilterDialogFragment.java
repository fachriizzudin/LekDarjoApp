package com.lazuardifachri.bps.lekdarjoapp.view.dialog_fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.DialogFilterNewsBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.Category;
import com.lazuardifachri.bps.lekdarjoapp.model.Subject;
import com.lazuardifachri.bps.lekdarjoapp.util.Parameters;

public class StatisticalNewsFilterDialogFragment extends DialogFragment {

    DialogFilterNewsBinding binding;

    public interface OnFilterSelected {
        void sendInput(int subjectId, int categoryId, String monthString, int year);
    }

    OnFilterSelected onFilterSelected;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        binding = DialogFilterNewsBinding.inflate(inflater);

        View dialogView = binding.getRoot();

        Spinner subjectSpinner = binding.dialogSubjectSpinner;
        ArrayAdapter<Subject> subjectAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, Parameters.getInstance().getSubjects());
        subjectSpinner.setAdapter(subjectAdapter);
        subjectSpinner.setSelection(0);

        binding.dialogCategoryText.setVisibility(View.GONE);

        Spinner categorySpinner = binding.dialogCategorySpinner;
        categorySpinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, Parameters.getInstance().getSocialCategories()));
        categorySpinner.setSelection(0);

        binding.dialogCategorySpinner.setVisibility(View.GONE);

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Subject subject = (Subject) parent.getSelectedItem();
                switch (subject.getId()) {
                    case 1:
                        categorySpinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, Parameters.getInstance().getSocialCategories()));
                        binding.dialogCategoryText.setVisibility(View.VISIBLE);
                        binding.dialogCategorySpinner.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        categorySpinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, Parameters.getInstance().getEconomyCategories()));
                        binding.dialogCategoryText.setVisibility(View.VISIBLE);
                        binding.dialogCategorySpinner.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        categorySpinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, Parameters.getInstance().getAgricultureCategories()));
                        binding.dialogCategoryText.setVisibility(View.VISIBLE);
                        binding.dialogCategorySpinner.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                    case 999:
                        binding.dialogCategoryText.setVisibility(View.GONE);
                        binding.dialogCategorySpinner.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        NumberPicker monthNumberPicker = binding.monthDialogNumberPicker;
        monthNumberPicker.setMinValue(0);
        monthNumberPicker.setMaxValue(12);
        monthNumberPicker.setValue(0);

        NumberPicker yearNumberPicker = binding.yearDialogNumberPicker;
        yearNumberPicker.setMinValue(2000);
        yearNumberPicker.setMaxValue(2100);
        yearNumberPicker.setValue(2020);

        binding.dialogSearchButton.setOnClickListener(v -> {
            Subject subject = (Subject) subjectSpinner.getSelectedItem();
            int categoryId;

            if (subject.getId() == 999) {
                categoryId = 0;
            } else {
                Category category = (Category) categorySpinner.getSelectedItem();
                categoryId = category.getId();
            }

            int month = monthNumberPicker.getValue();
            String monthString;
            if (month < 10) {
                monthString = "0" + month;
            } else {
                monthString = String.valueOf(month);
            }
            int year = yearNumberPicker.getValue();

            onFilterSelected.sendInput(subject.getId(), categoryId, monthString, year);
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
            onFilterSelected = (StatisticalNewsFilterDialogFragment.OnFilterSelected) getTargetFragment();
        } catch (ClassCastException e){
            Log.e("TAG", "onAttach: ClassCastException : " + e.getMessage() );
        }
    }
}
