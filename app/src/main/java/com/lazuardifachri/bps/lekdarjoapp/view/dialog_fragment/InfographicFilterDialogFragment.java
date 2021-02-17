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
import com.lazuardifachri.bps.lekdarjoapp.databinding.DialogFilterInfographicBinding;
import com.lazuardifachri.bps.lekdarjoapp.databinding.DialogFilterNewsBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.Category;
import com.lazuardifachri.bps.lekdarjoapp.model.Subject;
import com.lazuardifachri.bps.lekdarjoapp.util.Parameters;

public class InfographicFilterDialogFragment extends DialogFragment {

    DialogFilterInfographicBinding binding;

    public interface OnFilterSelected {
        void sendInput(int subjectId);
    }

    OnFilterSelected onFilterSelected;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        binding = DialogFilterInfographicBinding.inflate(inflater);

        View dialogView = binding.getRoot();

        Spinner subjectSpinner = binding.dialogSubjectSpinner;
        ArrayAdapter<Subject> subjectAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, Parameters.getInstance().getSubjects());
        subjectSpinner.setAdapter(subjectAdapter);
        subjectSpinner.setSelection(0);

        binding.dialogSearchButton.setOnClickListener(v -> {
            Subject subject = (Subject) subjectSpinner.getSelectedItem();
            onFilterSelected.sendInput(subject.getId());
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
            onFilterSelected = (InfographicFilterDialogFragment.OnFilterSelected) getTargetFragment();
        } catch (ClassCastException e){
            Log.e("TAG", "onAttach: ClassCastException : " + e.getMessage() );
        }
    }
}
