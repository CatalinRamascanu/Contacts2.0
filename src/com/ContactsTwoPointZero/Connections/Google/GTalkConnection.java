package com.ContactsTwoPointZero.Connections.Google;

import android.util.Log;
import android.util.SparseArray;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Cataaa
 * Date: 15.10.2013
 * Time: 21:20
 * To change this template use File | Settings | File Templates.
 */
public class GTalkConnection {
    private String googleAccount;
    private String googlePassword;
    private XMPPConnection xmppConnection;
    private ChatManager chatManager;
    private Chat chat;
    private AccountManager accountManager;
    private SparseArray<String> friendsList;
    public GTalkConnection(String googleAccount, String googlePassword) {
        this.googleAccount = googleAccount;
        this.googlePassword = googlePassword;
    }

    public void tryConnection(){
        String host = "talk.google.com";
        String port = "5222";
        String service = "gmail.com";
        String username = "catalin.rmc@gmail.com";
        String password = "ageofmight1992";
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
        }
        try {
            Log.i("XMPPClient", "Logging in..");
            xmppConnection.login(username, password);
            Log.i("XMPPClient", "Logged in as " + xmppConnection.getUser());

            // Set the status to available
            Presence presence = new Presence(Presence.Type.available);
            xmppConnection.sendPacket(presence);

            // Get Chat manager and Account Manager
            chatManager = xmppConnection.getChatManager();
            accountManager = xmppConnection.getAccountManager();

        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to log in as " + username);
            Log.e("XMPPClient", ex.toString());
        }

        Log.i("XMPPClient", "Connected!!!!!");
    }

    public SparseArray<String> getFriendsList(){
        if (friendsList == null){
            SparseArray<String> friendsList = new SparseArray<String>();
            Roster roster = xmppConnection.getRoster();
            Collection<RosterEntry> entries = roster.getEntries();
            int nrFriends = 0;
            for (RosterEntry entry : entries) {
                friendsList.append(nrFriends++,entry.toString());
            }
        }
        return friendsList;
    }

    public void sendMessage(String toFriend,String msg) throws XMPPException {
        if (chat == null){
            chat = chatManager.createChat(toFriend,new MessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {

                }
            });
        }
        chat.sendMessage(new Message(msg));
    }

    public void closeChat(){
        chat = null;
    }
}
