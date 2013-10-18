package com.ContactsTwoPointZero.Contacts;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.ExpandableList.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 10/18/13
 * Time: 8:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class EditableContactListAdapter extends ArrayAdapter<String> {
    private final Activity activity;
    private final SparseArray<Contact> contactList;
    private final LayoutInflater inflater;
    public EditableContactListAdapter(Activity activity, ContactManager contactManager) {
        super(activity.getApplicationContext(), R.layout.editable_contact_list, contactManager.getNamesOfContacts());
        this.activity = activity;
        inflater = activity.getLayoutInflater();
        this.contactList = contactManager.getListOfContacts();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contact_element, null);
        }
        Contact contact = contactList.get(position);
        TextView name = (TextView) convertView.findViewById(R.id.contact_name);
        name.setText(contact.getName());
        return convertView;
    }
}