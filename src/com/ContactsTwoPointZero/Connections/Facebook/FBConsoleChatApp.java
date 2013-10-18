package com.ContactsTwoPointZero.Connections.Facebook;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
 
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
 
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
 
public class FBConsoleChatApp {
 
   public static final String FB_XMPP_HOST = "chat.facebook.com";
   public static final int FB_XMPP_PORT = 5222;
 
   private ConnectionConfiguration config;
   private XMPPConnection connection;
   private BidiMap friends = new DualHashBidiMap();
   private FBMessageListener fbml;
 
   public String connect() throws XMPPException {
      config = new ConnectionConfiguration(FB_XMPP_HOST, FB_XMPP_PORT);
      SASLAuthentication.registerSASLMechanism("DIGEST-MD5"
        , CustomSASLDigestMD5Mechanism.class);
      config.setSASLAuthenticationEnabled(true);
      config.setDebuggerEnabled(true);
      connection = new XMPPConnection(config);
      connection.connect();
      fbml = new FBMessageListener(connection);
      return connection.getConnectionID();
   }
 
   public void disconnect() {
      if ((connection != null) && (connection.isConnected())) {
         Presence presence = new Presence(Presence.Type.unavailable);
         presence.setStatus("offline");
         connection.disconnect(presence);
      }
   }
 
   public boolean login(String userName, String password) 
     throws XMPPException {
      if ((connection != null) && (connection.isConnected())) {
    	 System.out.println(userName + " " + password);
         connection.login(userName, password);
         return true;
      }
      return false;
   }
 
   public String readInput() throws IOException {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      return br.readLine();
   }
 
   public void showMenu() {
      System.out.println("Please select one of the following menu.");
      System.out.println("1. List of Friends online");
      System.out.println("2. Send Message");
      System.out.println("3. EXIT");
      System.out.print("Your choice [1-3]: ");
   }
 
   public void getFriends() {
      if ((connection != null) && (connection.isConnected())) {
         Roster roster = connection.getRoster();
         int i = 1;
         for (RosterEntry entry : roster.getEntries()) {
            Presence presence = roster.getPresence(entry.getUser());
            if ((presence != null) 
               && (presence.getType() != Presence.Type.unavailable)) {
               friends.put("#" + i, entry);
               System.out.println(entry.getName() + "(#" + i + ")");
               i++;
            }
         }
         fbml.setFriends(friends);
      }
   }
 
   public void sendMessage() throws XMPPException
     , IOException {
      System.out.println("Type the key number of your friend (e.g. #1) and the text that you wish to send !");
      String friendKey = null;
      String text = null;
      System.out.print("Your friend's Key Number: ");
      friendKey = readInput();
      System.out.print("Your Text message: ");
      text = readInput();
      sendMessage((RosterEntry) friends.get(friendKey), text);
   }
 
   public void sendMessage(final RosterEntry friend, String text) 
     throws XMPPException {
      if ((connection != null) && (connection.isConnected())) {
         ChatManager chatManager = connection.getChatManager();
         Chat chat = chatManager.createChat(friend.getUser(), fbml);
         chat.sendMessage(text);
         System.out.println("Your message has been sent to "
            + friend.getName());
      }
   }
   
   public static void main(String[] args) {
      if (args.length == 0) {
        System.err.println("Usage: java FBConsoleChatApp [username_facebook] [password]");
        System.exit(-1);
      }
 
      String username = "catalin.ramascanu";
      String password = "caine ud";
      FBConsoleChatApp app = new FBConsoleChatApp();
 
      try {
    	 System.out.println("Connecting...");
         System.out.println(app.connect());
         System.out.println("Connected");
         if (!app.login(username, password)) {
        	 System.out.println("Showing Menu.");
            System.out.println("Access Denied...");
//            System.exit(-2);
         }
         System.out.println("Showing Menu.");
         
         app.showMenu();
         String data = null;
         menu:
         while((data = app.readInput().trim()) != null) {
            if (!Character.isDigit(data.charAt(0))) {
               System.out.println("Invalid input.Only 1-3 is allowed !");
               app.showMenu();
               continue;
            }
            int choice = Integer.parseInt(data);
            if ((choice != 1) && (choice != 2) && (choice != 3)) {
               System.out.println("Invalid input.Only 1-3 is allowed !");
               app.showMenu();
               continue;
            }
            switch (choice) {
               case 1: app.getFriends();
                       app.showMenu();
                       continue menu;
               case 2: app.sendMessage();
                       app.showMenu();
                       continue menu;
               default: break menu;
            }
         }
         app.disconnect();
      } catch (XMPPException e) {
        if (e.getXMPPError() != null) {
           System.err.println("ERROR-CODE : " + e.getXMPPError().getCode());
           System.err.println("ERROR-CONDITION : " + e.getXMPPError().getCondition());
           System.err.println("ERROR-MESSAGE : " + e.getXMPPError().getMessage());
           System.err.println("ERROR-TYPE : " + e.getXMPPError().getType());
        }
        app.disconnect();
      } catch (IOException e) {
        System.err.println(e.getMessage());
        app.disconnect();
      }
  }
}