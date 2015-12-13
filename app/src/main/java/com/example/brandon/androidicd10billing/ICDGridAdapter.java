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
    public ICDGridAdapter ICDAdapter;
    public Bill bill;
    public String visitCode;

    public ICDGridAdapter(Context context, ArrayList<Integer> data, FragmentActivity billFragmentActivity, Bill bill, String visitCode) {
        billActivity = billFragmentActivity;
        icd10IDs= data;
        db = new BillSystemDatabase(billActivity);
        this.context = context;
        ICDAdapter = this;
        this.bill = bill;
        this.visitCode = visitCode;
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
        rowView = inflater.inflate(R.layout.icd10_grid_row, null);
        System.out.println(rowView);

        holder.tv = (TextView)rowView.findViewById(R.id.textView);
        holder.deleteICD10CodeButton = (Button) rowView.findViewById(R.id.deleteICD10CodeButton);
        holder.ICD10UpButton = (Button) rowView.findViewById(R.id.icd10UpButton);
        holder.ICD10DownButton = (Button) rowView.findViewById(R.id.icdDownButton);
        System.out.println(icd10IDs);
        String icd10IDString = ""  + icd10IDs.get(position);
        //get the string from the icd10id to put in the grid adapter

        holder.tv.setText(db.getICD10WithID(icd10IDs.get(position)));

        addDeleteICD10CodeListener(holder, position);
        addICD10UpButtonListener(holder, position);
        addICD10DownButtonListener(holder, position);
        return rowView;
    }

    public void addDeleteICD10CodeListener(final Holder holder, final int position){
        holder.deleteICD10CodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> icd10IDsForVisitCode = bill.getVisitCodeToICD10ID().get(visitCode);
                icd10IDsForVisitCode.remove(position);
                ICDAdapter.notifyDataSetChanged();
            }
        });
    }

    public void addICD10UpButtonListener(final Holder holder, final int position){
        holder.ICD10UpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position != 0) {
                    ArrayList<Integer> icd10IDsForVisitCode = bill.getVisitCodeToICD10ID().get(visitCode);
                    int icd10ToMove = icd10IDsForVisitCode.get(position);
                    icd10IDsForVisitCode.remove(position);
                    icd10IDsForVisitCode.add(position-1,icd10ToMove);

                    ICDAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void addICD10DownButtonListener(final Holder holder, final int position){
        holder.ICD10DownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> icd10IDsForVisitCode = bill.getVisitCodeToICD10ID().get(visitCode);
                if(position != icd10IDsForVisitCode.size()-1) {
                    int icd10ToMove = icd10IDsForVisitCode.get(position);
                    icd10IDsForVisitCode.remove(position);
                    icd10IDsForVisitCode.add(position+1,icd10ToMove);
                    ICDAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public class Holder
    {
        TextView tv;
        Button deleteICD10CodeButton;
        Button ICD10UpButton;
        Button ICD10DownButton;
    }
}
