package com.ContactsTwoPointZero.Activities;

/**
 * Created with IntelliJ IDEA.
 * User: Cataaa
 * Date: 13.10.2013
 * Time: 13:08
 * To change this template use File | Settings | File Templates.
 */
public class ActivityProfile {
    private String googleAccount,googlePassword;
    private String facebookAccount, facebookPassword;
    private String yahooAccount, yahooPassword;

    public void setGoogleAccount(String account){
        googleAccount = account;
    }

    public void setGooglePassword(String password){
        googlePassword = password;
    }

    public void setFacebookAccount(String account){
        facebookAccount = account;
    }

    public void setFacebookPassword(String password){
        facebookPassword = password;
    }

    public void setYahooAccount(String account){
        yahooAccount = account;
    }

    public void setYahooPassword(String password){
        yahooPassword = password;
    }

    public String getGoogleAccount(){
        return googleAccount;
    }

    public String getGooglePassword(){
        return googlePassword;
    }

    public String getFacebookAccount(){
        return facebookAccount;
    }

    public String getFacebookPassword(){
        return facebookPassword;
    }

    public String getYahooAccount(){
        return yahooAccount;
    }

    public String getYahooPassword(){
        return yahooPassword;
    }
}
