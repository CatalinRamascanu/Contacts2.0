package com.ContactsTwoPointZero.Activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.ContactsTwoPointZero.Connections.Gmail.GmailSender;
import com.example.ExpandableList.R;
import org.jivesoftware.smack.SmackAndroid;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 10/29/13
 * Time: 7:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class EmailActivity extends Activity {
    private EmailActivity thisActivity;
    private String mailhost = "smtp.gmail.com";
    private Button sendButton;
    private EditText emailBody;
    private EditText emailSubject;
    private ImageButton attachFileButton;
    private String userAccount;
    private String userPassword;
    private GmailSender gmailSender;
    private String messageSubject;
    private String messageBody;
    private String recipientAddress;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmackAndroid.init(getApplicationContext());
        thisActivity = this;
        setContentView(R.layout.create_email_layout);
        emailBody = (EditText) findViewById(R.id.emailBody);
        emailSubject = (EditText) findViewById(R.id.emailSubject);
        sendButton = (Button) findViewById(R.id.sendEmailButton);
        attachFileButton = (ImageButton) findViewById(R.id.attachFileButton);
        Bundle extras = getIntent().getExtras();
        userAccount = (String) extras.getSerializable("userAccount");
        userPassword = (String) extras.getSerializable("userPassword");
        recipientAddress = (String) extras.getSerializable("recipientAddress");
        gmailSender = new GmailSender(userAccount,userPassword);
        addSendButtonListener();

    }

    private void addSendButtonListener(){
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageBody = emailBody.getText().toString();
                messageSubject = emailSubject.getText().toString();
                new SendEmail().execute();
            }
        });
    }

    private class SendEmail extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.i("EmailActivity","SENDING! " + userAccount + " " + recipientAddress);
                gmailSender.sendMail(messageSubject,messageBody,userAccount, recipientAddress);
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }

    }
}
