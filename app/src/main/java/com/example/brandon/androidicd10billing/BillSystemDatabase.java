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

    public Cursor searchPatients(String patientInput){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {"%" + patientInput + "%", "%" + patientInput + "%"}; //pass the search input to both parameters in the query
        Cursor c = db.rawQuery("SELECT pID as _id, email, date_of_birth, f_name, l_name FROM Patient WHERE f_name LIKE ? OR l_name LIKE ?", args);
        c.moveToFirst();
        return c;
    }

    public Cursor searchDoctorsWithType(String doctorInput, int type){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {"%" + doctorInput + "%", "%" + doctorInput + "%", type + ""}; //pass the search input to both parameters in the query
        Cursor c = db.rawQuery("SELECT dID as _id, email, f_name, l_name, type FROM Doctor WHERE f_name LIKE ? OR l_name LIKE ? AND type=?", args);
        c.moveToFirst();
        return c;
    }

    public Cursor searchPlaceOfService(String siteInput){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {"%" + siteInput + "%"}; //pass the search input to both parameters in the query
        Cursor c = db.rawQuery("SELECT placeID AS _id, place_description FROM Place_of_service WHERE place_description LIKE ?", args);
        c.moveToFirst();
        return c;
    }

    public Cursor searchRoom(String roomInput){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {"%" + roomInput + "%"}; //pass the search input to both parameters in the query
        Cursor c = db.rawQuery("SELECT roomID as _id, room_description FROM Room WHERE room_description LIKE ?", args);
        c.moveToFirst();
        return c;
    }

    public Cursor searchVisitCodes(String cptInput, String apt_type){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {"%" + cptInput + "%", "%" + cptInput + "%", apt_type}; //pass the search input to both parameters in the query
        Cursor c = db.rawQuery("SELECT ROWID AS _id, apt_code, type_description, code_description FROM Apt_type WHERE apt_code LIKE ? OR code_description LIKE ? AND type_description=?", args);
        c.moveToFirst();
        return c;
    }

    public Cursor getDoctors(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT dID as _id, f_name FROM Doctor", new String[0]);
        c.moveToFirst();
        db.close();
        return c;
    }

    public Cursor getPatients(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT pID as _id, f_name, l_name, date_of_birth FROM Patient", new String[0]);
        c.moveToFirst();
        db.close();
        return c;
    }

    public void insertDoctor(String fName, String lName, boolean isAdminDoc){
        SQLiteDatabase db = getReadableDatabase();
        int type;
        if(isAdminDoc){
            type = 1; //set the type to admin
        }else{
            type = 0; //set the type to referring doctor
        }
        //skip the email (third arg) for now
        String[] args = {fName, lName,"", type + ""};
        db.execSQL("INSERT INTO Doctor (dID,f_name,l_name, email, type) VALUES (NULL,?,?,?,?)", args);
        db.close();
    }

    public Cursor getDoctorWithDID(int dID) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {dID + ""};
        System.out.println("DID: " + dID);
        Cursor c = db.rawQuery("SELECT * FROM Doctor WHERE dID=?", args);
        c.moveToFirst();
        return c;
    }

    public void deleteDoctor(int dID){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {dID + ""};
        db.execSQL("DELETE FROM Doctor WHERE dID=?", args);
        db.close();
    }

    public void updateDoctor(int dID, String fName, String lName, boolean isAdminDoc){
        SQLiteDatabase db = getReadableDatabase();
        int type;
        if(isAdminDoc){
            type = 1;
        }else{
            type = 0;
        }
        String[] args = {fName, lName, type + "", dID + ""};
        db.execSQL("UPDATE Doctor SET f_name=?, l_name=?, type=? WHERE dID=?", args);
        db.close();
    }

    public void insertPatient(String fName, String lName, String dateOfBirth){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {"", dateOfBirth, fName, lName,};
        db.execSQL("INSERT INTO Patient (pID,email,date_of_birth,f_name,l_name) VALUES (NULL,?,?,?,?)", args);
        db.close();
    }

    public Cursor getPatientWithPID(int pID) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {pID + ""};
        Cursor c = db.rawQuery("SELECT * FROM Patient WHERE pID=?", args);
        c.moveToFirst();
        return c;
    }

    public void deletePatient(int pID){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {pID + ""};
        db.execSQL("DELETE FROM Patient WHERE pID=?", args);
        db.close();
    }

    public void updatePatient(int pID, String fName, String lName, String dateOfBirth){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {dateOfBirth, fName, lName, pID + ""};
        db.execSQL("UPDATE Patient SET date_of_birth=?, f_name=?, l_name=? WHERE pID=?", args);
        db.close();
    }
}
