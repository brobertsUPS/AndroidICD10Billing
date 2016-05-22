package com.example.brandon.gastroicd10lookup;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ICDDetailActivity extends AppCompatActivity {

    public BillSystemDatabase db;
    public int icd10ID;
    public ArrayList<String> extensionCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icddetail);
        db = new BillSystemDatabase(this);          //Set up the database

        Cursor detailInfo;
        Bundle extras = getIntent().getExtras();
        if (extras != null) { //This is a sub menu
            detailInfo = db.getCodesForICD10ID(extras.getInt("icd10ID"));
            loadDetailWithCursor(detailInfo);
        }
    }

    /**
     * Loads the information from the database into the detail activity
     */
    public void loadDetailWithCursor(Cursor detailInfo){

        String icd10_code = detailInfo.getString(detailInfo.getColumnIndex("ICD10_code"));//Get the ICD Texts from the cursor
        String icd9_code = detailInfo.getString(detailInfo.getColumnIndex("ICD9_code"));
        String icdDesription = detailInfo.getString(detailInfo.getColumnIndex("description_text"));
        //get the icd10_id

        TextView ICD10Text = (TextView)findViewById(R.id.ICD10Text);                       //Get the ICD TextViews
        TextView ICD9Text = (TextView)findViewById(R.id.ICD9Text);
        TextView ICDDescription = (TextView) findViewById(R.id.icdDescriptionText);

        ICD10Text.setText(icd10_code);                                                     //setText for ICD9Text and ICD10Text
        ICD9Text.setText(icd9_code);
        ICDDescription.setText(icdDesription);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_icddetail, menu);
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
