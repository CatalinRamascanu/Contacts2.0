package com.example.ExpandableList;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 9/26/13
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
import android.widget.*;
import com.example.ExpandableList.R;
import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.ArrayList;

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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contact_detail_facebook, null);
        }
        text = (TextView) convertView.findViewById(R.id.status);
        text.setText(children);
        ImageView logo = (ImageView) convertView.findViewById(R.id.logo_icon);
        ImageView mail = (ImageView) convertView.findViewById(R.id.post_icon);
        ImageView chat = (ImageView) convertView.findViewById(R.id.chat_icon);
        aux++;
        if (aux == 1){
            logo.setImageResource(R.drawable.facebook_icon);
            chat.setImageResource(R.drawable.facebook_chat_icon);
            mail.setImageResource(R.drawable.facebook_post_icon);
        }
        if (aux == 3){
            logo.setImageResource(R.drawable.yahoo_icon);
            chat.setImageResource(R.drawable.yahoo_chat_icon);
            mail.setImageResource(R.drawable.yahoo_mail_icon);
            aux = 0;
        }
        if (aux == 2){
            logo.setImageResource(R.drawable.google_icon);
            chat.setImageResource(R.drawable.google_chat_icon);
            mail.setImageResource(R.drawable.google_mail_icon);
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
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }
        Group group = (Group) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.string);
        ((CheckedTextView) convertView).setChecked(isExpanded);
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
