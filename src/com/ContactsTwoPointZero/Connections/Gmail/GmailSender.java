package com.ContactsTwoPointZero.Connections.Gmail;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 10/25/13
 * Time: 4:54 PM
 * To change this template use File | Settings | File Templates.
 */
import android.util.Log;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

public class GmailSender extends javax.mail.Authenticator {
    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;

//    static {
//        Security.addProvider(new JSSEProvider());
//    }

    public GmailSender(final String user, final String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("catalin.rmc@gmail.com", "ageofmight1992");
                    }
                });
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients) throws Exception {
        try{
            // Define message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            message.setSubject(subject);

            // create the message part
            MimeBodyPart messageBodyPart =
                    new MimeBodyPart();

            //fill message
            messageBodyPart.setText(body);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            DataSource source =
                    new FileDataSource("/storage/sdcard0/Download/tumblr_mftomy8ARK1qbmkkbo1_500.png");
            messageBodyPart.setDataHandler(
                    new DataHandler(source));
            messageBodyPart.setFileName("Kimi!");
            multipart.addBodyPart(messageBodyPart);

            // Put parts in message
            message.setContent(multipart);
            Log.i("EmailActivity","Email SENDING to " + recipients + " from " + sender);
            Transport.send(message);
            Log.i("EmailActivity","Email sent to " + recipients + " from " + sender);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("NOT !!!! SEEEEENDING!!!!!!!");
            }
    }
}