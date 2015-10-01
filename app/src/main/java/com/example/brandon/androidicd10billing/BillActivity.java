package com.example.brandon.androidicd10billing;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class BillActivity extends AppCompatActivity{

    public BillSystemDatabase db;
    public GridView gv;

    public boolean complete;
    public ArrayList<String> visitCodes = new ArrayList<String>();
    public HashMap<String, ArrayList<Integer>> visitCodeToICD10ID = new HashMap<String, ArrayList<Integer>>();
    public HashMap<String, Integer> visitCodeToModifierID = new HashMap<String, Integer>();
    public HashMap<Integer, ArrayList<String>> icd10IDToExtensionCode = new HashMap<Integer, ArrayList<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        db = new BillSystemDatabase(this);
        addAutocompleteAdapters();

        gv = (GridView) findViewById(R.id.visitCodeGridView);
        gv.setAdapter(new GridAdapter(this, visitCodes));

    }

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
        AutoCompleteTextView patientTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_patient); //Select the patient autocomplete textview
        patientTextView.setThreshold(0);                                                            //set it so the user only has to type in one letter
        SimpleCursorAdapter patientAdapter;
        patientAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,   //make a first name and last name adapter
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
        AutoCompleteTextView doctorTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_referring_doctor);
        doctorTextView.setThreshold(0);
        SimpleCursorAdapter doctorAdapter;
        doctorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
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
        AutoCompleteTextView roomTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_room);
        roomTextView.setThreshold(0);
        SimpleCursorAdapter roomAdapter;
        roomAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
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
        AutoCompleteTextView siteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_site);
        siteTextView.setThreshold(0);
        SimpleCursorAdapter siteAdapter;
        siteAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
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
        AutoCompleteTextView cptTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_cpt_code);
        cptTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                String visitCode = c.getString(c.getColumnIndex("apt_code"));
                Toast.makeText(BillActivity.this, "CPT Code Selected " + visitCode, Toast.LENGTH_SHORT).show();
                addVisitCodeToDataSource(visitCode);
                gv = (GridView) findViewById(R.id.visitCodeGridView);
                gv.setAdapter(new GridAdapter(BillActivity.this, visitCodes));
            }
        });
        cptTextView.setThreshold(0);
        SimpleCursorAdapter cptAdapter;
        cptAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
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
        AutoCompleteTextView pcTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_pc_code);
        pcTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                String visitCode = c.getString(c.getColumnIndex("apt_code"));
                addVisitCodeToDataSource(visitCode);
                gv = (GridView) findViewById(R.id.visitCodeGridView);
                gv.setAdapter(new GridAdapter(BillActivity.this, visitCodes));
                Toast.makeText(BillActivity.this, "PC Code Selected " + visitCode, Toast.LENGTH_SHORT).show();
            }
        });
        pcTextView.setThreshold(0);
        SimpleCursorAdapter pcAdapter;
        pcAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
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
        final AutoCompleteTextView mcTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_mc_code);
        mcTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                String visitCode = c.getString(c.getColumnIndex("apt_code"));
                addVisitCodeToDataSource(visitCode);
                gv = (GridView) findViewById(R.id.visitCodeGridView);
                gv.setAdapter(new GridAdapter(BillActivity.this, visitCodes));
                Toast.makeText(BillActivity.this, "MC Code Selected " + visitCode, Toast.LENGTH_SHORT).show();
            }
        });
        mcTextView.setThreshold(0);
        final SimpleCursorAdapter mcAdapter;
        mcAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
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

    public void addVisitCodeToDataSource(String visitCode){
        if(!visitCodes.contains(visitCode)){//only add the visitCode if it is not already in there
            visitCodes.add(visitCode);
        }
    }

    public void setDateForBill(){
        TextView dateTV = (TextView) findViewById(R.id.autocomplete_date);
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MM dd, yyyy");
        String dateString = sdf.format(date);
        dateTV.setText(dateString);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bill, menu);
        return true;
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