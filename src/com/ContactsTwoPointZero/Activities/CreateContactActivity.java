package com.ContactsTwoPointZero.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.ContactsTwoPointZero.Contacts.Contact;
import com.ContactsTwoPointZero.TestPack.SerialBitmap;
import com.example.ExpandableList.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 10/3/13
 * Time: 10:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class CreateContactActivity extends Activity {
    private LayoutInflater inflater;
    private Dialog infoDialog;
    private ImageButton extendName, addPhoneNumber;
    private int sBeforeChangeLength = 0;
    private boolean isNameExtended, operatorDetectActive;
    private SparseArray<View> logoOperators;
    private Contact givenContact,savedContact;
    private ArrayList<String> phoneNumberInputData;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_contact);
        inflater = this.getLayoutInflater();
        logoOperators = new SparseArray<View>();
        phoneNumberInputData = new ArrayList<String>();
        initExtendNameFunction();
        initPhoneList();
        initProfilePhotoListener();
        initOperatorDetect();
        initSaveListener();
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            givenContact = (Contact) extras.getSerializable("givenContact");
            addDataOfContact(givenContact);
        }
        addPhoneNumberLayout(null);

    }

    private void addDataOfContact(Contact contact){
        ((EditText) findViewById(R.id.firstName)).setText(contact.getName());
        if (contact.hasProfilePicture()) {
            ((ImageView) findViewById(R.id.profilePicture)).setImageBitmap(contact.getProfilePicture());
        }

        for (int i = 0; i < contact.getSizeOfPhoneList(); i++){
            addPhoneNumberLayout(contact.getPhoneNumber(i));
        }

        if (contact.hasFacebookAccount()){
            ((EditText) findViewById(R.id.f_contact_account)).setText(contact.getFacebookAccount());
        }

        if (contact.hasGoogleAccount()){
            ((EditText) findViewById(R.id.g_contact_account)).setText(contact.getGoogleAccount());
        }

        if (contact.hasYahooAccount()){
            ((EditText) findViewById(R.id.y_contact_account)).setText(contact.getYahooAccount());
        }

    }

    private void initProfilePhotoListener(){
        ImageView profile = ((ImageView) findViewById(R.id.profilePicture));
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPictureIntent();
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
        addPhoneNumber = (ImageButton) findViewById(R.id.addPhoneNumber);
        addPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoneNumberLayout(null);
            }
        });
    }

    private void addPhoneNumberLayout(String phoneNumber){
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

        if (phoneNumber != null){
            ((EditText) addPhoneNumberLayout.findViewById(R.id.phoneNumber)).setText(phoneNumber);
            performOperatorDetect(phoneNumber, ((ImageView) addPhoneNumberLayout.findViewById(R.id.operatorLogo)));
        }

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

    private void initSaveListener(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("At least a name and a phone number must be added.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        infoDialog = builder.create();
        ((Button) findViewById(R.id.save_contact_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName, middleName, lastName;
                firstName = ((EditText) findViewById(R.id.firstName)).getText().toString();
                middleName = ((EditText) findViewById(R.id.middleName)).getText().toString();
                lastName = ((EditText) findViewById(R.id.lastName)).getText().toString();
                if (firstName.length() > 0) {
                    savedContact = new Contact();
                    savedContact.setFirstName(firstName);

                    if (middleName.length() > 0) {
                        savedContact.setMiddleName(middleName);
                    }
                    if (lastName.length() > 0) {
                        savedContact.setMiddleName(lastName);
                    }

                    ImageView profilePictureView = (ImageView) findViewById(R.id.profilePicture);
                    savedContact.setProfilePicture(new SerialBitmap(((BitmapDrawable) profilePictureView.getDrawable()).getBitmap()));

                    LinearLayout layout = (LinearLayout) findViewById(R.id.phoneListLayout);
                    String phoneNumber;
                    boolean aNumberHasBeenAdded = false;

                    for (int i = 0; i < layout.getChildCount(); i++) {
                        View view = layout.getChildAt(i);
                        Class c = view.getClass();
                        if (c == LinearLayout.class) {
                            for (int j = 0; j < ((LinearLayout) view).getChildCount(); j++){
                                View childView = ((LinearLayout) view).getChildAt(j);
                                Class childClass = childView.getClass();
                                if (childClass == EditText.class) {
                                    phoneNumber = ((EditText) childView).getText().toString();
                                    if (phoneNumber.length() > 0) {
                                        savedContact.addPhoneNumber(phoneNumber);
                                        aNumberHasBeenAdded = true;
                                    }
                                }
                            }
                        }
                    }

                    if (!aNumberHasBeenAdded) {
                        infoDialog.show();
                        return;
                    }
                    String googleAccount = ((EditText) findViewById(R.id.g_contact_account)).getText().toString();
                    String facebookAccount = ((EditText) findViewById(R.id.f_contact_account)).getText().toString();
                    String yahooAccount = ((EditText) findViewById(R.id.y_contact_account)).getText().toString();

                    if (googleAccount.length() > 0) {
                        savedContact.setGoogleAccount(googleAccount);
                    }
                    if (facebookAccount.length() > 0) {
                        savedContact.setFacebookAccount(facebookAccount);
                    }
                    if (yahooAccount.length() > 0) {
                        savedContact.setYahooAccount(yahooAccount);
                    }
                } else {
                    infoDialog.show();
                }
                Intent saveIntent = getIntent();
                saveIntent.putExtra("savedContact", savedContact);
                setResult(1,saveIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed (){
        Intent saveIntent = getIntent();
        saveIntent.putExtra("null", "null");
        setResult(2,saveIntent);
        finish();
    }
}
