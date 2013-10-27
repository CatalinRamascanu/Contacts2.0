package com.ContactsTwoPointZero.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.ContactsTwoPointZero.Contacts.Contact;
import com.ContactsTwoPointZero.Contacts.ContactManager;
import com.ContactsTwoPointZero.Contacts.EditableContactListAdapter;
import com.example.ExpandableList.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Cataaa
 * Date: 22.10.2013
 * Time: 19:37
 * To change this template use File | Settings | File Templates.
 */
public class EditContactsActivity extends Activity {

    private EditContactsActivity thisActivity;
    private ListView listView;
    private EditableContactListAdapter contactListAdapter;
    private HashMap<Integer,Contact> contactList;
    private ArrayList<String> contactNames;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editable_contact_list);
        Bundle extras = getIntent().getExtras();
        contactList = (HashMap<Integer,Contact>) extras.getSerializable("contactList");
        contactNames = (ArrayList<String>) extras.getSerializable("contactNames");

        thisActivity = this;
        listView = (ListView) findViewById(R.id.contactList_editable);
        contactListAdapter = new EditableContactListAdapter(thisActivity,contactList,contactNames);
        listView.setAdapter(contactListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editContactIntent = new Intent(thisActivity, CreateContactActivity.class);
                editContactIntent.putExtra("givenContact", contactListAdapter.getContact(position));
                startActivityForResult(editContactIntent,0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (data.getExtras().containsKey("savedContact")){
            Contact savedContact = (Contact) data.getExtras().getSerializable("savedContact");
            System.out.println("Saving.");
            contactListAdapter.updateListOfContacts(savedContact, requestCode);
        }
    }

}
