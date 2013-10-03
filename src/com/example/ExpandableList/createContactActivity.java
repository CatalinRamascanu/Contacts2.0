package com.example.ExpandableList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 10/3/13
 * Time: 10:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class createContactActivity extends Activity {
    private LayoutInflater inflater;
    private View activityView;
    private ImageButton extendName;
    private boolean isNameExtended;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_contact);
        activityView = inflater.inflate(R.layout.create_contact,null);
        inflater = this.getLayoutInflater();
        extendName = (ImageButton) findViewById(R.id.extendName);
        isNameExtended = false;

        extendName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNameExtended){
                    ((EditText) activityView.findViewById(R.id.middleName)).setVisibility(View.VISIBLE);
                    ((EditText) activityView.findViewById(R.id.lastName)).setVisibility(View.VISIBLE);
                    extendName.setImageResource(R.drawable.arrow_up_icon);
                    isNameExtended = true;
                }
                else{
                    ((EditText) activityView.findViewById(R.id.middleName)).setVisibility(View.GONE);
                    ((EditText) activityView.findViewById(R.id.lastName)).setVisibility(View.GONE);
                    extendName.setImageResource(R.drawable.arrow_down_icon);
                    isNameExtended = false;
                }


            }
        });
    }
}
