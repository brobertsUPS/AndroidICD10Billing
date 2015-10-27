package com.example.brandon.androidicd10billing;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class ICDDetailFragment extends Fragment {

    public BillSystemDatabase db;
    public int icd10ID;
    public ArrayList<String> extensionCodes;
    public FragmentActivity detailActivity;
    public RelativeLayout detailLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        detailActivity = (FragmentActivity) super.getActivity();

        detailLayout = (RelativeLayout) inflater.inflate(R.layout.icd_detail_fragment, container, false);

        db = new BillSystemDatabase(detailActivity);          //Set up the database

        Cursor detailInfo;
        if(getArguments() != null) {   //This is a sub menu
            detailInfo = db.getCodesForICD10ID(getArguments().getInt("icd10ID"));
            loadDetailWithCursor(detailInfo);
        }
        return detailLayout;
    }

    /**
     * Loads the information from the database into the detail activity
     */
    public void loadDetailWithCursor(Cursor detailInfo){

        String icd10_code = detailInfo.getString(detailInfo.getColumnIndex("ICD10_code"));//Get the ICD Texts from the cursor
        String icd9_code = detailInfo.getString(detailInfo.getColumnIndex("ICD9_code"));
        String icdDesription = detailInfo.getString(detailInfo.getColumnIndex("description_text"));

        TextView ICD10Text = (TextView)detailLayout.findViewById(R.id.ICD10Text);                       //Get the ICD TextViews
        TextView ICD9Text = (TextView)detailLayout.findViewById(R.id.ICD9Text);
        TextView ICDDescription = (TextView) detailLayout.findViewById(R.id.icdDescriptionText);

        ICD10Text.setText(icd10_code);                                                     //setText for ICD9Text and ICD10Text
        ICD9Text.setText(icd9_code);
        ICDDescription.setText(icdDesription);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_icddetail, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
