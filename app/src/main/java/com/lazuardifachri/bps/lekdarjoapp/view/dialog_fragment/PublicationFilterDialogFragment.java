package com.lazuardifachri.bps.lekdarjoapp.view.dialog_fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.DialogFilterNewsBinding;
import com.lazuardifachri.bps.lekdarjoapp.databinding.DialogFilterPubBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.District;
import com.lazuardifachri.bps.lekdarjoapp.model.Subject;
import com.lazuardifachri.bps.lekdarjoapp.util.Parameters;

import java.util.ArrayList;
import java.util.List;

public class PublicationFilterDialogFragment extends DialogFragment {

    DialogFilterPubBinding binding;

    public interface OnFilterSelected {
        void sendInput(int subjectId, String districtCode, int year);
    }

    OnFilterSelected onFilterSelected;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        binding = DialogFilterPubBinding.inflate(inflater);

        View dialogView = binding.getRoot();

        NumberPicker numberPicker = binding.dialogNumberPicker;
        numberPicker.setMinValue(2000);
        numberPicker.setMaxValue(2100);
        numberPicker.setValue(2020);

        Spinner subjectSpinner = binding.dialogSubjectSpinner;
        ArrayAdapter<Subject> subjectAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, Parameters.getInstance().getSubjects());
        subjectSpinner.setAdapter(subjectAdapter);
        subjectSpinner.setSelection(subjectAdapter.getPosition(Parameters.getInstance().getSubjects().get(0)));

        Spinner districtSpinner = binding.dialogDistrictSpinner;
        ArrayAdapter<District> districtAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, Parameters.getInstance().getDistricts());
        districtSpinner.setAdapter(districtAdapter);
        districtSpinner.setSelection(districtAdapter.getPosition(Parameters.getInstance().getDistricts().get(0)));

        Button button = binding.dialogSearchButton;

        button.setOnClickListener(v -> {
            Subject subject = (Subject) subjectSpinner.getSelectedItem();
            District district = (District) districtSpinner.getSelectedItem();
            onFilterSelected.sendInput(subject.getId(), district.getCode(), numberPicker.getValue());
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
            onFilterSelected = (OnFilterSelected) getTargetFragment();
        } catch (ClassCastException e){
            Log.e("TAG", "onAttach: ClassCastException : " + e.getMessage() );
        }
    }
}

