package com.ContactsTwoPointZero.Connections.Facebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.example.ExpandableList.R;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;

/**
 * Created with IntelliJ IDEA.
 * User: Cataaa
 * Date: 26.10.2013
 * Time: 10:52
 * To change this template use File | Settings | File Templates.
 */
public class FacebookChatActivity extends Activity {
    private final String activityTag = "FacebookChatActivity";
    private TextView chatBody;
    private EditText chatInput;
    private Button sendButton;
    private FacebookChatActivity thisActivity;
    private XMPPConnection xmppConnection;
    private ChatManager chatManager;
    private String userAccount,userPassword;
    private String friendAccount;
    private Chat chat;
    private ScrollView chatScrollView;
    private String friendAccountID;
    private ProgressDialog loadingDialog;
    private String friendName;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        setContentView(R.layout.chat_layout);
        chatScrollView = (ScrollView) findViewById(R.id.chat_scrollView);
        chatBody = (TextView) findViewById(R.id.chat_body);
        chatInput = (EditText) findViewById(R.id.chat_input);
        sendButton = (Button) findViewById(R.id.send_chat_button);
        loadingDialog = new ProgressDialog(thisActivity);
        loadingDialog.setMessage("Connecting to Facebook Chat...");
        loadingDialog.setCancelable(false);
        Bundle extras = getIntent().getExtras();
        friendAccount = (String) extras.getSerializable("facebookAccount");
        friendName = (String) extras.getSerializable("contactName");
        userAccount = (String) extras.getSerializable("userAccount");
        userPassword = (String) extras.getSerializable("userPassword");
        ((ImageView) findViewById(R.id.chat_logo)).setImageResource(R.drawable.facebook_chat_icon);
        ((TextView) findViewById(R.id.recipient_name)).setText(friendName);
        new ConnectToFacebook().execute();
        addSendButtonListener();
    }

    private void sendMessage(String message) throws XMPPException {
        if (chat == null){
            chat = chatManager.createChat(friendAccountID + "@chat.facebook.com",new MessageListener() {
                @Override
                public void processMessage(Chat chat, final Message message) {
                }
            });
        }
        Log.i(activityTag, "Sending " + message);
        chat.sendMessage(message);
    }

    private void addSendButtonListener(){
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = chatInput.getText().toString();
                try {
                    sendMessage(message);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            chatInput.setText("");
                            chatBody.append("You: " + message + "\n\n");
                            chatScrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                } catch (XMPPException e) {
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                    builder.setMessage("Can not send message. Friend account is not valid. Please check his attached Facebook account")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    xmppConnection.disconnect();
                                    thisActivity.finish();
                                }
                            });
                    Dialog infoDialog = builder.create();
                    infoDialog.show();
                }
            }
        });
    }

    //Asynchronous class for connecting phase
    private class ConnectToFacebook extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            thisActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.show();
                }
            });
            if (tryConnection() && getIdByUsername()){
                thisActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                    }
                });
            }
            else{
                thisActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                        builder.setMessage("Can not connect to Facebook Chat Service. Please check your Internet Connection or your Facebook Account profile.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        xmppConnection.disconnect();
                                        thisActivity.finish();
                                    }
                                });
                        Dialog infoDialog = builder.create();
                        infoDialog.show();
                    }
                });
            }
            return null;
        }
    }


    private boolean tryConnection(){
        // Create a connection
        ConnectionConfiguration config = new ConnectionConfiguration("chat.facebook.com", 5222);
        config.setSASLAuthenticationEnabled(true);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        config.setRosterLoadedAtLogin(true);
        config.setTruststorePath("/system/etc/security/cacerts.bks");
        config.setTruststorePassword("changeit");
        config.setTruststoreType("bks");
        config.setSendPresence(false);
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, MemorizingTrustManager.getInstanceList(this), new java.security.SecureRandom());
            config.setCustomSSLContext(sc);
        } catch (GeneralSecurityException e) {
            Log.w(activityTag, "Unable to use MemorizingTrustManager", e);
            Log.e(activityTag,e.toString());
            return false;
        }
        xmppConnection = new XMPPConnection(config);
        Log.i("XMPPClient", "Connection Initialized");

        try {
            Log.i(activityTag, "Connecting..");
            xmppConnection.connect();
            Log.i(activityTag, "[SettingsDialog] Connected to " + xmppConnection.getHost());
        } catch (XMPPException ex) {
            Log.e(activityTag, "[SettingsDialog] Failed to connect to " + xmppConnection.getHost());
            Log.e(activityTag, ex.toString());
            return false;
        }

        try {
            Log.i(activityTag, "Logging in..");
            xmppConnection.login(userAccount, userPassword);
            Log.i(activityTag, "Logged in as " + xmppConnection.getUser());

            // Set the status to available
            Presence presence = new Presence(Presence.Type.available);
            xmppConnection.sendPacket(presence);

            // Get Chat manager and adding listener for incoming Chats
            chatManager = xmppConnection.getChatManager();

            xmppConnection.addPacketListener(new MessageParrot(), null);

        } catch (XMPPException ex) {
            Log.e(activityTag, "[SettingsDialog] Failed to log in as " + userAccount + " and " + userPassword);
            Log.e(activityTag, ex.toString());
            return false;
        }
        Log.i(activityTag, "Connected successfully.");
        return true;
    }


    //Listener for Incoming messages
    private class MessageParrot implements PacketListener {
        public void processPacket(Packet packet) {
            Message message = (Message) packet;
            if(message.getBody() != null && message.getFrom().contains(friendAccountID + "@chat.facebook.com")) {
                String fromName = StringUtils.parseBareAddress(message.getFrom());
                Log.i(activityTag, "Message from " + fromName + "\n" + message.getBody() + "\n");
                final String  messageBody = message.getBody();
                runOnUiThread(new Runnable() {
                    public void run() {
                        chatBody.append(friendAccount + ": " + messageBody + "\n\n");
                        chatScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }
    };

    //We need to get the Facebook ID of the username with who we are trying to chat.
    private boolean getIdByUsername(){
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            URL url = new URL("http://graph.facebook.com/" + friendAccount);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            Log.i(activityTag, "Error at getting ID for " + friendAccount);
            Log.e(activityTag, e.toString());
            return false;
        }
        Log.i(activityTag, "Get Method result: " + result + "\n");
        friendAccountID = result.split(",")[0].split(":")[1].replace("\"","");
        Log.i(activityTag, "The ID for " + friendAccount + " is " + friendAccountID + "\n");
        return true;
    }

    //Close Connection on Exit
    @Override
    public void onBackPressed (){
        xmppConnection.disconnect();
        finish();
    }
}
