package com.ContactsTwoPointZero.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import com.ContactsTwoPointZero.Connections.Yahoo.YahooMessengerConnection;
import com.example.ExpandableList.R;
import org.jivesoftware.smack.*;
import org.openymsg.network.Session;

/**
 * Created with IntelliJ IDEA.
 * User: Cataaa
 * Date: 30.10.2013
 * Time: 19:00
 * To change this template use File | Settings | File Templates.
 */
public class YahooChatActivity extends Activity {
    private TextView chatBody;
    private EditText chatInput;
    private Button sendButton;
    private YahooChatActivity thisActivity;
    private Chat chat;
    private String friendAccount;
    private ProgressDialog loadingDialog;
    private ScrollView chatScrollView;
    private Session yahooSession;
    private String userAccount;
    private String userPassword;
    private YahooMessengerConnection yahooConnection;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        setContentView(R.layout.chat_layout);
        chatScrollView = (ScrollView) findViewById(R.id.chat_scrollView);
        chatBody = (TextView) findViewById(R.id.chat_body);
        chatInput = (EditText) findViewById(R.id.chat_input);
        sendButton = (Button) findViewById(R.id.send_chat_button);
        Bundle extras = getIntent().getExtras();
        friendAccount = (String) extras.getSerializable("yahooAccount");
        userAccount = (String) extras.getSerializable("userAccount");
        userPassword = (String) extras.getSerializable("userPassword");
        loadingDialog = new ProgressDialog(thisActivity);
        loadingDialog.setMessage("Connecting to Yahoo Messenger...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        new ConnectToYahoo().execute();
        initSendButtonListener();
    }

    private void initYahooConnection(){
        yahooConnection = new YahooMessengerConnection(userAccount,userPassword,friendAccount);
        yahooConnection.setChatBody(chatBody,this);
        if (yahooConnection.loginToYahoo()){
            loadingDialog.dismiss();
        }else{
            loadingDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
            builder.setMessage("Failed to connect to Yahoo Messenger.Please check your Internet connection")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            thisActivity.finish();
                        }
                    });
            Dialog infoDialog = builder.create();
            infoDialog.show();
        }
    }

    private void initSendButtonListener(){
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = chatInput.getText().toString();
                if (yahooConnection.sendMessage(message)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatInput.setText("");
                            chatBody.append("You: " + message + "\n\n");
                        }
                    });
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                    builder.setMessage("Failed to send message.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    Dialog infoDialog = builder.create();
                    infoDialog.show();
                }


            }
        });
    }

    private class ConnectToYahoo extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            initYahooConnection();
            return null;
        }
    }
}
