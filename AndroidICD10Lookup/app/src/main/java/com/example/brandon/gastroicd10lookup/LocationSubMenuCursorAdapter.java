package com.example.brandon.gastroicd10lookup;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Brandon on 9/24/2015.
 */
public class LocationSubMenuCursorAdapter extends SimpleCursorAdapter {


    public LocationSubMenuCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.condition_location_row, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find fields to populate in inflated template
        TextView locationNameView = (TextView) view.findViewById(R.id.text1);
        Button addFavoriteButton = (Button) view.findViewById(R.id.addFavoriteButton);

        // Extract properties from cursor
        String locationNameText = cursor.getString(cursor.getColumnIndexOrThrow("location_name"));
        int LID = cursor.getInt(cursor.getColumnIndex("_id"));
        addFavoriteButton.setTag(LID);

        // Populate fields with extracted properties
        locationNameView.setText(locationNameText);
    }
}
