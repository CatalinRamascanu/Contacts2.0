package com.example.ExpandableList;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 9/26/13
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

public class MainActivity extends Activity {
    // More efficient than HashMap for mapping integers to objects
    SparseArray<Contact> contactList = new SparseArray<Contact>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createData();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
        final MyExpandableListAdapter adapter = new MyExpandableListAdapter(this,listView, contactList);
        listView.setAdapter(adapter);
        EditText inputSearch = (EditText) findViewById(R.id.editText);
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
                Intent myIntent = new Intent(MainActivity.this, createContactActivity.class);
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
