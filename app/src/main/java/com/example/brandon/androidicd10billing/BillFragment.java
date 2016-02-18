package com.example.brandon.androidicd10billing;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class BillFragment extends Fragment{
    private BillFragment billfragment = this;

    private BillSystemDatabase db;
    private GridView gv;
    public RelativeLayout billLayout;
    private FragmentActivity billActivity;

    private Bill bill;

    public BillFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        billActivity = (FragmentActivity) super.getActivity();
        billLayout = (RelativeLayout) inflater.inflate(R.layout.bill_fragment, container, false);

        db = new BillSystemDatabase(super.getActivity());
        addAutocompleteAdapters();

        gv = (GridView) billLayout.findViewById(R.id.visitCodeGridView); //changed to llLayout.findViewById

        //fill the bill information or create a new one
        if (bill == null) {
            bill = new Bill();
        } else {
            loadBillFromDetailPage();
        }

        gv.setAdapter(new GridAdapter(super.getActivity(), bill, billActivity, this));

        setOnClickListeners();
        return billLayout;
    }

    /**
     * Makes an alert message for when a user clicks the favorites item but don't have any favorites yet.
     */
    public void makeAlert(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Ooops!");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void saveBill(){

        captureBillInformation(bill);//update the bill with whatever the user is trying to save

        String[] names = bill.patientName.split(" ");
        int pID = -1;
        if(names.length > 1) {
            pID = db.getPatientID(names[0], names[1]);   //get the patient ID

            if(pID == -1){
                db.insertPatient(names[0], names[1], bill.dob);//patient not found so save it
                pID = db.getPatientID(names[0], names[1]);   //get the patient ID
            }
//            System.out.println("PID IN BILL " + pID);
        }else{
            makeAlert("The patient name appears to be incorrect. Please enter a patient's first and last name separated by a space.");
            return;
        }

        int siteID = db.getSiteID(bill.site);//get the site ID
        if(siteID == -1){
            db.insertSite(bill.site);
            siteID = db.getSiteID(bill.site);
        }
//        System.out.println("Site id " + siteID);

        int roomID = db.getRoomID(bill.room);//get the room ID
        if(roomID == -1){
            db.insertRoom(bill.room);
            roomID = db.getRoomID(bill.room);
        }
//        System.out.println("Room ID " + roomID);

        String[] docNames = bill.referringDoctor.split(" ");
        int referringDocID = -1;
        if(docNames.length > 1){
            referringDocID = db.getDoctorID(docNames[0], docNames[1]);       //get referring doctor ID
            if(referringDocID == -1){
                db.insertDoctor(docNames[0], docNames[1], false);
                referringDocID = db.getDoctorID(docNames[0], docNames[1]);
            }
            System.out.println("referring doc id "+referringDocID);
        }else{
            makeAlert("The referring doctor name appears to be incorrect. Please enter the referring doctor's first and last name separated by a space.");
            return;
        }

        System.out.println("Admin " + bill.adminDoctor + " referring " + bill.referringDoctor);
        String[] adminDocNames = bill.adminDoctor.split(" ");
        int adminDocID = -1;
        if(adminDocNames.length > 1){
            adminDocID = db.getDoctorID(adminDocNames[0], adminDocNames[1]);       //get admin doctor ID
            if(adminDocID == -1){
                db.insertDoctor(adminDocNames[0], adminDocNames[1], true);
                adminDocID = db.getDoctorID(adminDocNames[0], adminDocNames[1]);
            }
            System.out.println("admin doc id "+adminDocID);
        }else{
            makeAlert("The admin doctor name appears to be incorrect. Please enter the admin doctor's first and last name separated by a space.");
            return;
        }
//        System.out.println("AdminDocID " + adminDocID + " referringDocID " + referringDocID);

        saveNewBill(pID, adminDocID, referringDocID, siteID, roomID);

    }

    public void saveNewBill(int pID, int adminDocID, int referringDocID, int siteID, int roomID){

        //Switch s = (Switch) findViewById(R.id.SwitchID);

//        Switch icd10On = (Switch) billLayout.findViewById(R.id.ICD10OnSwitch);
//        Switch billComplete = (Switch) billLayout.findViewById(R.id.billCompleteSwitch);

//        boolean icd10Checked = icd10On.isChecked();
//        boolean billCompleteChecked = billComplete.isChecked();
//        int icd10Check = (icd10Checked) ? 1 : 0;
//        int billCompleteCheck = (billCompleteChecked) ? 1: 0;

        int aptID = db.addAppointmentToDatabase(pID, bill.date, siteID, roomID, 0, 0);//save with a default codeType and billComplete for now
        System.out.println("aptid " + aptID + " admin doc id " + adminDocID + " referringDocID " + referringDocID);
        db.addHasDoc(aptID, adminDocID);//add hasDoc admin
        db.addHasDoc(aptID, referringDocID);//add hasDoc referring

        saveCodesForBill(aptID);//save codes for bill

        Fragment newFragment = new BillFragment(); //make the new fragment that can be a detail page or a new drill down page
        Bundle bundle = new Bundle();
        FragmentTransaction transaction = billActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.bill_fragment_layout, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void saveCodesForBill(int aptID){

        ArrayList<String> visitCodes = bill.getVisitCodes();

        for(int i=0; i< visitCodes.size(); i++){
            String visitCode = visitCodes.get(i);
            ArrayList<Integer> diagnosesCodes = bill.getVisitCodeToICD10ID().get(visitCode);

            for(int j =0; j<diagnosesCodes.size();j++) {
//                System.out.println("aptID " + aptID + " visitCode " + visitCode + " diagnosesID " + diagnosesCodes.get(i) + " vistiCodePriority " + i + " diagnosesPriority " + j );
                db.addHasType(aptID, visitCode, diagnosesCodes.get(j), i, j, "");
            }
        }
    }

    public void setOnClickListeners(){
        Button b = (Button)billLayout.findViewById(R.id.saveButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBill();
            }
        });
    }

    /**
     * Saves all of the information in the bill form to the bill object for that form
     * @param bill the bill to update the contents for
     */
    public void captureBillInformation(Bill bill){

        AutoCompleteTextView dateTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_date); //Select the patient autocomplete textview
        bill.setDate(dateTextView.getText().toString());

        AutoCompleteTextView patientTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_patient); //Select the patient autocomplete textview
        bill.setPatientName(patientTextView.getText().toString());

        AutoCompleteTextView patientDOBTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.patient_dob); //Select the patient autocomplete textview
        bill.setDOB(patientDOBTextView.getText().toString());

        AutoCompleteTextView adminDoctorTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_admin_doctor);
        bill.setAdminDoctor(adminDoctorTextView.getText().toString());

        AutoCompleteTextView doctorTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_referring_doctor);
        bill.setReferringDoctor(doctorTextView.getText().toString());

        AutoCompleteTextView roomTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_room);
        bill.setRoom(roomTextView.getText().toString());

        AutoCompleteTextView siteTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_site);
        bill.setSite(siteTextView.getText().toString());
    }

    public void loadBillFromDetailPage(){
        //put the icd10 id with the visitCode
        AutoCompleteTextView patientTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_patient); //Select the patient autocomplete textview
        patientTextView.setText(bill.patientName);

        AutoCompleteTextView patientDOBTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.patient_dob); //Select the patient autocomplete textview
        patientDOBTextView.setText(bill.dob);

        AutoCompleteTextView adminDoctorTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_admin_doctor);
        adminDoctorTextView.setText(bill.adminDoctor);

        AutoCompleteTextView doctorTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_referring_doctor);
        doctorTextView.setText(bill.referringDoctor);

        AutoCompleteTextView roomTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_room);
        roomTextView.setText(bill.room);

        AutoCompleteTextView siteTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_site);
        siteTextView.setText(bill.site);


        //get the arguments that were passed to this fragment
        if(getArguments() != null) {

            //coming from a saved bill
            if(getArguments().getBoolean("savedBill")){
                gv = (GridView) billLayout.findViewById(R.id.visitCodeGridView);
                gv.setAdapter(new GridAdapter(getActivity(), bill, billActivity, billfragment)); //get the visit codes from the database for the bill
            }else {
                //coming from the detail page, so insert the icd10ToAdd
                //icd10ID
                int icd10IDToAdd = getArguments().getInt("icd10IDToAdd");
//                Toast.makeText(billActivity, "ICD10IDToADD " + icd10IDToAdd, Toast.LENGTH_SHORT).show();        //check if we received anything from the detail page
                //set the bill if there was one

                HashMap<String, ArrayList<Integer>> visitCodeToICD10ID = bill.getVisitCodeToICD10ID();
//                System.out.println("VISITCODETOADDTO " + bill.visitCodeToAddTo);
                ArrayList<Integer> ICD10IDs = visitCodeToICD10ID.get(bill.visitCodeToAddTo);
                ICD10IDs.add(icd10IDToAdd);

                gv = (GridView) billLayout.findViewById(R.id.visitCodeGridView);
                gv.setAdapter(new GridAdapter(getActivity(), bill, billActivity, billfragment));
            }
        }
        //clear our the visitCodeToAddTo field in the bill
        bill.visitCodeToAddTo = null;
    }

    /**
     * Adds the adapters to listen in and bring up a popup for user searches
     */
    public void addAutocompleteAdapters(){
        setDateForBill();
        addPatientCodeCompletionAdapter();
        addAdminDoctorCodeCompletionAdapter();
        addDoctorCodeCompletionAdapter();
        addSiteCompletionAdapter();
        addRoomCompletionAdapter();
        addCPTCodeCompletionAdapter();
        addPCCodeCompletionAdapter();
        addMCCodeCompletionAdapter();
    }

    public void addPatientCodeCompletionAdapter(){
        AutoCompleteTextView patientTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_patient); //Select the patient autocomplete textview
        patientTextView.setThreshold(0);                                                            //set it so the user only has to type in one letter
        SimpleCursorAdapter patientAdapter;
        patientAdapter = new SimpleCursorAdapter(super.getActivity(), android.R.layout.simple_list_item_2, null,   //make a first name and last name adapter
                new String[] {"f_name", "l_name"},
                new int[] {android.R.id.text1, android.R.id.text2},
                0);
        patientTextView.setAdapter(patientAdapter);

        patientAdapter.setFilterQueryProvider(new FilterQueryProvider() {                       //Do this any time we change something in the textview
            public Cursor runQuery(CharSequence str) {
                return db.searchPatients(str + "");
            }
        });

        patientAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() { //Call this when we click on something in the popup
            public CharSequence convertToString(Cursor cur) {
                int fNameIndex = cur.getColumnIndex("f_name");
                int lNameIndex = cur.getColumnIndex("l_name");
                String patientName = cur.getString(fNameIndex) + " " + cur.getString(lNameIndex);
                bill.setPatientName(patientName);
                return patientName;                    //return the CharSequence to put in the textview
            }
        });
    }

    public void addAdminDoctorCodeCompletionAdapter(){
        AutoCompleteTextView doctorTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_admin_doctor);
        doctorTextView.setThreshold(0);
        SimpleCursorAdapter doctorAdapter;
        doctorAdapter = new SimpleCursorAdapter(super.getActivity(), android.R.layout.simple_list_item_2, null,
                new String[] {"f_name", "l_name"}, //"code_description"
                new int[] {android.R.id.text1, android.R.id.text2},
                0);
        doctorTextView.setAdapter(doctorAdapter);

        doctorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                return db.searchDoctorsWithType(str + "", 0);
            }
        });

        doctorAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int fNameIndex = cur.getColumnIndex("f_name");
                int lNameIndex = cur.getColumnIndex("l_name");
                String doctorName = cur.getString(fNameIndex) + " " + cur.getString(lNameIndex);
                bill.setAdminDoctor(doctorName);
                return doctorName;
            }
        });
    }

    public void addDoctorCodeCompletionAdapter(){
        AutoCompleteTextView doctorTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_referring_doctor);
        doctorTextView.setThreshold(0);
        SimpleCursorAdapter doctorAdapter;
        doctorAdapter = new SimpleCursorAdapter(super.getActivity(), android.R.layout.simple_list_item_2, null,
                new String[] {"f_name", "l_name"}, //"code_description"
                new int[] {android.R.id.text1, android.R.id.text2},
                0);
        doctorTextView.setAdapter(doctorAdapter);

        doctorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                return db.searchDoctorsWithType(str + "", 1);
            }
        });

        doctorAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int fNameIndex = cur.getColumnIndex("f_name");
                int lNameIndex = cur.getColumnIndex("l_name");
                String doctorName = cur.getString(fNameIndex) + " " + cur.getString(lNameIndex);
                bill.setReferringDoctor(doctorName);
                return doctorName;
            }
        });
    }

    public void addRoomCompletionAdapter(){
        AutoCompleteTextView roomTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_room);
        roomTextView.setThreshold(0);
        SimpleCursorAdapter roomAdapter;
        roomAdapter = new SimpleCursorAdapter(super.getActivity(), android.R.layout.simple_list_item_1, null,
                new String[] {"room_description"},
                new int[] {android.R.id.text1},
                0);
        roomTextView.setAdapter(roomAdapter);

        roomAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                return db.searchRoom(str + "");
            }
        });

        roomAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int roomIndex = cur.getColumnIndex("room_description");
                String room = cur.getString(roomIndex);
                bill.setRoom(room);
                return room;
            }
        });
    }

    public void addSiteCompletionAdapter(){
        AutoCompleteTextView siteTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_site);
        siteTextView.setThreshold(0);
        SimpleCursorAdapter siteAdapter;
        siteAdapter = new SimpleCursorAdapter(super.getActivity(), android.R.layout.simple_list_item_1, null,
                new String[] {"place_description"},
                new int[] {android.R.id.text1},
                0);
        siteTextView.setAdapter(siteAdapter);

        siteAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                return db.searchPlaceOfService(str + "");
            }
        });

        siteAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int roomIndex = cur.getColumnIndex("place_description");
                String site = cur.getString(roomIndex);
                bill.setSite(site);
                return site;
            }
        });
    }

    public void addCPTCodeCompletionAdapter(){
        AutoCompleteTextView cptTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_cpt_code);
        cptTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                String visitCode = c.getString(c.getColumnIndex("apt_code"));
                addVisitCodeToDataSource(visitCode);
                gv = (GridView) billLayout.findViewById(R.id.visitCodeGridView);
                gv.setAdapter(new GridAdapter(getActivity(), bill, billActivity, billfragment));
            }
        });

        //set the adapter for displaying the results in a popup list
        cptTextView.setThreshold(0);
        SimpleCursorAdapter cptAdapter;
        cptAdapter = new SimpleCursorAdapter(super.getActivity(), android.R.layout.simple_list_item_2, null,
                new String[] {"apt_code", "code_description"}, //"code_description"
                new int[] {android.R.id.text1, android.R.id.text2},
                0);
        cptTextView.setAdapter(cptAdapter);
        cptAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                return db.searchVisitCodes(str + "", "C");
            }
        });
        cptAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                return (CharSequence)"";                        //we don't actually want to keep this data in the textview
            }
        });
    }

    public void addPCCodeCompletionAdapter(){
        AutoCompleteTextView pcTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_pc_code);
        pcTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                String visitCode = c.getString(c.getColumnIndex("apt_code"));
                addVisitCodeToDataSource(visitCode);
                gv = (GridView) billLayout.findViewById(R.id.visitCodeGridView);
                gv.setAdapter(new GridAdapter(getActivity(), bill, billActivity, billfragment));
            }
        });
        pcTextView.setThreshold(0);
        SimpleCursorAdapter pcAdapter;
        pcAdapter = new SimpleCursorAdapter(super.getActivity(), android.R.layout.simple_list_item_2, null,
                new String[] {"apt_code", "code_description"}, //"code_description"
                new int[] {android.R.id.text1, android.R.id.text2},
                0);
        pcTextView.setAdapter(pcAdapter);
        pcAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                return db.searchVisitCodes(str + "", "P");
            }
        });
        pcAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                return (CharSequence)"";
            }
        });
    }

    public void addMCCodeCompletionAdapter(){
        final AutoCompleteTextView mcTextView = (AutoCompleteTextView) billLayout.findViewById(R.id.autocomplete_mc_code);
        mcTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                String visitCode = c.getString(c.getColumnIndex("apt_code"));
                addVisitCodeToDataSource(visitCode);
                gv = (GridView) billLayout.findViewById(R.id.visitCodeGridView);
                gv.setAdapter(new GridAdapter(getActivity(), bill, billActivity, billfragment));
            }
        });
        mcTextView.setThreshold(0);
        final SimpleCursorAdapter mcAdapter;
        mcAdapter = new SimpleCursorAdapter(super.getActivity(), android.R.layout.simple_list_item_2, null,
                new String[] {"apt_code", "code_description"}, //"code_description"
                new int[] {android.R.id.text1, android.R.id.text2},
                0);
        mcTextView.setAdapter(mcAdapter);
        mcAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                return db.searchVisitCodes(str + "", "M");
            } });
        mcAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                return (CharSequence)"";
            }
        });
    }

    /**
     * Adds a visit code to the datasource to display in the gridview
     * @param visitCode the visitcode to add
     */
    public void addVisitCodeToDataSource(String visitCode){

        if(!bill.getVisitCodes().contains(visitCode)){//only add the visitCode if it is not already in there
            bill.getVisitCodes().add(visitCode);
            //create a space for the visit code on the map
            bill.getVisitCodeToICD10ID().put(visitCode, new ArrayList<Integer>());
            bill.getVisitCodeToModifierID().put(visitCode, null);
        }
    }

    public void setDateForBill(){
        TextView dateTV = (TextView) billLayout.findViewById(R.id.autocomplete_date);
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MM dd, yyyy");
        String dateString = sdf.format(date);
        dateTV.setText(dateString);
//        bill.setDate(dateString);
//        InputMethodManager imm = (InputMethodManager) billActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(dateTV.getWindowToken(), 0);
    }

    public void setBill(Bill bill){
//        System.out.println("Set BILL");
        this.bill = bill;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_bill, menu);
        //return super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}