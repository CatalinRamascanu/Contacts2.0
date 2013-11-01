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
import com.ContactsTwoPointZero.Activities.*;
import com.example.ExpandableList.R;

import java.util.ArrayList;

public class ContactListAdapter extends BaseExpandableListAdapter {
    private final SparseArray<Contact> originalGroups;
    private ArrayList<Integer> originalGroupsIndex;
    private SparseArray<Contact> groups;
    private ArrayList<Integer> groupsIndex;
    private int lastExpandedGroupPosition;
    private ExpandableListView listView;
    private LayoutInflater inflater;
    private MainActivity activity;
    private boolean editableChilds;
    private View editableSocialAccount, editablePhoneList;
    private View[][] contactChildsView;
    private int currentSelectedContactPosition;
    public ContactListAdapter(MainActivity act, ExpandableListView listView, SparseArray<Contact> groups) {
        activity = act;
        this.listView = listView;
        this.groups = groups;
        originalGroups = groups;
        groupsIndex = new ArrayList<Integer>();
        originalGroupsIndex = new ArrayList<Integer>();
        for (int i = 0; i < groups.size(); i++){
            groupsIndex.add(i);
        }
        originalGroupsIndex = groupsIndex;
        inflater = act.getLayoutInflater();
        editableChilds = false;
        loadViews();
    }

    private void loadViews(){
        TextView status;
        ImageButton chatButton, mailButton;
        View phoneView , googleView, facebookView, yahooView;
        Contact contact;
        contactChildsView = new View[groups.size()][4];

        for (int i = 0; i < groups.size(); i++){

            contact = groups.get(i);
            phoneView = inflater.inflate(R.layout.contact_detail_phone, null);
            Spinner spinner = (Spinner) phoneView.findViewById(R.id.number_spinner);
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(listView.getContext(), android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            for (int j = 0; j < contact.getSizeOfPhoneList(); j++){
                adapter.add(contact.getPhoneNumber(j));
            }
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int realCurrSelectContPos = originalGroupsIndex.get(groupsIndex.get(currentSelectedContactPosition));
//                    System.out.println("Current is " + currentSelectedContactPosition + " and real is " + realCurrSelectContPos);
                    ImageView operatorLogo = (ImageView) contactChildsView[realCurrSelectContPos][0].findViewById(R.id.phone_logo);
                    if (originalGroups.get(realCurrSelectContPos).isDetectPhoneOperator()){
                        String phoneNumber = ((Spinner) contactChildsView[realCurrSelectContPos][0].findViewById(R.id.number_spinner)).getSelectedItem().toString();
                        CreateContactActivity.performOperatorDetect(phoneNumber,operatorLogo);
                    }
                    else{
                        operatorLogo.setImageResource(R.drawable.phone_logo);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
            ImageButton callButton = (ImageButton) phoneView.findViewById(R.id.phone_call_button);
            final int finalI = i;
            callButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    callNumber(((Spinner) contactChildsView[finalI][0].findViewById(R.id.number_spinner)).getSelectedItem().toString());
                }
            });

            ImageButton sendSmsButton = (ImageButton) phoneView.findViewById(R.id.phone_sms_button);
            sendSmsButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendSmsToNumber((String) ((Spinner) contactChildsView[finalI][0].findViewById(R.id.number_spinner)).getSelectedItem());
                }
            });

            contactChildsView[i][0] = phoneView;

            int viewCount = 1;
            if (contact.hasFacebookAccount()){
                facebookView = inflater.inflate(R.layout.facebook_view_layout, null);
                chatButton = (ImageButton) facebookView.findViewById(R.id.chat_icon);
                chatButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFacebookChatActivity();
                    }
                });
                status = (TextView) facebookView.findViewById(R.id.status);
                status.setText(contact.getFacebookAccount());
                contactChildsView[i][viewCount++] = facebookView;
//                System.out.println(contact.getName() + " has facebook account " + contact.getFacebookAccount());
            }
            if (contact.hasGoogleAccount()){
                googleView = inflater.inflate(R.layout.google_view_layout, null);
                chatButton = (ImageButton) googleView.findViewById(R.id.chat_icon);
                chatButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startGTalkActivity();
                    }
                });

                mailButton = (ImageButton) googleView.findViewById(R.id.mail_icon);
                mailButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startEmailActivity(AccountType.GOOGLE);
                    }
                });

                status = (TextView) googleView.findViewById(R.id.status);
                status.setText(contact.getGoogleAccount().split("@gmail")[0]);
                contactChildsView[i][viewCount++] = googleView;
