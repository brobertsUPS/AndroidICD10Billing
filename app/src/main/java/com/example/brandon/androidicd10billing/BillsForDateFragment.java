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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Brandon on 10/27/2015.
 */
public class BillsForDateFragment extends Fragment {

    private FragmentActivity billsForDateActivity;
    private RelativeLayout billsForDateLayout;
    private ListView lv;
    public BillSystemDatabase db;
    ListAdapter adapter;
    Cursor billsCursor;

    public BillsForDateFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        billsForDateActivity = (FragmentActivity) super.getActivity();

        billsForDateLayout = (RelativeLayout) inflater.inflate(R.layout.bills_for_date_fragment, container, false);

        lv = (ListView) billsForDateLayout.findViewById(R.id.billsForDateList);

        db = new BillSystemDatabase(super.getActivity());

        if(getArguments() != null) {
            System.out.println("Bills retrieved");
            String date = getArguments().getString("dateForBill");
            billsCursor = db.getBillsForDate(date);   //get out the passed date
        }


        adapter = new SimpleCursorAdapter(super.getActivity(), android.R.layout.simple_list_item_1, billsCursor, new String[]{"f_name"}, new int[]{android.R.id.text1}, 0);

        lv.setAdapter(adapter);
        this.addListViewOnClick();
        updateList();

        return billsForDateLayout;
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
                Cursor billInformation = (Cursor) parent.getAdapter().getItem(position);
                int aptID = (int) parent.getAdapter().getItemId(position);

                BillFragment newFragment = new BillFragment(); //make the new fragment that can be a detail page or a new drill down page
                newFragment.setBill(fillInBill(position, billInformation, aptID));
                Bundle bundle = new Bundle();
                bundle.putBoolean("savedBill", true);
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = billsForDateActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.bills_for_date_fragment_layout, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    public Bill fillInBill(int cursorPosition, Cursor billInformation, int aptID){

        //fill in all of the details of the bill
        Bill bill = new Bill();

        bill.setDate(billInformation.getString(billInformation.getColumnIndex("date")));

        String fName = billInformation.getString(billInformation.getColumnIndex("f_name"));
        String lName = billInformation.getString(billInformation.getColumnIndex("l_name"));
        bill.setPatientName(fName + " " + lName);

        String dob = billInformation.getString(billInformation.getColumnIndex("date_of_birth"));
        bill.setDOB(dob);

        int siteID = billInformation.getInt(billInformation.getColumnIndex("placeID"));
        Cursor site = db.getSiteWithID(siteID);
        bill.setSite(site.getString(site.getColumnIndex("place_description")));

        int roomID = billInformation.getInt(billInformation.getColumnIndex("roomID"));
        Cursor room = db.getRoomWithID(roomID);
        bill.setRoom(room.getString(room.getColumnIndex("room_description")));

        Cursor doctors = db.getDoctorsForBill(aptID);
        if(doctors.moveToFirst()) {
            String referringFName = doctors.getString(doctors.getColumnIndex("f_name"));
            String referringLName = doctors.getString(doctors.getColumnIndex("l_name"));
            bill.setReferringDoctor(referringFName + " " + referringLName);
        }
        if(doctors.moveToNext()){
            String adminFName = doctors.getString(doctors.getColumnIndex("f_name"));
            String adminLName = doctors.getString(doctors.getColumnIndex("l_name"));
            Toast.makeText(billsForDateActivity, "doctor " + (adminFName + " " + adminLName), Toast.LENGTH_SHORT).show();
            bill.setAdminDoctor(adminFName + " " + adminLName);
        }

        //get the visit codes
        Cursor visitCodesCursor = db.getVisitCodesForBill(aptID);
        ArrayList<String> visitCodes = new ArrayList<String>();
        do{
            visitCodes.add(visitCodesCursor.getString(visitCodesCursor.getColumnIndex("apt_code")));
        }while(visitCodesCursor.moveToNext());

        bill.setVisitCodes(visitCodes);

        //get icd10 ids
        HashMap<String, ArrayList<Integer>> visitCodeToICD10ID = new HashMap<String, ArrayList<Integer>>();
        for(int i=0; i<visitCodes.size(); i++){

            Cursor icd10ForVisitCode = db.getDiagnosesForVisitCode(aptID, visitCodes.get(i));
            ArrayList<Integer> icd10IDs = new ArrayList<Integer>();
            do{
                icd10IDs.add(icd10ForVisitCode.getInt(icd10ForVisitCode.getColumnIndex("ICD10_ID")));
            }while(icd10ForVisitCode.moveToNext());
            
            visitCodeToICD10ID.put(visitCodes.get(i), icd10IDs);
        }
        bill.setVisitCodeToICD10ID(visitCodeToICD10ID);
        return bill;
    }
    //
    private void updateList(){
//        datesCursor = db.getDatesForBills();
//        adapter = new SimpleCursorAdapter(billDatesActivity, android.R.layout.simple_list_item_1, datesCursor, new String[]{"date"}, new int[]{android.R.id.text1}, 0);
//        lv.setAdapter(adapter);
//        lv.deferNotifyDataSetChanged();
    }

}
