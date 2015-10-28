package com.example.brandon.androidicd10billing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by Brandon on 10/27/2015.
 */
public class PatientEditFragment extends Fragment {

    private FragmentActivity patientEditActivity;
    private RelativeLayout patientEditLayout;
    private BillSystemDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        patientEditActivity = (FragmentActivity) super.getActivity();

        patientEditLayout = (RelativeLayout) inflater.inflate(R.layout.patient_edit, container, false);

        db = new BillSystemDatabase(super.getActivity());
        this.addPatientSaveButtonOnClickListener();

        return patientEditLayout;
    }

    private void addPatientSaveButtonOnClickListener(){
        Button addPatientButton = (Button) patientEditLayout.findViewById(R.id.savePatientButton);

        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //get fname, lname, and type
                EditText patientFName = (EditText) getActivity().findViewById(R.id.patientFname);
                EditText patientLName = (EditText) getActivity().findViewById(R.id.patientLname);
                EditText patientDOB = (EditText) getActivity().findViewById(R.id.patientDateOfBirth);
                db.insertPatient(patientFName.getText().toString(), patientLName.getText().toString(), patientDOB.getText().toString());
            }
        });
    }
}