//                System.out.println(contact.getName() + " has google account " + contact.getGoogleAccount());
            }
            if (contact.hasYahooAccount()){
                yahooView = inflater.inflate(R.layout.yahoo_view_layout, null);
                chatButton = (ImageButton) yahooView.findViewById(R.id.chat_icon);
                chatButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startYahooChatActivity();
                    }
                });

                mailButton = (ImageButton) yahooView.findViewById(R.id.mail_icon);
                mailButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startEmailActivity(AccountType.YAHOO);
                    }
                });
                status = (TextView) yahooView.findViewById(R.id.status);
                status.setText(contact.getYahooAccount().split("@yahoo")[0]);
                contactChildsView[i][viewCount++] = yahooView;
//                System.out.println(contact.getName() + " has yahoo account " + contact.getYahooAccount());
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
        int realGroupPosition = originalGroupsIndex.get(groupsIndex.get(groupPosition));
        currentSelectedContactPosition = groupPosition;
        if (childPosition == 0){
            return contactChildsView[realGroupPosition][0];
        }
        if (childPosition > 0){
            return contactChildsView[realGroupPosition][childPosition];
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
        ImageView profilePicture = (ImageView) convertView.findViewById(R.id.contact_photo);
        if (contact.hasProfilePicture()){
            profilePicture.setImageBitmap(contact.getProfilePicture());
        }
        else{
            profilePicture.setImageResource(R.drawable.contact_photo);
        }
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
        ArrayList<Integer> newListIndex = new ArrayList<Integer>();
        query = query.toLowerCase();
        if (query.isEmpty()){
            groups = originalGroups;
            groupsIndex = originalGroupsIndex;
        }
        else{
            int j = 0;
            for (int i = 0; i < originalGroups.size(); i++){
                if (originalGroups.get(i).getName().toLowerCase().contains(query)){
                    newList.put(j++,originalGroups.get(i));
                    newListIndex.add(i);
                }
            }
            groups = newList;
            groupsIndex = newListIndex;
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
        gTalkIntent.putExtra("contactName",contact.getName());
        gTalkIntent.putExtra("userAccount", activity.getActivityProfile().getGoogleAccount());
        gTalkIntent.putExtra("userPassword", activity.getActivityProfile().getGooglePassword());
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

    private void startYahooChatActivity() {
        Contact contact = (Contact) getGroup(currentSelectedContactPosition);
        Intent yMessengerIntent = new Intent(activity,YahooChatActivity.class);
        yMessengerIntent.putExtra("userAccount", activity.getActivityProfile().getYahooAccount());
        yMessengerIntent.putExtra("userPassword", activity.getActivityProfile().getYahooPassword());
        yMessengerIntent.putExtra("yahooAccount", contact.getYahooAccount());
        yMessengerIntent.putExtra("contactName",contact.getName());
        activity.startActivity(yMessengerIntent);
    }

    private void startFacebookChatActivity() {
        Contact contact = (Contact) getGroup(currentSelectedContactPosition);
        Intent facebookChatIntent = new Intent(activity,FacebookChatActivity.class);
        facebookChatIntent.putExtra("userAccount", activity.getActivityProfile().getFacebookAccount());
        System.out.println("PASSWORD:" + activity.getActivityProfile().getFacebookPassword());
        facebookChatIntent.putExtra("userPassword", activity.getActivityProfile().getFacebookPassword());
        facebookChatIntent.putExtra("facebookAccount", contact.getFacebookAccount());
        facebookChatIntent.putExtra("contactName",contact.getName());
        activity.startActivity(facebookChatIntent);
    }

    public Contact getCurrentSelectedContact(int currentContactIndex){
        return originalGroups.get(originalGroupsIndex.get(groupsIndex.get(currentContactIndex)));
    }

    public void updateContact(Contact contact, int contactPosition){
        int realContactPosition = originalGroupsIndex.get(groupsIndex.get(contactPosition));
        originalGroups.remove(realContactPosition);
        originalGroups.put(realContactPosition, contact);
        groups = originalGroups;
        loadViews();
        notifyDataSetChanged();
    }

    public void addContact(Contact contact){
        originalGroups.put(originalGroups.size(), contact);
        originalGroupsIndex.add(originalGroupsIndex.size());
        groups = originalGroups;
        groupsIndex = originalGroupsIndex;
        loadViews();
        notifyDataSetChanged();
    }
}
