package com.ContactsTwoPointZero.Contacts;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 9/26/13
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
import android.graphics.Bitmap;
import com.ContactsTwoPointZero.TestPack.SerialBitmap;

import java.io.Serializable;
import java.util.HashMap;

public class Contact implements Serializable {

    private static final long serialVersionUID = 46543445;
    private String firstName,middleName,lastName;
    private SerialBitmap profilePicture;
    private boolean detectPhoneOperator;
    private HashMap<Integer,String> phoneList;
    private int sizeOfPhoneList;

    private HashMap<Integer,String> emailList;
    private int sizeOfEmailList;

    private String facebookAccount;
    private String googleAccount;
    private String yahooAccount;

    public Contact() {
        phoneList = new HashMap<Integer,String>();
        emailList = new HashMap<Integer,String>();
        sizeOfPhoneList = 0;
        sizeOfEmailList = 0;
    }

    public Contact(String name) {
        this.firstName = name;
        phoneList = new HashMap<Integer,String>();
        emailList = new HashMap<Integer,String>();
        sizeOfPhoneList = 0;
        sizeOfEmailList = 0;

    }

    public void addProfilePicture(Bitmap picture){
        profilePicture = new SerialBitmap(picture);
    }

    public String getFacebookAccount() {
        return facebookAccount;
    }

    public String getGoogleAccount() {
        return googleAccount;
    }

    public String getYahooAccount() {
        return yahooAccount;
    }

    public boolean hasAtLeastOneEmail(){
        if (sizeOfEmailList > 0){
            return true;
        }
        return false;
    }

    public int getSizeOfEmailList() {
        return sizeOfEmailList;
    }

    public HashMap<Integer,String>getEmailList() {
        return emailList;
    }

    public boolean hasProfilePicture(){
        if (profilePicture != null){
            return true;
        }
        return false;
    }

    public Bitmap getProfilePicture(){
        return profilePicture.getBitmap();
    }

    public void addPhoneNumber(String phoneNumber) {
        phoneList.put(sizeOfPhoneList++, phoneNumber);
    }

    public void addEmail(String email){
        emailList.put(sizeOfEmailList++, email);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getName(){
        StringBuilder name = new StringBuilder();
        if (firstName != null){
            name.append(firstName);
        }
        if (middleName != null){
            name.append(" " + middleName);
        }
        if (lastName != null){
            name.append(" " + lastName);
        }
        return name.toString();
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

    public void setGoogleAccount(String googleAccount){
        this.googleAccount = googleAccount;
    }

    public void setYahooAccount(String yahooAccount){
        this.yahooAccount = yahooAccount;
    }

    public boolean hasFacebookAccount(){
        if (facebookAccount != null){
            return true;
        }
        return false;
    }

    public boolean hasGoogleAccount(){
        if (googleAccount != null){
            return true;
        }
        return false;
    }

    public boolean hasYahooAccount(){
        if (yahooAccount != null){
            return true;
        }
        return false;
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

    public void setProfilePicture(SerialBitmap profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String toString(){
        StringBuilder contactData = new StringBuilder();

        contactData.append("First Name: " + firstName + "\n");
        contactData.append("Middle Name: " + middleName + "\n");
        contactData.append("Last Name: " + lastName + "\n");
        contactData.append("Phone numbers:\n");

        for (int i : phoneList.keySet()){
            String phoneNumber = phoneList.get(i);
            contactData.append(phoneNumber + "\n");
        }

        contactData.append("Google Account: " + googleAccount + "\n" );
        contactData.append("Facebook Account: " + facebookAccount + "\n");
        contactData.append("Yahoo Account: " + yahooAccount + "\n");

        return contactData.toString();
    }

    public boolean isDetectPhoneOperator() {
        return detectPhoneOperator;
    }

    public void setDetectPhoneOperator(boolean detectPhoneOperator) {
        this.detectPhoneOperator = detectPhoneOperator;
    }
}
