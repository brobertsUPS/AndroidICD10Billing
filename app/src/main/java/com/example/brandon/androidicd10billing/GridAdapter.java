package com.example.brandon.androidicd10billing;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
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
        TextView tv;
        if (convertView == null) {
            tv = new TextView(context);
            tv.setLayoutParams(new GridView.LayoutParams(85, 85));
        }
        else {
            tv = (TextView) convertView;
        }

        tv.setText(texts.get(position));
        return tv;
    }
}
