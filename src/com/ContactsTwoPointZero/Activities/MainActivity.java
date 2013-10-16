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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import com.ContactsTwoPointZero.Connections.Google.GTalkConnection;
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
    private Activity thisActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity = this;
        contactList = new SparseArray<Contact>();
        startActivitySetup();
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    new GTalkConnection("","").tryConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void startActivitySetup(){
        setContentView(R.layout.setup_app);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Before you can use Contacts 2.0, you need to setup your social accounts.")
               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                   }
               });
        Dialog infoDialog = builder.create();
        infoDialog.show();
        Button saveButton = (Button) findViewById(R.id.save_setup);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityProfile = new ActivityProfile();

                activityProfile.setGoogleAccount(((EditText) findViewById(R.id.g_acc_input)).getText().toString());
                activityProfile.setGooglePassword(((EditText) findViewById(R.id.g_pass_input)).getText().toString());

                activityProfile.setFacebookAccount(((EditText) findViewById(R.id.f_acc_input)).getText().toString());
                activityProfile.setFacebookPassword(((EditText) findViewById(R.id.f_pass_input)).getText().toString());

                activityProfile.setYahooAccount(((EditText) findViewById(R.id.y_acc_input)).getText().toString());
                activityProfile.setYahooPassword(((EditText) findViewById(R.id.y_pass_input)).getText().toString());

                final ProgressDialog mDialog = new ProgressDialog(thisActivity);
                mDialog.setMessage("Loading Contacts...");
                mDialog.setCancelable(false);
                mDialog.show();
                setContentView(R.layout.activity_main);
                final ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);

                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 0){
                            ContactListAdapter adapter = new ContactListAdapter(thisActivity,listView, contactManager.getListOfContacts());
                            listView.setAdapter(adapter);
                            mDialog.dismiss();
                        }
                        super.handleMessage(msg);
                    }
                };
                contactManager = new ContactManager(thisActivity,handler);
                try {
                    contactManager.readContacts();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }
    private void startNormalActivity(){
        createData();
//        ContactManager manager = new ContactManager(this);
//        manager.readContacts();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
        adapter = new ContactListAdapter(this,listView, contactList);
        listView.setAdapter(adapter);
        initListeners();
    }
    private void initListeners(){
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
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });


        ImageButton createContact = (ImageButton) findViewById(R.id.addContact);
        createContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CreateContactActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }
    public void createData() {
        int nrOfContacts = 0;
        Contact contact;

        contact = new Contact("Gheorghe Ion ");
        contact.addPhoneNumber("0735425123");
        contact.addPhoneNumber("0215425123");
        contact.addPhoneNumber("075552584");
        contact.setFacebookAccount("test");
        contactList.append(nrOfContacts++, contact);

        contact = new Contact("George Popescu");
        contact.addPhoneNumber("0761235123");
        contact.setFacebookAccount("test");
        contact.setYahooAccount("test");
        contact.setGoogleAccount("test");
        contactList.append(nrOfContacts++, contact);

        contact = new Contact("Alexandra Poenaru");
        contact.addPhoneNumber("023183283");
        contact.setGoogleAccount("test");
        contactList.append(nrOfContacts++, contact);

        contact = new Contact("Irina Tomescu");
        contact.addPhoneNumber("023183283");
        contact.addPhoneNumber("0735213882");
        contact.setYahooAccount("test");
        contactList.append(nrOfContacts++, contact);

    }

}