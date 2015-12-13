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
public class PatientListFragment extends Fragment {

    private FragmentActivity patientActivity;
    private RelativeLayout patientLayout;
    private ListView lv;
    public BillSystemDatabase db;
    ListAdapter adapter;
    Cursor patientsCursor;

    public PatientListFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        patientActivity = (FragmentActivity) super.getActivity();

        patientLayout = (RelativeLayout) inflater.inflate(R.layout.patient_list_fragment, container, false);

        lv = (ListView) patientLayout.findViewById(R.id.patientList);
        db = new BillSystemDatabase(super.getActivity());

//        Toast.makeText(getContext(), " Arguments null ", Toast.LENGTH_LONG);
        patientsCursor = db.getPatients();
        adapter = new SimpleCursorAdapter(super.getActivity(), android.R.layout.simple_list_item_1, patientsCursor, new String[]{"f_name"}, new int[]{android.R.id.text1}, 0);

        lv.setAdapter(adapter);
        this.addPatientButtonOnClickListener();
        this.addRemovePatientOnLongClickListener();
        this.addListViewOnClick();

        return patientLayout;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateList();
    }

    /**
     * Add the onItemClickListener to the ListView
     * Navigates to sub-menu if there is one. Otherwise it goes to the detail page.
     */
    public void addListViewOnClick() {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                int pID = (int) parent.getAdapter().getItemId(position);
                //get pID of the patient

                Fragment newFragment = new PatientEditFragment(); //make the new fragment that can be a detail page or a new drill down page
                Bundle bundle = new Bundle();
                bundle.putInt("pID", pID);
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = patientActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.patient_list_fragment_layout, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }


    private void addPatientButtonOnClickListener(){
       Button addPatientButton = (Button) patientLayout.findViewById(R.id.addPatientButton);

        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //navigate to edit page
                Fragment newFragment = new PatientEditFragment();
                FragmentTransaction transaction = patientActivity.getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.patient_list_fragment_layout, newFragment);
                transaction.commit();
            }
        });
    }

    public void addRemovePatientOnLongClickListener(){
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                int pID = (int) parent.getAdapter().getItemId(position);
                deletePatientDialog(pID);
                return true;
            }
        });
    }

    public void deletePatientDialog(final int pID){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Remove patient?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.deletePatient(pID);
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
        patientsCursor = db.getPatients();
        adapter = new SimpleCursorAdapter(patientActivity, android.R.layout.simple_list_item_1, patientsCursor, new String[]{"f_name"}, new int[]{android.R.id.text1}, 0);
        lv.setAdapter(adapter);
        lv.deferNotifyDataSetChanged();
    }
}