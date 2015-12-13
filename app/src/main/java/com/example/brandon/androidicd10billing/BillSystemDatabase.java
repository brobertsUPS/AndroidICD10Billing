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

    public int getLastInsertedID(){
        int result = -1;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM SQLITE_SEQUENCE", new String[0]);
        c.moveToFirst();
        db.close();

        if(c != null && c.getCount() > 0){
            result = c.getInt(0);
        }
        return result;
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

    public void insertSite(String site){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {site};
        db.execSQL("INSERT INTO Place_of_service (placeID, place_description) VALUES (NULL, ?)", args);
        db.close();
    }

    public int getSiteID(String site){
        int result = -1;
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {site};
        Cursor c = db.rawQuery("SELECT placeID FROM Place_of_service WHERE place_description=?", args);
        c.moveToFirst();

        if (c != null && c.getCount() > 0) {
            result = c.getInt(0);
        }
        db.close();
        return result;
    }

    public Cursor getSiteWithID(int siteID){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {siteID + ""};
        System.out.println("siteID: " + siteID);
        Cursor c = db.rawQuery("SELECT * FROM Place_of_service WHERE placeID=?", args);
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

    public void insertRoom(String room){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {room};
        db.execSQL("INSERT INTO Room (roomID, room_description) VALUES (NULL, ?)", args);
        db.close();
    }

    public int getRoomID(String room){
        int result = -1;
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {room};
        Cursor c = db.rawQuery("SELECT roomID FROM Room WHERE room_description=?", args);
        c.moveToFirst();

        if (c != null && c.getCount() > 0) {
            result = c.getInt(0);
        }
        db.close();
        return result;
    }

    public Cursor getRoomWithID(int roomID){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {roomID + ""};
        System.out.println("roomID: " + roomID);
        Cursor c = db.rawQuery("SELECT * FROM Room WHERE roomID=?", args);
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

    public int getDoctorID(String fName, String lName){
        int result = -1;
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {fName, lName};
        Cursor c = db.rawQuery("SELECT dID FROM Doctor WHERE f_name=? AND l_name=?", args);
        c.moveToFirst();

        if (c != null && c.getCount() > 0) {
            result = c.getInt(0);
        }
        db.close();
        return result;
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

    public int getPatientID(String fName, String lName){

        int result = -1;
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {fName, lName};
        Cursor c = db.rawQuery("Select pID FROM Patient WHERE f_name=? AND l_name=?", args);
        c.moveToFirst();

        if (c != null && c.getCount() > 0) {
            result = c.getInt(0);
        }
        db.close();
        return result;
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

    public void addHasDoc(int aptID, int dID){

        SQLiteDatabase db = getReadableDatabase();
        String[] args = {aptID + "", dID + ""};
        db.execSQL("INSERT INTO Has_doc (aptID, dID) VALUES (?, ?)", args);
        db.close();
    }

    public int addAppointmentToDatabase(int pID, String date, int siteID, int roomID, int codeType, int billComplete){

        SQLiteDatabase db = getReadableDatabase();
        String[] args = {pID +"", date, siteID + "", roomID + "", codeType + "", billComplete + ""};
        db.execSQL("INSERT INTO Appointment (aptID, pID, date, placeID, roomID, code_type, complete) VALUES (NULL, ?, ?, ?, ?, ?, ?)", args);

        Cursor c = db.rawQuery("SELECT last_insert_rowid()", new String[0]);
        c.moveToFirst();

        int aptID = -1;
        if(c != null && c.getCount() > 0){
            aptID = c.getInt(0);
        }

        System.out.println("APTID -------->" + aptID);
        db.close();

        return aptID;
    }

    public void addHasType(int aptID, String visitCodeText, int icd10CodeID, int visitCodePriority, int icdPriority, String extensionCode){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {aptID +"", visitCodeText, icd10CodeID + "", visitCodePriority + "", icdPriority + "", extensionCode};
        db.execSQL("INSERT INTO Has_type (aptID,apt_code, ICD10_ID, visit_priority, icd_priority, extension) VALUES (?,?,?,?,?,?)", args);
        db.close();
    }

    public Cursor getDatesForBills(){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {};
        Cursor c = db.rawQuery("SELECT aptID as _id, date FROM Appointment GROUP BY date", args);
        c.moveToFirst();
        return c;
    }

    public Cursor getBillsForDate(String date){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {date};
        Cursor c = db.rawQuery("SELECT aptID as _id, pID,date_of_birth, f_name, l_name, placeID, roomID, code_type, complete, date FROM Patient NATURAL JOIN Appointment WHERE date=?", args);
        c.moveToFirst();
        return c;
    }

    public String getDateForAppointment(int aptID){
        String date = "";
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {aptID + ""};
        Cursor c = db.rawQuery("SELECT date FROM Appointment WHERE aptID=?", args);
        c.moveToFirst();

        if (c != null && c.getCount() > 0) {
            date = c.getString(0);
        }
        db.close();
        return date;
    }

    public Cursor getDoctorsForBill(int aptID){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {aptID + ""};
        Cursor c = db.rawQuery("SELECT f_name, l_name, type FROM Appointment NATURAL JOIN Has_doc NATURAL JOIN Doctor WHERE aptID=?", args);
        c.moveToFirst();
        return c;
    }

    public Cursor getVisitCodesForBill(int aptID){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {aptID + ""};
        Cursor c = db.rawQuery("SELECT apt_code, visit_priority FROM Appointment NATURAL JOIN Has_type NATURAL JOIN Apt_type WHERE aptID=? GROUP BY apt_code ORDER BY visit_priority", args);
        c.moveToFirst();
        return c;
    }

    public Cursor getDiagnosesForVisitCode(int aptID, String visitCode){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {aptID + "", visitCode};
        Cursor c = db.rawQuery("SELECT ICD10_code, ICD9_code, ICD10_ID, extension FROM Has_type NATURAL JOIN Appointment NATURAL JOIN ICD10_Condition NATURAL JOIN Characterized_by WHERE aptID=? AND apt_code=? ORDER BY icd_priority", args);
        c.moveToFirst();
        return c;
    }

    public String getICD10WithID(int ICD10ID){
        String icd10 = "";
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {ICD10ID + ""};
        Cursor c = db.rawQuery("Select ICD10_code FROM ICD10_condition WHERE ICD10_ID=?", args);
        c.moveToFirst();

        if (c != null && c.getCount() > 0) {
            icd10 = c.getString(0);
        }
        db.close();
        return icd10;
    }

    public String getICD9WithICD10ID(int ICD10ID){
        String icd9 = "";
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {ICD10ID + ""};
        Cursor c = db.rawQuery("Select ICD9_code FROM Characterized_by WHERE ICD10_ID=?", args);
        c.moveToFirst();

        if (c != null && c.getCount() > 0) {
            icd9 = c.getString(0);
        }
        db.close();
        return icd9;
    }

    public void deleteAllBills(){
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {};
        db.execSQL("DELETE FROM Appointment", args);
        db.close();
    }
}