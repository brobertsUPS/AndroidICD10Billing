package com.example.brandon.androidicd10billing;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * Created by Brandon on 10/27/2015.
 */
public class DoctorListFragment extends Fragment {

    private FragmentActivity doctorActivity;
    private RelativeLayout doctorLayout;
    private ListView lv;
    public BillSystemDatabase db;
    ListAdapter adapter;
    Cursor doctorsCursor;

    public DoctorListFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        doctorActivity = (FragmentActivity) super.getActivity();

        doctorLayout = (RelativeLayout) inflater.inflate(R.layout.doctor_list_fragment, container, false);

        lv = (ListView) doctorLayout.findViewById(R.id.doctorList);
        db = new BillSystemDatabase(super.getActivity());

        Toast.makeText(getContext(), " Arguments null " ,Toast.LENGTH_LONG);
        doctorsCursor = db.getDoctors();
        adapter = new SimpleCursorAdapter(super.getActivity(), android.R.layout.simple_list_item_1, doctorsCursor, new String[]{"f_name"}, new int[]{android.R.id.text1}, 0);

        lv.setAdapter(adapter);
        this.addDoctorButtonOnClickListener();
        this.addRemoveDoctorOnLongClickListener();
        updateList();

        return doctorLayout;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateList();
    }

    private void addDoctorButtonOnClickListener(){
        Button addDoctorButton = (Button) doctorLayout.findViewById(R.id.addDoctorButton);

        addDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //navigate to edit page
                Fragment newFragment = new DoctorEditFragment();
                FragmentTransaction transaction = doctorActivity.getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.doctor_list_fragment_layout, newFragment);
                transaction.commit();
            }
        });
    }

    public void addRemoveDoctorOnLongClickListener(){
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                int dID = (int) parent.getAdapter().getItemId(position);
                deleteDoctorDialog(dID);
                return true;
            }
        });
    }

    public void deleteDoctorDialog(final int dID){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Remove doctor?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.deleteDoctor(dID);
                        updateList();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateList(){
        doctorsCursor = db.getDoctors();
        adapter = new SimpleCursorAdapter(doctorActivity, android.R.layout.simple_list_item_1, doctorsCursor, new String[]{"f_name"}, new int[]{android.R.id.text1}, 0);
        lv.setAdapter(adapter);
        lv.deferNotifyDataSetChanged();
    }

}
