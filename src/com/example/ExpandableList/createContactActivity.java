package com.example.ExpandableList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import java.util.HashMap;
import java.util.LinkedList;

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
    private int sBeforeChangeLength = 0;
    private boolean isNameExtended, operatorDetectActive;
    private SparseArray<View> logoOperators;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_contact);
        inflater = this.getLayoutInflater();
        activityView = inflater.inflate(R.layout.create_contact, null);
        logoOperators = new SparseArray<View>();
        initExtendNameFunction();
        initPhoneList();
        ImageView profile = ((ImageView) findViewById(R.id.profilePicture));
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPictureIntent();
            }
        });
        initOperatorDetect();
    }
    private void initExtendNameFunction(){
        isNameExtended = false;
        extendName = (ImageButton) findViewById(R.id.extendName);
        extendName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNameExtended){
                    ((EditText) findViewById(R.id.firstName)).setHint("First Name");
                    findViewById(R.id.middleName).setVisibility(View.VISIBLE);;
                    findViewById(R.id.lastName).setVisibility(View.VISIBLE);
                    extendName.setImageResource(R.drawable.arrow_up_icon);
                    isNameExtended = true;
                }
                else{
                    ((EditText) findViewById(R.id.firstName)).setHint("Name");
                    findViewById(R.id.middleName).setVisibility(View.GONE);
                    findViewById(R.id.lastName).setVisibility(View.GONE);
                    extendName.setImageResource(R.drawable.arrow_down_icon);
                    isNameExtended = false;
                }
            }
        });
    }

    private void initPhoneList(){
        operatorDetectActive = true;
        addPhoneNumberLayout();
        addPhoneNumber = (ImageButton) findViewById(R.id.addPhoneNumber);
        addPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoneNumberLayout();
            }
        });
    }
    private void addPhoneNumberLayout(){
        final LinearLayout phoneList = (LinearLayout) findViewById(R.id.phoneListLayout);
        final View addPhoneNumberLayout = getLayoutInflater().inflate(R.layout.phone_number_layout,null);

        ImageButton deleteView = (ImageButton)  addPhoneNumberLayout.findViewById(R.id.deleteButton);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneList.removeView(addPhoneNumberLayout);
            }
        });

        logoOperators.append(logoOperators.size(), addPhoneNumberLayout.findViewById(R.id.operatorLogo));

        final EditText numberInput = (EditText) addPhoneNumberLayout.findViewById(R.id.phoneNumber);
        numberInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (numberInput.isFocused()){
                    numberInput.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() > sBeforeChangeLength && s.length() > 2){
                                if (s.charAt(0) == '0'){
                                    if (s.charAt(1) == '0'){
                                        if (start == 3 || start == 7 || start == 11){
                                            numberInput.setText(s + " ");
                                        }
                                    }
                                    else{
                                        if (start == 3 || start == 7){
                                            numberInput.setText(s + " ");
                                            numberInput.setSelection(numberInput.length());
                                        }
                                    }
                                }
                            }
                            sBeforeChangeLength = s.length();
                            performOperatorDetect(s, ((ImageView) addPhoneNumberLayout.findViewById(R.id.operatorLogo)));
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
                }

            }
        });
        phoneList.addView(addPhoneNumberLayout);
    }

    private void initOperatorDetect(){
        ((CheckBox) findViewById(R.id.operatorDetect)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                operatorDetectActive = isChecked;
            }
        });
    }

    private void performOperatorDetect(CharSequence number, ImageView operatorLogo){

        if (operatorDetectActive){
            if (number.length() > 2 && number.charAt(0) == '0'){
                if (number.charAt(1) == '7'){
                    if (number.charAt(2) == '2' || number.charAt(2) == '3'){
                        operatorLogo.setImageResource(R.drawable.vodafone_icon);
                    }
                    if (number.charAt(2) == '4' || number.charAt(2) == '5'){
                        operatorLogo.setImageResource(R.drawable.orange_icon);

                    }
                    if (number.charAt(2) == '6' || number.charAt(2) == '8'){
                        operatorLogo.setImageResource(R.drawable.cosmote_icon);
                    }
                }
            }
        }
        else{
            operatorLogo.setImageResource(R.drawable.phone_logo);
        }

    }
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private void getPictureIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                Bitmap photo = BitmapFactory.decodeFile(selectedImagePath);
                ((ImageView) findViewById(R.id.profilePicture)).setImageBitmap(photo);
            }
        }
    }

    private String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
