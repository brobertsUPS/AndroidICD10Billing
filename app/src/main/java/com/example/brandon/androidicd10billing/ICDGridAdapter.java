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

    public ICDGridAdapter(Context context, ArrayList<Integer> data, FragmentActivity billFragmentActivity) {
        billActivity = billFragmentActivity;
        icd10IDs= data;
        this.context = context;
    }

    public int getCount() {
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

        holder.tv = (TextView)rowView.findViewById(R.id.textView);
//        holder.modButton = (Button)rowView.findViewById(R.id.modButton);
//        holder.addVisitCodeButton = (Button) rowView.findViewById(R.id.addVisitCodeButton);
        holder.deleteVisitCodeButton = (Button) rowView.findViewById(R.id.deleteVisitCodeButton);

        holder.tv.setText(icd10IDs.get(position));

        //begin the IC10 search
//        holder.addVisitCodeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment drillDownFragment = new DrillDownCodeSearchFragment();
//                //bundle and args
//                FragmentTransaction transaction = billActivity.getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.bill_fragment_layout, drillDownFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });

        return rowView;
    }

    public class Holder
    {
        TextView tv;
        Button modButton;
        Button addVisitCodeButton;
        Button deleteVisitCodeButton;
    }
}
