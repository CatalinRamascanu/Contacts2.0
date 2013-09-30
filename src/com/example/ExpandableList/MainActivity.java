package com.example.ExpandableList;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 9/26/13
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.widget.EditText;
import android.widget.ExpandableListView;

public class MainActivity extends Activity {
    // More efficient than HashMap for mapping integers to objects
    SparseArray<Contact> groups = new SparseArray<Contact>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createData();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
        final MyExpandableListAdapter adapter = new MyExpandableListAdapter(this,listView,groups);
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

    }

    public void createData() {
        Contact contact;
        contact = new Contact("Gheorghe Ion ");
        for (int i = 0; i < 4; i++) {
            contact.children.add("Sub Item" + i);
        }
        groups.append(0, contact);
        contact = new Contact("George Popescu");
        for (int i = 0; i < 4; i++) {
            contact.children.add("Sub Item" + i);
        }
        contact = new Contact("Alexandra Poenaru");
        groups.append(1, contact);
        for (int i = 0; i < 4; i++) {
            contact.children.add("Sub Item" + i);
        }
        groups.append(2, contact);
    }

}
