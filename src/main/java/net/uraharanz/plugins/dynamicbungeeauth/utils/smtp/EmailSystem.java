package net.uraharanz.plugins.dynamicbungeeauth.utils.smtp;

import java.io.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uraharanz.plugins.dynamicbungeeauth.main;
import org.apache.commons.io.IOUtils;

public class EmailSystem {
    private final main plugin;
    private final String host;
    private final String port;
    private final String user;
    private final String pass;
    private final String from;
    private final String file;
    private final String subj;

    public EmailSystem(main main2) {
        this.plugin = main2;
        this.host = main2.getConfigLoader().getStringCFG("SMTP.host");
        this.port = main2.getConfigLoader().getStringCFG("SMTP.port");
        this.user = main2.getConfigLoader().getStringCFG("SMTP.user");
        this.pass = main2.getConfigLoader().getStringCFG("SMTP.pass");
        this.from = main2.getConfigLoader().getStringCFG("SMTP.from");
        this.file = main2.getConfigLoader().getStringCFG("SMTP.file");
        this.subj = main2.getConfigLoader().getStringCFG("SMTP.subj");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendEmailMessage(ProxiedPlayer proxiedPlayer, String string) {
        try {
            Properties properties = System.getProperties();
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.port", this.port);
            properties.put("mail.smtp.auth", true);
            properties.put("mail.smtp.starttls.enabled", true);
            properties.put("mail.smtp.starttls.required", true);
            properties.put("mail.smtp.socketFactory.port", "465");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.auth", true);
            properties.put("mail.smtp.port", "465");
            Session session = Session.getDefaultInstance(properties, new Authenticator(){

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EmailSystem.this.user, EmailSystem.this.pass);
                }
            });
            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.setFrom(new InternetAddress(this.from, this.subj));
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(string));
            mimeMessage.setSubject(this.subj);

            StringWriter stringWriter = new StringWriter();
            File file = new File(this.plugin.getDataFolder() + "/" + this.file);
            try {
                IOUtils.copy((InputStream)new FileInputStream(file), (Writer)stringWriter);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
            String string2 = stringWriter.toString().replace("%^SECRET-KEY^%", proxiedPlayer.getUniqueId().toString()).replace("%^player^%", proxiedPlayer.getName()).replace("%^playerUUID^%", proxiedPlayer.getUniqueId().toString());
            mimeMessage.setContent(string2.replace("%^SECRET-KEY^%", proxiedPlayer.getUniqueId().toString()).replace("%^player^%", proxiedPlayer.getName()).replace("%^playerUUID^%", proxiedPlayer.getUniqueId().toString()), "text/html");
            try {
                stringWriter.close();
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
            try (Transport transport = session.getTransport()){
                transport.connect(this.host, Integer.parseInt(this.port), this.user, this.pass);
                transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            }
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
