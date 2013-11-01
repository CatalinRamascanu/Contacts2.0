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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        setContentView(R.layout.chat_layout);
        loadingDialog = new ProgressDialog(thisActivity);
        loadingDialog.setMessage("Connecting to Google Talk...");
        loadingDialog.setCancelable(false);

        chatScrollView = (ScrollView) findViewById(R.id.chat_scrollView);
        chatBody = (TextView) findViewById(R.id.chat_body);
        chatInput = (EditText) findViewById(R.id.chat_input);
        sendButton = (Button) findViewById(R.id.send_chat_button);
        Bundle extras = getIntent().getExtras();
        friendAccount = (String) extras.getSerializable("googleAccount");
//        loadingDialog.show();
        new ConnectToXmpp().execute();
        addSendButtonListener();

    }

    private void tryConnection(){

        String host = "talk.google.com";
        String port = "5222";
        String service = "gmail.com";
        String username = "catalin.rmc@gmail.com";
        String password = "fusroda2123";
        Log.i("XMPPClient", "Fields Initialized");

        // Create a connection
        ConnectionConfiguration connConfig =
                new ConnectionConfiguration(host, Integer.parseInt(port), service);
        xmppConnection = new XMPPConnection(connConfig);
        Log.i("XMPPClient", "Connection Initialized");
        try {
            Log.i("XMPPClient", "Connecting..");
            xmppConnection.connect();
            Log.i("XMPPClient", "[SettingsDialog] Connected to " + xmppConnection.getHost());
        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to connect to " + xmppConnection.getHost());
            Log.e("XMPPClient", ex.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                    builder.setMessage("Connection to GTalk Failed. Please check your Internet connection")
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
        try {
            Log.i("XMPPClient", "Logging in..");
            xmppConnection.login(username, password);
            Log.i("XMPPClient", "Logged in as " + xmppConnection.getUser());

            // Set the status to available
            Presence presence = new Presence(Presence.Type.available);
            xmppConnection.sendPacket(presence);

            // Get Chat manager and adding listener for incoming packets
            chatManager = xmppConnection.getChatManager();

            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            xmppConnection.addPacketListener(new MessageParrot(xmppConnection), filter);

            // Get connection Roster
            connectionRoster = xmppConnection.getRoster();

        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to log in as " + username);
            Log.e("XMPPClient", ex.toString());

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Connection to GTalk Failed. Please check your Social account setup.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            thisActivity.finish();
                        }
                    });
            Dialog infoDialog = builder.create();
            infoDialog.show();
        }

        Log.i("XMPPClient", "Connected successfully.");

    }

    private void sendMessage(String toFriend,String message) throws XMPPException {
        if (!connectionRoster.contains(friendAccount)){
            Log.i("XMPPClient", "Account " + friendAccount + " does not exist in roster. Sending invite;");
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
            tryConnection();
            loadingDialog.dismiss();
            return null;
        }
    }

    private class MessageParrot implements PacketListener {
        private XMPPConnection xmppConnection;

        public MessageParrot(XMPPConnection conn) {
            xmppConnection = conn;
        }

        public void processPacket(Packet packet) {
            Message message = (Message) packet;
            if(message.getBody() != null) {
                String fromName = StringUtils.parseBareAddress(message.getFrom());
                Log.i("XMPPClient", "Message from " + fromName + "\n" + message.getBody() + "\n");
                final String  messageBody = message.getBody();
                runOnUiThread(new Runnable() {
                    public void run() {
                        chatBody.append("Your Friend: " + messageBody + "\n\n");
                        chatScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }
    };
}
