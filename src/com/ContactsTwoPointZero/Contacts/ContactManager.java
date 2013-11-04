package com.ContactsTwoPointZero.Contacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.util.SparseArray;
import com.example.ExpandableList.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 10/11/13
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */

public class ContactManager{
    // More efficient than HashMap for mapping integers to objects
    private SparseArray<Contact> listOfContacts;
    private Activity activity;
    private int nrOfContacts;

    public ContactManager(Activity act){
        listOfContacts = new SparseArray<Contact>();
        activity = act;
        nrOfContacts = 0;
    }

    public void readContactsFromPhone(){
        ContentResolver cr = activity.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        Contact contact;
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    // Create new Contact
                    contact = new Contact(name);
                    //Get Phone Numbers
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNumber = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contact.addPhoneNumber(phoneNumber);
                        System.out.print(phoneNumber+" ");
                    }
                    pCur.close();

                    //Get Profile Picture
                    Bitmap photo = null;
                    try {
                        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(cr,
                                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id)));

                        if (inputStream != null) {
                            photo = BitmapFactory.decodeStream(inputStream);
                        }

                        if (inputStream != null) inputStream.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (photo != null){
                        contact.addProfilePicture(photo);
                    }

                    //Add to contactList
                    listOfContacts.put(nrOfContacts++,contact);
                }
            }
        }
    }

    // Test Contacts for Demo
    public void createTestContactsData() {
        Contact contact;
        contact = new Contact("Gheorghe Ion ");
        contact.setProfilePicture(new SerialBitmap(BitmapFactory.decodeResource(activity.getResources(),R.drawable.test_profile_picture_male)));
        contact.addPhoneNumber("0735 425 123");
        contact.addPhoneNumber("0215 425 123");
        contact.addPhoneNumber("0755 525 842");
        contact.setYahooAccount("smack_test_yah@yahoo.com");
        listOfContacts.put(nrOfContacts++, contact);

        contact = new Contact("George Popescu");
        contact.setProfilePicture(new SerialBitmap(BitmapFactory.decodeResource(activity.getResources(),R.drawable.test_profile_picture_male2)));
        contact.addPhoneNumber("0761 235 123");
        contact.setFacebookAccount("derek.popescu.1");
        contact.setYahooAccount("smack_test_yah@yahoo.com");
        contact.setGoogleAccount("jdoe4033@gmail.com");
        listOfContacts.put(nrOfContacts++, contact);

        contact = new Contact("Alexandra Poenaru");
        contact.setProfilePicture(new SerialBitmap(BitmapFactory.decodeResource(activity.getResources(),R.drawable.test_profile_picture_female)));
        contact.addPhoneNumber("0331 832 831");
        contact.setFacebookAccount("derek.popescu.1");
        listOfContacts.put(nrOfContacts++, contact);

        contact = new Contact("Irina Tomescu");
        contact.setProfilePicture(new SerialBitmap(BitmapFactory.decodeResource(activity.getResources(),R.drawable.test_profile_picture_female2)));
        contact.addPhoneNumber("0231 832 832");
        contact.addPhoneNumber("0785 213 882");
        contact.setGoogleAccount("jdoe4033@gmail.com");
        contact.setYahooAccount("smack_test_yah@yahoo.com");
        listOfContacts.put(nrOfContacts++, contact);

    }

    public SparseArray<Contact> getListOfContacts(){
        return listOfContacts;
    }

}