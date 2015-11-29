package com.example.brandon.androidicd10billing;

import android.text.Editable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Brandon on 11/21/2015.
 */
public class Bill {

    //Patient information
    public String date;
    public String patientName;
    public String dob;
    public String adminDoctor;
    public String referringDoctor;
    public String site;
    public String room;
    public String visitCodeToAddTo;

    //Bill is complete and if the icd10 is selected
    public boolean isComplete;
    public boolean icd10Selected;

    //Bill Code information

    //The visitCodes in the bill (used for listing in the GridView)
    private ArrayList<String> visitCodes = new ArrayList<String>();
    private HashMap<String, ArrayList<Integer>> visitCodeToICD10ID = new HashMap<String, ArrayList<Integer>>();
    private HashMap<String, Integer> visitCodeToModifierID = new HashMap<String, Integer>();
    private HashMap<Integer, ArrayList<String>> icd10IDToExtensionCode = new HashMap<Integer, ArrayList<String>>();

    public Bill() {

    }

    public String toString(){
        return date + " " + patientName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setDOB(String dob) {
        this.dob = dob;
    }

    public void setAdminDoctor(String adminDoctor) {
        this.adminDoctor = adminDoctor;
    }

    public void setReferringDoctor(String referringDoctor) {
        this.referringDoctor = referringDoctor;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setVisitCodeToAddTo(String visitCodeToAddTo){
        this.visitCodeToAddTo = visitCodeToAddTo;
    }

    public ArrayList<String> getVisitCodes() {
        return visitCodes;
    }

    public HashMap<String, ArrayList<Integer>> getVisitCodeToICD10ID(){
        return visitCodeToICD10ID;
    }

    public HashMap<String, Integer> getVisitCodeToModifierID(){
        return visitCodeToModifierID;
    }
}
