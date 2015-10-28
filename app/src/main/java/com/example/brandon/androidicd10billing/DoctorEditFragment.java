package com.example.brandon.androidicd10billing;

import android.database.Cursor;
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

    EditText doctorFName;
    EditText doctorLName;
    Switch doctorType;

    private FragmentActivity doctorEditActivity;
    private RelativeLayout doctorEditLayout;
    private BillSystemDatabase db;
    private int dID = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        doctorEditActivity = (FragmentActivity) super.getActivity();

        doctorEditLayout = (RelativeLayout) inflater.inflate(R.layout.doctor_edit, container, false);

        db = new BillSystemDatabase(super.getActivity());

        doctorFName = (EditText) doctorEditLayout.findViewById(R.id.doctorFname);
        doctorLName = (EditText) doctorEditLayout.findViewById(R.id.doctorLname);
        doctorType = (Switch) doctorEditLayout.findViewById(R.id.doctorTypeSwitch);

        if(getArguments() != null) {
            dID = getArguments().getInt("dID");
            fillDoctorInfoWithDID(dID);
        }

        this.addDoctorSaveButtonOnClickListener();
        return doctorEditLayout;
    }

    private void fillDoctorInfoWithDID(int dID){
        Cursor doctorInfo = db.getDoctorWithDID(dID);
        int adminDoc = doctorInfo.getInt(doctorInfo.getColumnIndex("type"));
        doctorFName.setText(doctorInfo.getString(doctorInfo.getColumnIndex("f_name")));
        doctorLName.setText(doctorInfo.getString(doctorInfo.getColumnIndex("l_name")));
        if(adminDoc == 1){
            doctorType.setChecked(true);//set the doc as an admin
        }else{
            doctorType.setChecked(false);//set the doc as a referring doctor
        }
    }

    private void addDoctorSaveButtonOnClickListener(){
        Button addDoctorButton = (Button) doctorEditLayout.findViewById(R.id.saveDoctorButton);

        addDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(dID == -1){
                    db.insertDoctor(doctorFName.getText().toString(), doctorLName.getText().toString(), doctorType.isChecked());
                } else{
                    db.updateDoctor(dID, doctorFName.getText().toString(), doctorLName.getText().toString(), doctorType.isChecked());
                }
            }
        });
    }
}
