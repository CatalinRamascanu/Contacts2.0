package com.ContactsTwoPointZero.Connections.Yahoo;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.ContactsTwoPointZero.Activities.YahooChatActivity;
import org.jivesoftware.smack.Roster;
import org.openymsg.network.*;
import org.openymsg.network.event.SessionEvent;
import org.openymsg.network.event.SessionListener;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: Cataaa
 * Date: 30.10.2013
 * Time: 19:06
 * To change this template use File | Settings | File Templates.
 */
public class YahooMessengerConnection implements SessionListener {

    private Session session = new Session();
    private String username;
    private String password;
    private String recipient;
    private TextView chatBody;
    private YahooChatActivity activity;
    private org.openymsg.roster.Roster roster;

    public YahooMessengerConnection(String username, String password, String recipient) {
        this.username = username.split("@yahoo")[0];
        this.password = password;
        this.recipient = recipient.split("@yahoo")[0];
    }

    public void setChatBody(TextView chatBody, YahooChatActivity activity){
        this.chatBody = chatBody;
        this.activity = activity;
    }


    public boolean loginToYahoo() {
        try {
            Log.i("YMessConn","Connecting with username: " + username + " and password: " + password);
            session.addSessionListener(this);
            session.login(username, password);
            roster = session.getRoster();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void logout(){
        try {
            session.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMessage(String message){
        if (!roster.contains(new YahooUser(recipient))){
            try {
                Log.i("YMessConn",recipient + "is not on my friend list.");
                session.sendNewFriendRequest(recipient+"@yahoo.com","Contacts2.0", YahooProtocol.YAHOO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Log.i("YMessConn","Sending " + message + " to: " + recipient);
            session.sendMessage(recipient, message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendBuzz(){
        try {
            session.sendBuzz(recipient);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void dispatch(FireEvent fireEvent) {
        ServiceType type = fireEvent.getType();
        final SessionEvent sessionEvent = fireEvent.getEvent();
        if (type == ServiceType.MESSAGE) {
            try {
                if (sessionEvent.getFrom().equals(recipient)){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatBody.append(activity.getFriendName() + ": " + sessionEvent.getMessage() + "\n\n");
                            activity.getChatScrollView().fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            } catch (Exception e) {
            }
        }
    }
}
