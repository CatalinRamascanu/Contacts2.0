package com.ContactsTwoPointZero.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.ContactsTwoPointZero.Connections.Gmail.GmailSender;
import com.ContactsTwoPointZero.TestPack.FileDialog;
import com.example.ExpandableList.R;
import org.jivesoftware.smack.SmackAndroid;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 10/29/13
 * Time: 7:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class EmailActivity extends Activity {
    private EmailActivity thisActivity;
    private Button sendButton;
    private EditText emailBody;
    private EditText emailSubject;
    private ImageButton attachFileButton;
    private TextView attachmentBox;
    private String userAccount;
    private String userPassword;
    private GmailSender gmailSender;
    private String messageSubject;
    private String messageBody;
    private String recipientAddress;
    private FileDialog fileDialog;
    private ProgressDialog loadingDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmackAndroid.init(getApplicationContext());
        thisActivity = this;
        loadingDialog = new ProgressDialog(thisActivity);
        loadingDialog.setMessage("Sending Email...");
        loadingDialog.setCancelable(false);
        Bundle extras = getIntent().getExtras();
        userAccount = (String) extras.getSerializable("userAccount");
        userPassword = (String) extras.getSerializable("userPassword");
        recipientAddress = (String) extras.getSerializable("recipientAddress");
        setContentView(R.layout.create_email_layout);
        emailBody = (EditText) findViewById(R.id.emailBody);
        emailSubject = (EditText) findViewById(R.id.emailSubject);
        sendButton = (Button) findViewById(R.id.sendEmailButton);
        attachFileButton = (ImageButton) findViewById(R.id.attachFileButton);
        attachmentBox = (TextView) findViewById(R.id.attachments_box);
        if (recipientAddress.contains("gmail")){
            ((ImageView) findViewById(R.id.email_logo)).setImageResource(R.drawable.google_mail_icon);
        }else{
            ((ImageView) findViewById(R.id.email_logo)).setImageResource(R.drawable.yahoo_mail_icon);

        }
        ((TextView) findViewById(R.id.recipient_name)).setText(recipientAddress);
        gmailSender = new GmailSender(userAccount,userPassword);
        initButtonListeners();

    }

    private void initButtonListeners(){
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageBody = emailBody.getText().toString();
                messageSubject = emailSubject.getText().toString();
                new SendEmail().execute();
            }
        });

        attachFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
                fileDialog = new FileDialog(thisActivity, mPath);
                fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                    public void fileSelected(File file) {
                        gmailSender.addAttachement(file.getName(),file.getPath());
                        attachmentBox.setText("");
                        attachmentBox.setText("Attached the following file : \n" + file.getName());
                    }
                });
                fileDialog.showDialog();
            }
        });
    }


    private class SendEmail extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.show();
                    }
                });
                if (gmailSender.sendMail(messageSubject,messageBody,userAccount, recipientAddress)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                            builder.setMessage("Email Sent Successfully!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            thisActivity.finish();
                                        }
                                    });
                            Dialog infoDialog = builder.create();
                            infoDialog.show();
                        }
                    });

                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                            builder.setMessage("Failed to Send Email.Please check your Internet connection")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            thisActivity.finish();
                                        }
                                    });
                            Dialog infoDialog = builder.create();
                            infoDialog.show();
                        }
                    });

                }
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }

    }

}
