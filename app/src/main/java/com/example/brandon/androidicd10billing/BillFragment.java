package com.example.brandon.androidicd10billing;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class BillFragment extends Fragment{
    private Fragment billfragment = this;

    private BillSystemDatabase db;
    private GridView gv;
    private RelativeLayout billLayout;
    private FragmentActivity billActivity;

    private boolean complete;
    //The visitCodes in the bill (used for listing in the GridView)
    private ArrayList<String> visitCodes = new ArrayList<String>();

    //The map for icd10ids
    private HashMap<String, ArrayList<Integer>> visitCodeToICD10ID = new HashMap<String, ArrayList<Integer>>();
    private HashMap<String, Integer> visitCodeToModifierID = new HashMap<String, Integer>();
    private HashMap<Integer, ArrayList<String>> icd10IDToExtensionCode = new HashMap<Integer, ArrayList<String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        billActivity  = (FragmentActivity) super.getActivity();
        billLayout = (RelativeLayout) inflater.inflate(R.layout.bill_fragment, container, false);

        db = new BillSystemDatabase(super.getActivity());
        addAutocompleteAdapters();

        gv = (GridView) billLayout.findViewById(R.id.visitCodeGridView); //changed to llLayout.findViewById
        gv.setAdapter(new GridAdapter(super.getActivity(), visitCodes, billActivity, visitCodeToICD10ID, this));

        if(getArguments() != null) {
            //icd10ID
            int icd10IDToAdd = getArguments().getInt("icd10IDToAdd");
            Toast.makeText(billActivity, "ICD10IDToADD " + getActivity().getSupportFragmentManager().getBackStackEntryCount(), Toast.LENGTH_SHORT).show();        //check if we received anything from the detail page
        }

        return billLayout;

    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.bill_fragment);
//        db = new BillSystemDatabase(this);
//        addAutocompleteAdapters();
//
//        gv = (GridView) findViewById(R.id.visitCodeGridView);
//        gv.setAdapter(new GridAdapter(this, visitCodes));
//
//    }

    /**
     * Adds the adapters to listen in and bring up a popup for user searches
     */
    public void addAutocompleteAdapters(){
        setDateForBill();
        addPatientCodeCompletionAdapter();
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
                return cur.getString(fNameIndex) + " " + cur.getString(lNameIndex);                    //return the CharSequence to put in the textview
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
                return cur.getString(fNameIndex) + " " + cur.getString(lNameIndex);
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
                return cur.getString(roomIndex);
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
                return cur.getString(roomIndex);
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
                gv.setAdapter(new GridAdapter(getActivity(), visitCodes, billActivity, visitCodeToICD10ID, billfragment));
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
                gv.setAdapter(new GridAdapter(getActivity(), visitCodes, billActivity, visitCodeToICD10ID, billfragment));
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
                gv.setAdapter(new GridAdapter(getActivity(), visitCodes, billActivity, visitCodeToICD10ID, billfragment));
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
        if(!visitCodes.contains(visitCode)){//only add the visitCode if it is not already in there
            visitCodes.add(visitCode);
            //create a space for the visit code on the map
            visitCodeToICD10ID.put(visitCode, new ArrayList<Integer>());
            visitCodeToModifierID.put(visitCode, null);
        }
    }

    public void setDateForBill(){
        TextView dateTV = (TextView) billLayout.findViewById(R.id.autocomplete_date);
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MM dd, yyyy");
        String dateString = sdf.format(date);
        dateTV.setText(dateString);
//        InputMethodManager imm = (InputMethodManager) billActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(dateTV.getWindowToken(), 0);
    }

    public void moveVisitCodeUp(){

    }

    public void moveVisitCodeDown(){

    }

    public void moveICDCodeUp(){

    }

    public void moveICDCodeDown(){

    }

    public void saveBill(){

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