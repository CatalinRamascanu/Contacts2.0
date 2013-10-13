package com.example.ExpandableList;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 9/26/13
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
import android.graphics.Bitmap;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Contact {

    private String name;
    private Bitmap  profilePicture;
    private SparseArray<String> phoneList;
    private int sizeOfPhoneList;

    private SparseArray<String> emailList;
    private int sizeOfEmailList;

    private String facebookAccount, facebookPassword;
    private String googleAccount, googlePassword;
    private String yahooAccount, yahooPassword;

    public Contact(String name) {
        this.name = name;
        phoneList = new SparseArray<String>();
        emailList = new SparseArray<String>();
        sizeOfPhoneList = 0;
        sizeOfEmailList = 0;
    }

    public void addProfilePicture(Bitmap picture){
        profilePicture = picture;
    }

    public void addPhoneNumber(String phoneNumber) {
        phoneList.append(sizeOfPhoneList++, phoneNumber);
    }

    public void addEmail(String email){
        emailList.append(sizeOfEmailList++,email);
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPhoneList(SparseArray<String> phoneList){
        this.phoneList = phoneList;
    }

    public String getName(){
        return name;
    }

    public String getPhoneNumber(int index){
        return phoneList.get(index);
    }
    public int getSizeOfPhoneList(){
        return sizeOfPhoneList;
    }
    public void setFacebookAccount(String facebookAccount){
        this.facebookAccount = facebookAccount;
    }

    public void setFacebookPassword(String facebookPassword){
        this.facebookPassword = facebookPassword;
    }

    public void setGoogleAccount(String googleAccount){
        this.googleAccount = googleAccount;
    }

    public void setGooglePassword(String googlePassword){
        this.googlePassword = googlePassword;
    }

    public void setYahooAccount(String yahooAccount){
        this.yahooAccount = yahooAccount;
    }

    public void setYahooPassword(String yahooPassword){
        this.yahooPassword = yahooPassword;
    }

    public boolean hasFacebookAccount(){
        if (facebookAccount != null){
            return false;
        }
        return true;
    }

    public boolean hasGoogleAccount(){
        if (googleAccount != null){
            return false;
        }
        return true;
    }

    public boolean hasYahooAccount(){
        if (yahooAccount != null){
            return false;
        }
        return true;
    }
    public int getNumberOfAccounts(){
        int nr = 0;
        if (hasFacebookAccount()){
            nr++;
        }
        if (hasGoogleAccount()){
            nr++;
        }
        if (hasYahooAccount()){
            nr++;
        }
        return nr;
    }
}
