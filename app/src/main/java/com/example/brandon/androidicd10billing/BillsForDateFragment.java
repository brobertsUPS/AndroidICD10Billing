package com.example.brandon.androidicd10billing;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
    Button submitBillsButton;

    public BillsForDateFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        billsForDateActivity = (FragmentActivity) super.getActivity();

        billsForDateLayout = (RelativeLayout) inflater.inflate(R.layout.bills_for_date_fragment, container, false);

        lv = (ListView) billsForDateLayout.findViewById(R.id.billsForDateList);
        submitBillsButton  = (Button)billsForDateLayout.findViewById(R.id.submit_bills_button);

        db = new BillSystemDatabase(super.getActivity());

        if(getArguments() != null) {
//            System.out.println("Bills retrieved");
            String date = getArguments().getString("dateForBill");
            billsCursor = db.getBillsForDate(date);   //get out the passed date
        }


        adapter = new SimpleCursorAdapter(super.getActivity(), android.R.layout.simple_list_item_1, billsCursor, new String[]{"f_name"}, new int[]{android.R.id.text1}, 0);

        lv.setAdapter(adapter);
        this.addListViewOnClick();
        this.addSubmitBillsOnClickListener();
        updateList();

        return billsForDateLayout;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateList();
    }

    public void addSubmitBillsOnClickListener(){
        submitBillsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get all the bills for the date
                ArrayList<Bill> bills = new ArrayList<Bill>();
                int position = 0;
                do {
                    //get the bill out of the cursor
                    Cursor currentBillInfo = (Cursor) lv.getAdapter().getItem(position);
                    int aptID = (int) lv.getAdapter().getItemId(position);
                    bills.add(fillInBill(currentBillInfo, aptID));
                    position++;
                } while (billsCursor.moveToNext());

                //create the file with all of the bill information in an html table

                String html = getHtmlTable(bills);

                try {
                    String fileName = "Bills.html";
                    File root = new File(Environment.getExternalStorageDirectory(), "testDir");
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    File gpxfile = new File(root, fileName);
                    FileWriter writer = new FileWriter(gpxfile);
                    writer.append(html);
                    writer.flush();
                    writer.close();
                    sendEmail(gpxfile);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void sendEmail(File file){
        Uri path = Uri.fromFile(file); // This guy gets the job done!

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "Bills");
        i.putExtra(Intent.EXTRA_TEXT, "The bills for the submitted date are attached.");
        i.putExtra(Intent.EXTRA_STREAM, path); // Include the path
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(billsForDateActivity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public String getHtmlTable(ArrayList<Bill> bills){

        String html = "<!DOCTYPE html> <html> <head> <meta charset='UTF-8'> <title>Bills</title> </head> <body> <table border='1' style='width:100%; '> <tr><td> Admin Doc </td><td> Date </td><td> Patient Name </td><td> Patient Date of Birth </td><td> Referring Doctor </td><td> Place of Service </td><td> Room </td><td> Visit Code </td><td> ICD10 </td><td> ICD9 </td> </tr>";
        for(int i=0; i< bills.size(); i++){
            html = html + makeHTMLLine(bills.get(i));
        }
        html = html + "</table></body> </html>";
        return html;
    }

    public String makeHTMLLine(Bill bill){
        String htmlLine = "";

        if(bill.getVisitCodes() != null && bill.getVisitCodes().size() > 0) {
            String firstVisitCode = bill.getVisitCodes().get(0);
            ArrayList<Integer> icd10IDsForFirstVisitCode = bill.getVisitCodeToICD10ID().get(firstVisitCode);
            String firstICD10Code = db.getICD10WithID(icd10IDsForFirstVisitCode.get(0));
            String firstICD9Code = db.getICD9WithICD10ID(icd10IDsForFirstVisitCode.get(0));
            htmlLine = htmlLine + "<tr><td> " + bill.adminDoctor + "</td><td> " + bill.date + "</td><td> " + bill.patientName + " </td><td>" + bill.dob + "  </td><td> " + bill.adminDoctor + " </td><td> " + bill.site + " </td><td> " + bill.room + " </td><td> " + firstVisitCode + "</td><td> " + firstICD10Code + " </td><td> " + firstICD9Code +"  </td> </tr>";

            for (int i = 1; i < icd10IDsForFirstVisitCode.size(); i++) {

                htmlLine = htmlLine + "<tr> <td>  </td><td>  </td><td>  </td><td> </td><td> </td><td> </td><td> </td><td>  </td><td>  " + db.getICD10WithID(icd10IDsForFirstVisitCode.get(i)) + " </td><td> " + db.getICD9WithICD10ID(icd10IDsForFirstVisitCode.get(i))+ "  </td> </tr>";
            }

            for (int i = 1; i < bill.getVisitCodes().size(); i++) {
                String visitCode = bill.getVisitCodes().get(i);
                ArrayList<Integer> icd10IDsForVisitCode = bill.getVisitCodeToICD10ID().get(visitCode);

                for (int j = 0; j < icd10IDsForVisitCode.size(); j++) {
                    htmlLine = htmlLine + "<tr> <td>  </td><td>  </td><td>  </td><td> </td><td> </td><td> </td><td> </td><td>" + visitCode + " </td><td> " + db.getICD10WithID(icd10IDsForVisitCode.get(j)) + " </td><td>" + db.getICD9WithICD10ID(icd10IDsForVisitCode.get(j))+"  </td> </tr>";
                    visitCode = "";
                }
            }
        }
        return htmlLine;
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
                newFragment.setBill(fillInBill(billInformation, aptID));
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

    public Bill fillInBill(Cursor billInformation, int aptID){

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
//            Toast.makeText(billsForDateActivity, "doctor " + (adminFName + " " + adminLName), Toast.LENGTH_SHORT).show();
            bill.setAdminDoctor(adminFName + " " + adminLName);
        }

        //get the visit codes
        Cursor visitCodesCursor = db.getVisitCodesForBill(aptID);
        ArrayList<String> visitCodes = new ArrayList<String>();
        if(visitCodesCursor.moveToFirst()) {
            do {
                visitCodes.add(visitCodesCursor.getString(visitCodesCursor.getColumnIndex("apt_code")));
            } while (visitCodesCursor.moveToNext());
        }

        bill.setVisitCodes(visitCodes);

        //get icd10 ids
        HashMap<String, ArrayList<Integer>> visitCodeToICD10ID = new HashMap<String, ArrayList<Integer>>();
        for(int i=0; i<visitCodes.size(); i++){

            Cursor icd10ForVisitCode = db.getDiagnosesForVisitCode(aptID, visitCodes.get(i));
            ArrayList<Integer> icd10IDs = new ArrayList<Integer>();
            if(icd10ForVisitCode.moveToFirst()) {
                do {
                    icd10IDs.add(icd10ForVisitCode.getInt(icd10ForVisitCode.getColumnIndex("ICD10_ID")));
                } while (icd10ForVisitCode.moveToNext());
            }
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
