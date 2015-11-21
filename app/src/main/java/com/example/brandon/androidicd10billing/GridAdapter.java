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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
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
    public Fragment billFragment;

    public GridView icdGridview;

    public GridAdapter(Context context, Bill bill, FragmentActivity billFragmentActivity, Fragment billFragment) {
        billActivity = billFragmentActivity;
        this.bill = bill;
        this.context = context;
        this.billFragment = billFragment;
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
        holder.modButton = (Button)rowView.findViewById(R.id.modButton);
        holder.addVisitCodeButton = (Button) rowView.findViewById(R.id.addVisitCodeButton);
        holder.deleteVisitCodeButton = (Button) rowView.findViewById(R.id.deleteVisitCodeButton);
        holder.icdGridView = (GridView) rowView.findViewById(R.id.icd_grid_view);

        holder.tv.setText(bill.getVisitCodes().get(position));

        final String visitCodeToAddTo = bill.getVisitCodes().get(position);

        //begin the IC10 search
        holder.addVisitCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrillDownCodeSearchFragment drillDownFragment = new DrillDownCodeSearchFragment();
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
        holder.icdGridView.setAdapter(new ICDGridAdapter(context, icdCodesForVisitCode, billActivity));

        return rowView;
    }

    public class Holder
    {
        TextView tv;
        Button modButton;
        Button addVisitCodeButton;
        Button deleteVisitCodeButton;
        GridView icdGridView;
    }
}
