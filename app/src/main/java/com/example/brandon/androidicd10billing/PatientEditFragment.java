package com.example.brandon.androidicd10billing;

import android.database.Cursor;
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

    EditText patientFName;
    EditText patientLName;
    EditText patientDOB;

    private FragmentActivity patientEditActivity;
    private RelativeLayout patientEditLayout;
    private BillSystemDatabase db;
    private int pID = -1; //set the pID as -1 to mark it as invalid to begin

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        patientEditActivity = (FragmentActivity) super.getActivity();

        patientEditLayout = (RelativeLayout) inflater.inflate(R.layout.patient_edit, container, false);

        db = new BillSystemDatabase(super.getActivity());
        this.addPatientSaveButtonOnClickListener();

        patientFName = (EditText) patientEditLayout.findViewById(R.id.patientFname);
        patientLName = (EditText) patientEditLayout.findViewById(R.id.patientLname);
        patientDOB = (EditText) patientEditLayout.findViewById(R.id.patientDateOfBirth);

        if(getArguments() != null) {
            pID = getArguments().getInt("pID");
            fillPatientInfoWithPID(pID);
        }

        return patientEditLayout;
    }

    public void fillPatientInfoWithPID(int pID){
        //get the information from the database
        Cursor patientInfo = db.getPatientWithPID(pID);
        patientFName.setText(patientInfo.getString(patientInfo.getColumnIndex("f_name")));
        patientLName.setText(patientInfo.getString(patientInfo.getColumnIndex("l_name")));
        patientDOB.setText(patientInfo.getString(patientInfo.getColumnIndex("date_of_birth")));
    }

    private void addPatientSaveButtonOnClickListener(){
        Button addPatientButton = (Button) patientEditLayout.findViewById(R.id.savePatientButton);

        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //save the new patient
                if(pID == -1) {
                    db.insertPatient(patientFName.getText().toString(), patientLName.getText().toString(), patientDOB.getText().toString());
                }else{//update the patient that was already in the database
                    db.updatePatient(pID, patientFName.getText().toString(), patientLName.getText().toString(), patientDOB.getText().toString());
                }
            }
        });
    }
}
