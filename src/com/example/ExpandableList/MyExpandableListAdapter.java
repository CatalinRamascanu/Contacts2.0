package com.example.ExpandableList;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 9/26/13
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
import android.widget.*;
import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private int aux;
    private final SparseArray<Group> groups;
    private int lastExpandedGroupPosition;
    private ExpandableListView listView;
    public LayoutInflater inflater;
    public Activity activity;

    public MyExpandableListAdapter(Activity act,ExpandableListView listView ,SparseArray<Group> groups) {
        aux = 0;
        activity = act;
        this.listView = listView;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String children = (String) getChild(groupPosition, childPosition);
        TextView text = null;
        System.out.println("ChildPosition:" + childPosition);
        if (convertView == null) {
            if (childPosition == 0){
              convertView = inflater.inflate(R.layout.contact_detail_phone,null);
              return convertView;
            }
            if (childPosition == 1){
                convertView = inflater.inflate(R.layout.contact_detail_social, null);
            }
        }
        if (childPosition == 0){
            return convertView;
        }
        if (childPosition == 1){
            ImageView logo = (ImageView) convertView.findViewById(R.id.logo_icon);
            logo.setImageResource(R.drawable.google_icon);

            text = (TextView) convertView.findViewById(R.id.status);
            text.setText("Online");
            ImageView statusIcon = (ImageView) convertView.findViewById(R.id.status_icon);
            statusIcon.setImageResource(R.drawable.online_icon);

            ImageButton chatButton = (ImageButton) convertView.findViewById(R.id.chat_icon);
            chatButton.setImageResource(R.drawable.google_chat_icon);

            ImageButton mailButton = (ImageButton) convertView.findViewById(R.id.mail_icon);
            mailButton.setImageResource(R.drawable.google_mail_icon);
            return convertView;
        }


        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, children,
                        Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();
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
        Group group = (Group) getGroup(groupPosition);
//        ((CheckedTextView) convertView).setText(group.string);
//        ((CheckedTextView) convertView).setChecked(isExpanded);
//        Spinner spinner = (Spinner) convertView.findViewById(R.id.spinner);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity.getBaseContext() , R.array.planets_array ,android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
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
}
