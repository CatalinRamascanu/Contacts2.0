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
import java.util.HashMap;
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
    private HashMap<Integer,Contact> contactList;
    private final LayoutInflater inflater;
    private ArrayList<String> contactNames;
    public EditableContactListAdapter(Activity activity, HashMap<Integer,Contact> contactList, ArrayList<String> contactNames) {
        super(activity,R.id.contactList_editable,contactNames);
        this.contactNames = contactNames;
        this.activity = activity;
        inflater = activity.getLayoutInflater();
        this.contactList = contactList;
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

    public void updateListOfContacts(Contact contact,int position){
        System.out.println(contact);
        contactList.remove(position);
        contactList.put(position, contact);
        contactNames.remove(position);
        contactNames.add(position,contact.getName());
        System.out.println(contactList);

        notifyDataSetChanged();

    }

    public Contact getContact(int position){
        return  contactList.get(position);
    }
}