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
import android.util.SparseArray;
import android.widget.ExpandableListView;
import com.example.ExpandableList.R;

public class MainActivity extends Activity {
    // More efficient than HashMap for mapping integers to objects
    SparseArray<Group> groups = new SparseArray<Group>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createData();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
        MyExpandableListAdapter adapter = new MyExpandableListAdapter(this,listView,groups);
        listView.setAdapter(adapter);
    }

    public void createData() {
        for (int j = 0; j < 15; j++) {
            Group group = new Group("Test " + j);
            for (int i = 0; i < 6; i++) {
                group.children.add("Sub Item" + i);
            }
            groups.append(j, group);
        }
    }

}
