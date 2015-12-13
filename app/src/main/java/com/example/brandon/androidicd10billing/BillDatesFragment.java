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

/**
 * Created by Brandon on 10/27/2015.
 */
public class BillDatesFragment extends Fragment {

    private FragmentActivity billDatesActivity;
    private RelativeLayout billDatesLayout;
    private ListView lv;
    public BillSystemDatabase db;
    ListAdapter adapter;
    Cursor datesCursor;

    public BillDatesFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        billDatesActivity = (FragmentActivity) super.getActivity();

        billDatesLayout = (RelativeLayout) inflater.inflate(R.layout.bill_dates_fragment, container, false);

        lv = (ListView) billDatesLayout.findViewById(R.id.billDatesList);
        Button deleteAllBills = (Button) billDatesLayout.findViewById(R.id.deleteAllBills);
        addDeleteAllBillsListener(deleteAllBills);

        db = new BillSystemDatabase(super.getActivity());

        datesCursor = db.getDatesForBills();
        adapter = new SimpleCursorAdapter(super.getActivity(), android.R.layout.simple_list_item_1, datesCursor, new String[]{"date"}, new int[]{android.R.id.text1}, 0);

        lv.setAdapter(adapter);
        this.addListViewOnClick();
        updateList();

        return billDatesLayout;
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
                System.out.println("Position " + position);
                int aptID = (int) parent.getAdapter().getItemId(position);
                //get aptID
                String date = db.getDateForAppointment(aptID);
                System.out.println("aptID " + aptID + " date  " + date);


                Fragment newFragment = new BillsForDateFragment(); //make the new fragment that can be a detail page or a new drill down page
                Bundle bundle = new Bundle();
                bundle.putString("dateForBill", date);
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = billDatesActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.bill_dates_fragment_layout, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    public void addDeleteAllBillsListener(Button deleteAllBillsButton){
        deleteAllBillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllBillsDialog();
            }
        });
    }

    public void deleteAllBillsDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete All Bills?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.deleteAllBills();
                        lv.deferNotifyDataSetChanged();
                        datesCursor = db.getDatesForBills();
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
//
    private void updateList(){
//        datesCursor = db.getDatesForBills();
//        adapter = new SimpleCursorAdapter(billDatesActivity, android.R.layout.simple_list_item_1, datesCursor, new String[]{"date"}, new int[]{android.R.id.text1}, 0);
//        lv.setAdapter(adapter);
//        lv.deferNotifyDataSetChanged();
    }

}
