package com.example.brandon.androidicd10billing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by Brandon on 10/27/2015.
 */
public class DoctorEditFragment extends Fragment {

    public DoctorEditFragment(){

    }

    private FragmentActivity doctorEditActivity;
    private RelativeLayout doctorEditLayout;
    private BillSystemDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        doctorEditActivity = (FragmentActivity) super.getActivity();

        doctorEditLayout = (RelativeLayout) inflater.inflate(R.layout.doctor_edit, container, false);

        db = new BillSystemDatabase(super.getActivity());
        this.addDoctorSaveButtonOnClickListener();
        return doctorEditLayout;
    }

    private void addDoctorSaveButtonOnClickListener(){
        Button addDoctorButton = (Button) doctorEditLayout.findViewById(R.id.saveDoctorButton);

        addDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //get fname, lname, and type
                EditText doctorFName = (EditText) getActivity().findViewById(R.id.doctorFname);
                EditText doctorLName = (EditText) getActivity().findViewById(R.id.doctorLname);
                Switch doctorType = (Switch) getActivity().findViewById(R.id.doctorTypeSwitch);
                db.insertDoctor(doctorFName.getText().toString(), doctorLName.getText().toString(), doctorType.isChecked());
            }
        });
    }
}
