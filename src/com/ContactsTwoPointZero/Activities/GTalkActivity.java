package com.ContactsTwoPointZero.Activities;

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
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Cataaa
 * Date: 26.10.2013
 * Time: 10:52
 * To change this template use File | Settings | File Templates.
 */
public class GTalkActivity extends Activity {
    private final String activityTag = "GoogleTalkActivity";
    private TextView chatBody;
    private EditText chatInput;
    private Button sendButton;
    private GTalkActivity thisActivity;
    private XMPPConnection xmppConnection;
    private ChatManager chatManager;
    private Chat chat;
    private String friendAccount;
    private Roster connectionRoster;
    private ScrollView chatScrollView;
    private ProgressDialog loadingDialog;
    private String userAccount;
    private String userPassword;
    private String friendName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        loadingDialog = new ProgressDialog(thisActivity);
        loadingDialog.setMessage("Connecting to Google Talk...");
        loadingDialog.setCancelable(false);
        setContentView(R.layout.chat_layout);
        chatScrollView = (ScrollView) findViewById(R.id.chat_scrollView);
        chatBody = (TextView) findViewById(R.id.chat_body);
        chatInput = (EditText) findViewById(R.id.chat_input);
        sendButton = (Button) findViewById(R.id.send_chat_button);
        Bundle extras = getIntent().getExtras();
        friendAccount = (String) extras.getSerializable("googleAccount");
        userAccount = (String) extras.getSerializable("userAccount");
        userPassword = (String) extras.getSerializable("userPassword");
        friendName = (String) extras.getSerializable("contactName");
        ((ImageView) findViewById(R.id.chat_logo)).setImageResource(R.drawable.google_chat_icon);
        ((TextView) findViewById(R.id.recipient_name)).setText(friendName);
        new ConnectToXmpp().execute();
        addSendButtonListener();
    }

    private boolean tryConnection(){

        String host = "talk.google.com";
        String port = "5222";
        String service = "gmail.com";
        Log.i(activityTag, "Fields Initialized");

        // Create a connection
        ConnectionConfiguration connConfig =
                new ConnectionConfiguration(host, Integer.parseInt(port), service);
        xmppConnection = new XMPPConnection(connConfig);
        Log.i(activityTag, "Connection Initialized");
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
            Log.i(activityTag, "Logging in.. as" + userAccount + " " + userPassword);
            xmppConnection.login(userAccount, userPassword);
            Log.i(activityTag, "Logged in as " + xmppConnection.getUser());

            // Set the status to available
            Presence presence = new Presence(Presence.Type.available);

            // Set the status message
            presence.setStatus("Online with Contacts 2.0!");

            // Set the highest priority
            presence.setPriority(24);

            // Set available presence mode
            presence.setMode(Presence.Mode.available);

            xmppConnection.sendPacket(presence);

            // Get Chat manager and adding listener for incoming packets
            chatManager = xmppConnection.getChatManager();

            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            xmppConnection.addPacketListener(new MessageParrot(), filter);

            // Get connection Roster
            connectionRoster = xmppConnection.getRoster();

        } catch (XMPPException ex) {
            Log.e(activityTag, "[SettingsDialog] Failed to log in as " + userAccount);
            Log.e(activityTag, ex.toString());
            return false;
        }

        Log.i(activityTag, "Connected successfully.");
        return true;
    }

    private void sendMessage(String toFriend,String message) throws XMPPException {
        if (!connectionRoster.contains(friendAccount)){
            Log.i(activityTag, "Account " + friendAccount + " does not exist in roster. Sending invite;");
            connectionRoster.createEntry(friendAccount, null, null);
        }
        Message msg = new Message(friendAccount, Message.Type.chat);
        msg.setBody(message);
        xmppConnection.sendPacket(msg);
    }

    private void addSendButtonListener(){
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = chatInput.getText().toString();
                try {
                    sendMessage(friendAccount, message);
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
                    builder.setMessage("Can not send message. Friend account is not valid. Please check his attached Google account")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    thisActivity.finish();
                                }
                            });
                    Dialog infoDialog = builder.create();
                    infoDialog.show();
                }
            }
        });
    }

    private class ConnectToXmpp extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            thisActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.show();
                }
            });
            if (tryConnection()){
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
                        builder.setMessage("Can not connect to Google Talk Service. Please check your Internet Connection or your Google Account profile.")
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
            tryConnection();
            loadingDialog.dismiss();
            return null;
        }
    }

    private class MessageParrot implements PacketListener {
        public void processPacket(Packet packet) {
            Message message = (Message) packet;
            if(message.getBody() != null && message.getFrom().contains(friendAccount)) {
                String fromName = StringUtils.parseBareAddress(message.getFrom());
                Log.i(activityTag, "Message from " + fromName + "\n" + message.getBody() + "\n");
                final String  messageBody = message.getBody();
                runOnUiThread(new Runnable() {
                    public void run() {
                        chatBody.append(friendAccount +": " + messageBody + "\n\n");
                        chatScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }
    };

    @Override
    public void onBackPressed (){
        xmppConnection.disconnect();
        finish();
    }
}
