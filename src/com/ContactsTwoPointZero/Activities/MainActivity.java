package com.ContactsTwoPointZero.Activities;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 9/26/13
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.widget.*;
import com.ContactsTwoPointZero.Contacts.Contact;
import com.ContactsTwoPointZero.Contacts.ContactListAdapter;
import com.ContactsTwoPointZero.Contacts.ContactManager;
import com.example.ExpandableList.R;

public class MainActivity extends Activity {
    // More efficient than HashMap for mapping integers to objects
    private SparseArray<Contact> contactList;
    private ContactListAdapter adapter;
    private ActivityProfile activityProfile;
    private ContactManager contactManager;
    private MainActivity thisActivity;
    private ExpandableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity = this;
        contactManager = new ContactManager(this);
        activityProfile = new ActivityProfile();
        startActivitySetup();

    }

    private void startActivitySetup(){
        setContentView(R.layout.setup_app);
        loadTestDataActivityProfile();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Before you can use Contacts 2.0, you need to setup your social accounts.")
               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                   }
               });
        Dialog infoDialog = builder.create();
        infoDialog.show();

        Button loadPhoneContacts = (Button) findViewById(R.id.phone_contacts);
        loadPhoneContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveActivityProfileData();
                contactManager.createTestContactsData();
                contactManager.readContactsFromPhone();
                setContentView(R.layout.activity_main);
                listView = (ExpandableListView) findViewById(R.id.listView);
                adapter = new ContactListAdapter(thisActivity, listView, contactManager.getListOfContacts());
                listView.setAdapter(adapter);
                initListeners();
            }
        });

        Button loadTestContacts = (Button) findViewById(R.id.test_contacts);
        loadTestContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveActivityProfileData();
                contactManager.createTestContactsData();
                setContentView(R.layout.activity_main);
                listView = (ExpandableListView) findViewById(R.id.listView);
                adapter = new ContactListAdapter(thisActivity, listView, contactManager.getListOfContacts());
                listView.setAdapter(adapter);
                initListeners();
            }
        });
    }

    private void startNormalActivity(){
        contactManager.createTestContactsData();
        listView = (ExpandableListView) findViewById(R.id.listView);
        adapter = new ContactListAdapter(this,listView, contactList);
        listView.setAdapter(adapter);
        initListeners();
    }

    private void initListeners(){

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editContactIntent = new Intent(thisActivity, CreateContactActivity.class);
                editContactIntent.putExtra("givenContact", adapter.getCurrentSelectedContact(position));
                startActivityForResult(editContactIntent,position);
                return true;
            }
        });

        EditText inputSearch = (EditText) findViewById(R.id.searchBox);
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.filterData(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });


        ImageButton createContact = (ImageButton) findViewById(R.id.addContact);
        createContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CreateContactActivity.class);
                MainActivity.this.startActivityForResult(myIntent,2);
            }
        });


    }

    private void saveActivityProfileData(){
        activityProfile.setGoogleAccount(((EditText) findViewById(R.id.g_acc_input)).getText().toString());
        activityProfile.setGooglePassword(((EditText) findViewById(R.id.g_pass_input)).getText().toString());

        activityProfile.setFacebookAccount(((EditText) findViewById(R.id.f_acc_input)).getText().toString());
        activityProfile.setFacebookPassword(((EditText) findViewById(R.id.f_pass_input)).getText().toString());

        activityProfile.setYahooAccount(((EditText) findViewById(R.id.y_acc_input)).getText().toString());
        activityProfile.setYahooPassword(((EditText) findViewById(R.id.y_pass_input)).getText().toString());
    }

    private void loadTestDataActivityProfile(){
        ((TextView) findViewById(R.id.g_acc_input)).setText("bot.smack21@gmail.com");
        ((TextView) findViewById(R.id.g_pass_input)).setText("Linux1234");

        ((TextView) findViewById(R.id.y_acc_input)).setText("y_smack_test@yahoo.com");
        ((TextView) findViewById(R.id.y_pass_input)).setText("Linux1234");

        ((TextView) findViewById(R.id.f_acc_input)).setText("smack.test");
        ((TextView) findViewById(R.id.f_pass_input)).setText("Linux1234");

//        activityProfile.setGoogleAccount("bot.smack21@gmail.com");
//        activityProfile.setGooglePassword("Linux1234");
//        activityProfile.setYahooAccount("y_smack_test@yahoo.com");
//        activityProfile.setYahooPassword("Linux1234");
//        activityProfile.setFacebookAccount("100006895481717");
//        activityProfile.setFacebookPassword("Linux1234");
    }



    public ActivityProfile getActivityProfile(){
        return activityProfile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (data.getExtras().containsKey("savedExistedContact") && data != null){
            Contact savedContact = (Contact) data.getExtras().getSerializable("savedExistedContact");
            adapter.updateContact(savedContact, requestCode);
        }

        if (data.getExtras().containsKey("savedNewContact") && data != null){
            Contact savedContact = (Contact) data.getExtras().getSerializable("savedNewContact");
            System.out.println("adding " + savedContact);
            adapter.addContact(savedContact);
        }
    }
}
