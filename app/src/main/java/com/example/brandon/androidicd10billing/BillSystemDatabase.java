package com.example.brandon.androidicd10billing;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Brandon on 9/14/2015.
 */
public class BillSystemDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME= "medicalBilling.sqlite";
    private static final int DATABASE_VERSION = 1;

    public BillSystemDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getRootLocations(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT LID as _id, location_name FROM Condition_location cl WHERE NOT EXISTS (SELECT * FROM Sub_location sl WHERE cl.LID = sl.LID) ORDER BY LID", new String[0]);
        c.moveToFirst();
        db.close();
        return c;
    }

    public Cursor getSubLocations(int LID){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {LID + ""};
        Cursor c = db.rawQuery("SELECT LID as _id, location_name FROM Condition_location NATURAL JOIN (SELECT * FROM Sub_location WHERE Parent_locationID=?) ORDER BY location_name", args);
        c.moveToFirst();
        db.close();
        return c;
    }

    public Cursor getICD10IDForLocation(int LID){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {LID + ""};
        Cursor c = db.rawQuery("SELECT ICD10_ID FROM ICD10_condition NATURAL JOIN characterized_by NATURAL JOIN ICD9_condition WHERE ICD10_ID=(SELECT ICD10_ID FROM Located_in WHERE LID=?)", args);
        c.moveToFirst();
        db.close();
        return c;
    }

    public Cursor getCodesForICD10ID(int ICD10_ID){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {ICD10_ID + ""};
        Cursor c = db.rawQuery("SELECT ICD10_code, description_text, ICD9_code, ICD10_ID FROM ICD10_condition NATURAL JOIN characterized_by NATURAL JOIN ICD9_condition WHERE ICD10_ID=?", args);
        c.moveToFirst();
        db.close();
        return c;
    }
    public void addLocationToFavorites(int LID){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {LID + ""};
        db.execSQL("INSERT INTO Sub_location (LID, Parent_locationID) VALUES (?, 0)", args);
        db.close();
    }

    public void deleteLocationFromFavorites(int LID){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {LID + ""};
        db.execSQL("DELETE FROM Sub_location WHERE LID=? AND parent_locationID=0", args);
        db.close();
    }

    public Cursor searchDirectlyForCodes(String searchInput) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {"%" + searchInput + "%", "%" + searchInput + "%"}; //pass the search input to both parameters in the query
        Cursor c = db.rawQuery("SELECT ICD10_ID as _id, ICD10_code, description_text FROM ICD10_condition WHERE description_text LIKE ? OR ICD10_code LIKE ?", args);
        c.moveToFirst();
        return c;
    }
}
