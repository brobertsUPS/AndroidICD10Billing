package com.example.brandon.gastroicd10lookup;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class DrillDownCodeSearchActivity extends AppCompatActivity {

    public Cursor conditionLocations;
    public BillSystemDatabase db;
    public ListView lv;             //the list of locations
    ListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drill_down_code_search);
        lv = (ListView) findViewById(R.id.conditionLocations);
        db = new BillSystemDatabase(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {                   //This is a sub menu

            if(extras.getBoolean("isFavoritesMenu"))
                addRemoveFavoritesOnLongClickListener();

            int LID = extras.getInt("lID");
            conditionLocations = db.getSubLocations(LID);
            if(LID==0){ //make a root cell (no add to favorites option) if this is the favorites sub-menu
                adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, conditionLocations, new String[]{"location_name"}, new int[]{android.R.id.text1}, 0);
            }else{
                adapter = new LocationSubMenuCursorAdapter(this, R.layout.condition_location_row, conditionLocations, new String[]{"location_name"}, new int[]{R.id.text1}, 0);
            }

        } else {                                //This is the root menu
            conditionLocations = db.getRootLocations();
            adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, conditionLocations, new String[]{"location_name"}, new int[]{android.R.id.text1}, 0);
        }
        addListViewOnClick();

        lv.setAdapter(adapter);
    }

    /**
     * Add the onItemClickListener to the ListView
     * Navigates to sub-menu if there is one. Otherwise it goes to the detail page.
     */
    public void addListViewOnClick() {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Intent i;
                int LID = (int) parent.getAdapter().getItemId(position);
                Cursor possibleSubLocations = db.getSubLocations(LID);

                if (LID == 0 && !possibleSubLocations.moveToFirst()) { //show an alert if we clicked the favorites menu and there are none
                    makeFavoritesErrorAlertDialog();
                } else {//Go to the next page (another drill down or detail page) and pass the ICD10ID

                    if (!possibleSubLocations.moveToFirst())  //go to detail
                        i = new Intent(DrillDownCodeSearchActivity.this, ICDDetailActivity.class);
                    else
                        i = new Intent(DrillDownCodeSearchActivity.this, DrillDownCodeSearchActivity.class);

                    if (LID == 0) { //if we are moving to the favorites section mark the next sub-menu as such
                        i.putExtra("isFavoritesMenu", true);
                    } else {
                        i.putExtra("isFavoritesMenu", false);
                    }

                    Cursor icd10IDCursor = db.getICD10IDForLocation(LID);//get the ICD10ID

                    if (icd10IDCursor != null && icd10IDCursor.moveToFirst()) {
                        int icd10ID = icd10IDCursor.getInt(icd10IDCursor.getColumnIndex("ICD10_ID"));
                        i.putExtra("icd10ID", icd10ID);
                    }

                    i.putExtra("lID", LID);//get the LID and pass it to the next page (could be drill down or detail page.
                    startActivity(i);
                }
            }
        });
    }

    /**
     * Pops up a dialog asking the user if they want to delete the favorite from the favorites list.
     */
    public void addRemoveFavoritesOnLongClickListener(){
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                int LID = (int) parent.getAdapter().getItemId(position);
                deleteFavoriteDialog(LID);
                return true;
            }
        });
    }

    /**
     * Registers a click with the add favorite button on a sub-menu
     * @param v the button that was clicked
     */
    public void addFavorite(View v){
        int LIDOfButtonLocation = (int) v.getTag();
        db.addLocationToFavorites(LIDOfButtonLocation);
        makeFavoritesSuccessAlertDialog();
    }

    /**
     * Popup a success alert for adding a location to the favorites list
     */
    public void makeFavoritesSuccessAlertDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(DrillDownCodeSearchActivity.this).create();
        alertDialog.setTitle("Yay!");
        alertDialog.setMessage("The item was added to the favorites list!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Makes an alert message for when a user clicks the favorites item but don't have any favorites yet.
     */
    public void makeFavoritesErrorAlertDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(DrillDownCodeSearchActivity.this).create();
        alertDialog.setTitle("Ooops!");
        alertDialog.setMessage("There does not appear to be any favorites in the database!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Pops up a dialog asking the user if they want to delete the favorite from the favorites list.
     */
    public void deleteFavoriteDialog(final int LID){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Remove favorite?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.deleteLocationFromFavorites(LID);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drill_down_code_search, menu);

        //set up how the autocomplete textview will look
        AutoCompleteTextView codeTextView = new AutoCompleteTextView(this);
        codeTextView.setHint("Search Codes");
        codeTextView.setTextColor(getResources().getColor(R.color.black));
        codeTextView.setThreshold(0);
        codeTextView.setWidth(400);

        menu.add(0, 0, 1, "").setActionView(codeTextView).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        codeTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                int icd10ID = c.getInt(c.getColumnIndex("_id"));
                Intent i = new Intent(DrillDownCodeSearchActivity.this, ICDDetailActivity.class);
                i.putExtra("icd10ID", icd10ID);
                startActivity(i);
            }
        });
        codeTextView.setThreshold(0); //start searching after 1 letter is typed

        //set the adapter
        SimpleCursorAdapter cptAdapter;
        cptAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
                new String[]{"description_text","ICD10_code"}, new int[]{android.R.id.text2, android.R.id.text1}, 0);
        codeTextView.setAdapter(cptAdapter);

        //do the search for the adapter
        cptAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                return db.searchDirectlyForCodes("" +str);
            }
        });

        //Get the string for the cursor
        cptAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int ICD10CodeIndex = cur.getColumnIndex("ICD10_code");
                return cur.getString(ICD10CodeIndex);                //we don't actually want to keep this data in the textview
            }
        });


//        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//
//        // Make the search happen for the SearchCodesActivity class
//        ComponentName cn = new ComponentName(this, SearchCodesActivity.class);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
//
//        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }
}
