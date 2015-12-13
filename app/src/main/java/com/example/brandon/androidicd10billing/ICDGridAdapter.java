package com.example.brandon.androidicd10billing;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Brandon on 10/1/2015.
 */
public class ICDGridAdapter extends BaseAdapter {

    private Context context;
    private FragmentActivity billActivity;
    private ArrayList<Integer> icd10IDs;
    public BillSystemDatabase db;

    public ICDGridAdapter(Context context, ArrayList<Integer> data, FragmentActivity billFragmentActivity) {
        billActivity = billFragmentActivity;
        icd10IDs= data;
        db = new BillSystemDatabase(billActivity);
        this.context = context;
    }

    public int getCount() {
        System.out.println("ICD10IDS size " + icd10IDs.size());
        return icd10IDs.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder=new Holder();
        View rowView;
        LayoutInflater inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.grid_row, null);
        System.out.println(rowView);

        holder.tv = (TextView)rowView.findViewById(R.id.textView);
        holder.deleteVisitCodeButton = (Button) rowView.findViewById(R.id.deleteVisitCodeButton);
        System.out.println(icd10IDs);
        String icd10IDString = ""  + icd10IDs.get(position);
        //get the string from the icd10id to put in the grid adapter

        holder.tv.setText(db.getICD10WithID(icd10IDs.get(position)));

        return rowView;
    }

    public class Holder
    {
        TextView tv;
        Button deleteVisitCodeButton;
    }
}
