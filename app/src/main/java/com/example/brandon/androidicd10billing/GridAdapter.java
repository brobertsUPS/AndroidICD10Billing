package com.example.brandon.androidicd10billing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Brandon on 10/1/2015.
 */
public class GridAdapter extends BaseAdapter {

    private Context context;
    private FragmentActivity billActivity;
    private Bill bill;
    public BillFragment billFragment;
    public GridAdapter gAdapter;
    public RelativeLayout billLayout;

    public GridView icdGridview;

    public GridAdapter(Context context, Bill bill, FragmentActivity billFragmentActivity, BillFragment billFragment) {
        billActivity = billFragmentActivity;
        this.bill = bill;
        this.context = context;
        this.billFragment = billFragment;
        this.gAdapter = this;
    }

    public int getCount() {
        return bill.getVisitCodes().size();
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
        LayoutInflater inflater = ( LayoutInflater )billActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.grid_row, null);

        holder.tv = (TextView)rowView.findViewById(R.id.textView);
//        holder.modButton = (Button)rowView.findViewById(R.id.modButton);
        holder.addVisitCodeButton = (Button) rowView.findViewById(R.id.addVisitCodeButton);
        holder.deleteVisitCodeButton = (Button) rowView.findViewById(R.id.deleteVisitCodeButton);
        holder.visitCodeUpButton = (Button) rowView.findViewById(R.id.visitCodeUpButton);
        holder.visitCodeDownButton = (Button) rowView.findViewById(R.id.visitCodeDownButton);
        holder.icdGridView = (GridView) rowView.findViewById(R.id.icd_grid_view);

        holder.tv.setText(bill.getVisitCodes().get(position));

        final String visitCodeToAddTo = bill.getVisitCodes().get(position);

        //begin the IC10 search
        holder.addVisitCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrillDownCodeSearchFragment drillDownFragment = new DrillDownCodeSearchFragment();
                billFragment.captureBillInformation(bill);
                bill.setVisitCodeToAddTo(visitCodeToAddTo);
                drillDownFragment.setBill(bill);
                FragmentTransaction transaction = billActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.bill_fragment_layout, drillDownFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                billActivity.getSupportFragmentManager().executePendingTransactions();
            }
        });

        //Get the icd10ids that correspond to the visitcode
        System.out.println(bill.getVisitCodes().get(position));
        ArrayList<Integer> icdCodesForVisitCode = bill.getVisitCodeToICD10ID().get(bill.getVisitCodes().get(position));

        //set the adapter for this specified row
        holder.icdGridView.setAdapter(new ICDGridAdapter(context, icdCodesForVisitCode, billActivity, bill, bill.getVisitCodes().get(position)));

        addDeleteVisitCodeListener(holder, position);
        addVisitCodeUpButtonListener(holder, position);
        addVisitCodeDownButtonListener(holder, position);
        return rowView;
    }

    public void addDeleteVisitCodeListener(final Holder holder, final int position){
        holder.deleteVisitCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String visitCode = bill.getVisitCodes().get(position);
                bill.getVisitCodes().remove(visitCode);
                bill.getVisitCodeToICD10ID().remove(visitCode);
                gAdapter.notifyDataSetChanged();
            }
        });
    }

    public void addVisitCodeUpButtonListener(final Holder holder, final int position){
        holder.visitCodeUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position != 0) {
                    ArrayList<String> visitCodes = bill.getVisitCodes();
                    String visitCodeToMove = visitCodes.get(position);
                    visitCodes.remove(position);
                    visitCodes.add(position-1,visitCodeToMove);
                    gAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void addVisitCodeDownButtonListener(final Holder holder, final int position){
        holder.visitCodeDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> visitCodes = bill.getVisitCodes();
                if(position != visitCodes.size()-1) {
                    String visitCodeToMove = visitCodes.get(position);
                    visitCodes.remove(position);
                    visitCodes.add(position+1,visitCodeToMove);
                    gAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public class Holder
    {
        TextView tv;
        Button addVisitCodeButton;
        Button deleteVisitCodeButton;
        Button visitCodeUpButton;
        Button visitCodeDownButton;
        GridView icdGridView;
    }
}
