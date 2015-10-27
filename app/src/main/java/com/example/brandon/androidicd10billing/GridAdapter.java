package com.example.brandon.androidicd10billing;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Brandon on 10/1/2015.
 */
public class GridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> texts;
    public HashMap<String, ArrayList<Integer>> visitCodeToICD10ID;
    public HashMap<String, Integer> visitCodeToModifierID;
    public HashMap<Integer, ArrayList<String>> icd10IDToExtensionCode;

    public GridAdapter(Context context, ArrayList<String> data) {
        texts = data;
        this.context = context;
    }

    public int getCount() {
        return texts.size();
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
        holder.modButton = (Button)rowView.findViewById(R.id.modButton);
        holder.addVisitCodeButton = (Button) rowView.findViewById(R.id.addVisitCodeButton);
        holder.deleteVisitCodeButton = (Button) rowView.findViewById(R.id.deleteVisitCodeButton);

        holder.tv.setText(texts.get(position));

        //begin the IC10 search
        holder.addVisitCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DrillDownCodeSearchFragment.class);
                context.startActivity(i);
            }
        });

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
