package com.example.ExpandableList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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
    private ImageButton extendName, addPhoneNumber;

    private boolean isNameExtended, operatorDetectation;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_contact);
        inflater = this.getLayoutInflater();
        activityView = inflater.inflate(R.layout.create_contact, null);
        initExtendNameFunction();
        initPhoneList();
    }

    private void initPhoneList(){
        operatorDetectation = true;
        final LinearLayout phoneList = (LinearLayout) findViewById(R.id.phoneListLayout);
        final View addPhoneNumberLayout = getLayoutInflater().inflate(R.layout.add_nr_layout,null);
        ImageButton deleteView = (ImageButton)  addPhoneNumberLayout.findViewById(R.id.deleteButton);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneList.removeView(addPhoneNumberLayout);
            }
        });
        phoneList.addView(addPhoneNumberLayout);
        addPhoneNumber = (ImageButton) findViewById(R.id.addPhoneNumber);
        addPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout phoneList = (LinearLayout) findViewById(R.id.phoneListLayout);
                final View addPhoneNumberLayout = getLayoutInflater().inflate(R.layout.add_nr_layout,null);
                ImageButton deleteView = (ImageButton)  addPhoneNumberLayout.findViewById(R.id.deleteButton);
                deleteView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        phoneList.removeView(addPhoneNumberLayout);
                    }
                });
                phoneList.addView(addPhoneNumberLayout);
            }
        });
    }

    private void initExtendNameFunction(){
        isNameExtended = false;
        extendName = (ImageButton) findViewById(R.id.extendName);
        extendName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNameExtended){
                    ((EditText) findViewById(R.id.firstName)).setText("First Name");
                    ((EditText) findViewById(R.id.middleName)).setVisibility(View.VISIBLE);;
                    ((EditText) findViewById(R.id.lastName)).setVisibility(View.VISIBLE);
                    extendName.setImageResource(R.drawable.arrow_up_icon);
                    isNameExtended = true;
                }
                else{
                    ((EditText) findViewById(R.id.firstName)).setText("Name");
                    ((EditText) findViewById(R.id.middleName)).setVisibility(View.GONE);
                    ((EditText) findViewById(R.id.lastName)).setVisibility(View.GONE);
                    extendName.setImageResource(R.drawable.arrow_down_icon);
                    isNameExtended = false;
                }
            }
        });
    }
}
