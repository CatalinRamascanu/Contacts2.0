package com.ContactsTwoPointZero.Contacts;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.SparseArray;
import android.widget.Toast;
import com.ContactsTwoPointZero.TestPack.SerialBitmap;
import com.example.ExpandableList.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 10/11/13
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */

public class ContactManager{
    private SparseArray<Contact> listOfContacts;
    private Activity activity;
    private Handler handler;
    private int nrOfContacts;

    public ContactManager(Activity act){
        listOfContacts = new SparseArray<Contact>();
        activity = act;
        nrOfContacts = 0;
    }

    public void addContact(Contact contact){
        listOfContacts.put(nrOfContacts++,contact);
    }

    public void readContactsFromPhone(){
        ContentResolver cr = activity.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        SparseArray<Contact> contacts = new SparseArray<Contact>();
        Contact contact;
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    // Create new Contact and add Phone Numbers
                    contact = new Contact(name);
                    System.out.print(name + ": ");
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNumber = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contact.addPhoneNumber(phoneNumber);
                        System.out.print(phoneNumber+" ");
                    }
                    //close phone Cursor.
                    pCur.close();

                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        // This would allow you get several email addresses
                        // if the email addresses were stored in an array
                        String email = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                        int type = emailCur.getInt(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        String customLabel = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL));
                        CharSequence CustomemailType = ContactsContract.CommonDataKinds.Email.getTypeLabel(activity.getResources(), type, customLabel);

                        contact.addEmail(email);
                    }
                    emailCur.close();

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

    public ArrayList<String> getNamesOfContacts(){
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < listOfContacts.size(); i++){
            list.add(listOfContacts.get(i).getName());
        }
        return  list;
    }

    private void addContactToPhone(Contact contact){
        String contactName = contact.getName();
        SparseArray<String> phoneNumbers = new SparseArray<String>();
        String emailID = "email@nomail.com";
        String company = "bad";
        String jobTitle = "abcd";

        ArrayList <ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            contactName).build());

        //------------------------------------------------------ Mobile Number
        int key = 0;
        for(int i = 0; i < phoneNumbers.size(); i++) {
            key = phoneNumbers.keyAt(i);
            // get the object by the key.
            String phoneNumber = phoneNumbers.get(key);
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Email
        if (emailID != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Organization
        if (!company.equals("") && !jobTitle.equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity.getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateContact(Contact contact,int position){
        listOfContacts.remove(position);
        listOfContacts.put(position,contact);
    }
}