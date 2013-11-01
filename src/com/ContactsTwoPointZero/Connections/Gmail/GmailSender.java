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
import java.util.Properties;

public class GmailSender extends javax.mail.Authenticator {
    private String user;
    private String password;
    private String attachmentFilePath, attachmentFileName;
    private Session session;

    public GmailSender(final String user, final String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "25");

        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("bot.smack21@gmail.com", "");
                    }
                });
    }

    public void addAttachement(String fileName, String filePath){
        attachmentFilePath = filePath;
        attachmentFileName = fileName;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized boolean sendMail(String subject, String body, String sender, String recipients) throws Exception {
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
            if (attachmentFilePath != null && attachmentFileName != null){
                messageBodyPart = new MimeBodyPart();
                DataSource source =
                        new FileDataSource(attachmentFilePath);
                messageBodyPart.setDataHandler(
                        new DataHandler(source));
                messageBodyPart.setFileName(attachmentFileName);
                multipart.addBodyPart(messageBodyPart);
            }

            // Put parts in message
            message.setContent(multipart);
            Log.i("GmailSender","Email SENDING to " + recipients + " from " + sender);
            Transport.send(message);
            Log.i("GmailSender","Email sent SUCCESSFULLY to " + recipients + " from " + sender);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}