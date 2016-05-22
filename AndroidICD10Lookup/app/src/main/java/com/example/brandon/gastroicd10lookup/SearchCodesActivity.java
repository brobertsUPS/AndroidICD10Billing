package com.example.brandon.gastroicd10lookup;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class SearchCodesActivity extends AppCompatActivity {

    public Cursor conditionDescriptions;
    public BillSystemDatabase db;
    public ListView lv;
    public ListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_codes);

        lv = (ListView) findViewById(R.id.ICD_conditions);
        db = new BillSystemDatabase(this);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchInput = intent.getStringExtra(SearchManager.QUERY);
            conditionDescriptions = db.searchDirectlyForCodes(searchInput);
            adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, conditionDescriptions, new String[]{"description_text","ICD10_code"}, new int[]{android.R.id.text1, android.R.id.text2}, 0);
            lv.setAdapter(adapter);
        }
        addListViewOnClick();
    }

    /**
     * Add the onItemClickListener to the ListView
     * Navigates to sub-menu if there is one. Otherwise it goes to the detail page.
     */
    public void addListViewOnClick() {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Intent i = new Intent(SearchCodesActivity.this, ICDDetailActivity.class);
                int ICD10_ID = (int) parent.getAdapter().getItemId(position);

                i.putExtra("icd10ID", ICD10_ID);
                i.putExtra("isDirectSearch", true);
                startActivity(i);
            }

        });

    }

                @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_codes, menu);
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