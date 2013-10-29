package com.ContactsTwoPointZero.Contacts;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 9/26/13
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */

import android.content.Intent;
import android.net.Uri;
import android.widget.*;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.ContactsTwoPointZero.Activities.EmailActivity;
import com.ContactsTwoPointZero.Activities.GTalkActivity;
import com.ContactsTwoPointZero.Activities.MainActivity;
import com.example.ExpandableList.R;

public class ContactListAdapter extends BaseExpandableListAdapter {
    private View phoneView , googleView, facebookView, yahooView, emailView;
    private final SparseArray<Contact> originalGroups;
    private SparseArray<Contact> groups;
    private int lastExpandedGroupPosition;
    private ExpandableListView listView;
    private LayoutInflater inflater;
    private MainActivity activity;
    private boolean editableChilds;
    private View editableSocialAccount, editablePhoneList;
    private View[][] socialViews;
    private int currentSelectedContactPosition;
    public ContactListAdapter(MainActivity act, ExpandableListView listView, SparseArray<Contact> groups) {
        activity = act;
        this.listView = listView;
        this.groups = groups;
        originalGroups = groups;
        inflater = act.getLayoutInflater();
        editableChilds = false;
        loadViews();
        
    }

    public void activateEditableChilds(){
        editableChilds = true;

        editablePhoneList = inflater.inflate(R.layout.contact_detail_phone,null);

        ImageButton deleteButton = ((ImageButton) editablePhoneList.findViewById(R.id.mail_icon));
        deleteButton.setImageResource(R.drawable.delete_icon);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner numberSpinner = (Spinner) editablePhoneList.findViewById(R.id.number_spinner);
                SpinnerAdapter adapter = numberSpinner.getAdapter();

            }
        });
        ((ImageButton) editablePhoneList.findViewById(R.id.chat_icon)).setImageResource(R.drawable.add_icon);
        editableSocialAccount = inflater.inflate(R.layout.editable_contact_list,null);

    }

    private void loadViews(){
        TextView status;
        ImageView logo,statusIcon;
        ImageButton chatButton, mailButton;

        phoneView = inflater.inflate(R.layout.contact_detail_phone, null);

        emailView = inflater.inflate(R.layout.contact_detail_phone, null);

        facebookView = inflater.inflate(R.layout.contact_detail_social, null);
        logo = (ImageView) facebookView.findViewById(R.id.logo_icon);
        logo.setImageResource(R.drawable.facebook_icon);

        status = (TextView) facebookView.findViewById(R.id.status);
        status.setText("Online");
        statusIcon = (ImageView) facebookView.findViewById(R.id.status_icon);
        statusIcon.setImageResource(R.drawable.online_icon);

        chatButton = (ImageButton) facebookView.findViewById(R.id.chat_icon);
        chatButton.setImageResource(R.drawable.facebook_chat_icon);

        mailButton = (ImageButton) facebookView.findViewById(R.id.mail_icon);
        mailButton.setImageResource(R.drawable.facebook_post_icon);

        googleView = inflater.inflate(R.layout.contact_detail_social, null);
        logo = (ImageView) googleView.findViewById(R.id.logo_icon);
        logo.setImageResource(R.drawable.google_icon);

        status = (TextView) googleView.findViewById(R.id.status);
        status.setText("Online");
        statusIcon = (ImageView) googleView.findViewById(R.id.status_icon);
        statusIcon.setImageResource(R.drawable.online_icon);

        chatButton = (ImageButton) googleView.findViewById(R.id.chat_icon);
        chatButton.setImageResource(R.drawable.google_chat_icon);
        chatButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startGTalkActivity();
            }
        });

        mailButton = (ImageButton) googleView.findViewById(R.id.mail_icon);
        mailButton.setImageResource(R.drawable.google_mail_icon);
        mailButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEmailActivity(AccountType.GOOGLE);
            }
        });

        yahooView = inflater.inflate(R.layout.contact_detail_social, null);
        logo = (ImageView) yahooView.findViewById(R.id.logo_icon);
        logo.setImageResource(R.drawable.yahoo_icon);

        status = (TextView) yahooView.findViewById(R.id.status);
        status.setText("Online");
        statusIcon = (ImageView) yahooView.findViewById(R.id.status_icon);
        statusIcon.setImageResource(R.drawable.online_icon);

        chatButton = (ImageButton) yahooView.findViewById(R.id.chat_icon);
        chatButton.setImageResource(R.drawable.yahoo_chat_icon);

        mailButton = (ImageButton) yahooView.findViewById(R.id.mail_icon);
        mailButton.setImageResource(R.drawable.yahoo_mail_icon);
        mailButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEmailActivity(AccountType.YAHOO);
            }
        });

        Contact contact;
        socialViews = new View[groups.size()][3];

        for (int i = 0; i < groups.size(); i++){
            contact = groups.get(i);
            int viewCount = 0;
            if (contact.hasFacebookAccount()){
                socialViews[i][viewCount++] = facebookView;
                System.out.println(contact.getName() + " has facebook account " + contact.getFacebookAccount());
            }
            if (contact.hasGoogleAccount()){
                socialViews[i][viewCount++] = googleView;
                System.out.println(contact.getName() + " has google account " + contact.getFacebookAccount());
            }
            if (contact.hasYahooAccount()){
                socialViews[i][viewCount++] = yahooView;
            }
        }
    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Contact contact = (Contact) getGroup(groupPosition);
        currentSelectedContactPosition = groupPosition;
        if (childPosition == 0){
            convertView = phoneView;
            Spinner spinner = (Spinner) phoneView.findViewById(R.id.number_spinner);
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(listView.getContext(), android.R.layout.simple_spinner_item);;
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            for (int i = 0; i < contact.getSizeOfPhoneList(); i++){
                adapter.add(contact.getPhoneNumber(i));
            }

            ImageButton callButton = (ImageButton) phoneView.findViewById(R.id.phone_call_button);
            callButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    callNumber(((Spinner) phoneView.findViewById(R.id.number_spinner)).getSelectedItem().toString());
                }
            });

            ImageButton sendSmsButton = (ImageButton) phoneView.findViewById(R.id.phone_sms_button);
            sendSmsButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendSmsToNumber((String) ((Spinner) phoneView.findViewById(R.id.number_spinner)).getSelectedItem());
                }
            });
            return convertView;
        }

        if (childPosition > 0){
            return socialViews[groupPosition][childPosition - 1];
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).getNumberOfAccounts() + 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        if(groupPosition != lastExpandedGroupPosition){
            listView.collapseGroup(lastExpandedGroupPosition);
        }

        super.onGroupExpanded(groupPosition);
        lastExpandedGroupPosition = groupPosition;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contact_element, null);
        }
        Contact contact = groups.get(groupPosition);
        TextView name = (TextView) convertView.findViewById(R.id.contact_name);
        name.setText(contact.getName());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void filterData(String query){
        SparseArray<Contact> newList = new SparseArray<Contact>();
        query = query.toLowerCase();
        if (query.isEmpty()){
           groups = originalGroups;
        }
        else{
            int j = 0;
            for (int i = 0; i < originalGroups.size(); i++){
                if (originalGroups.get(i).getName().toLowerCase().contains(query)){
                    newList.put(j++,originalGroups.get(i));
                }
            }
            groups = newList;
        }

        notifyDataSetChanged();
    }

    private void callNumber(String phoneNumber){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        activity.startActivity(callIntent);
    }

    private void sendSmsToNumber(String phoneNumber){
        Intent smsIntent = new Intent("android.intent.action.VIEW");
        Uri data = Uri.parse("sms:" + phoneNumber);
        smsIntent.setData(data);
        activity.startActivity(smsIntent);
    }

    private void sendEmailToContact(String contactEmail){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, contactEmail);
        email.setType("message/rfc822");
        activity.startActivity(email);
    }

    private void startGTalkActivity(){
        Contact contact = (Contact) getGroup(currentSelectedContactPosition);
        Intent gTalkIntent = new Intent(activity,GTalkActivity.class);
        gTalkIntent.putExtra("googleAccount", contact.getGoogleAccount());
        activity.startActivity(gTalkIntent);
    }

    private void startEmailActivity(AccountType accountType){
        Contact contact = (Contact) getGroup(currentSelectedContactPosition);
        Intent eMailIntent = new Intent(activity,EmailActivity.class);
        if (accountType.equals(AccountType.GOOGLE)){
            eMailIntent.putExtra("recipientAddress", contact.getGoogleAccount());
        }
        if (accountType.equals(AccountType.YAHOO)){
            eMailIntent.putExtra("recipientAddress", contact.getYahooAccount());
        }
        eMailIntent.putExtra("userAccount", activity.getActivityProfile().getGoogleAccount());
        eMailIntent.putExtra("userPassword", activity.getActivityProfile().getGooglePassword());
        activity.startActivity(eMailIntent);
    }

    private enum AccountType {
         YAHOO,GOOGLE;
    }
}
