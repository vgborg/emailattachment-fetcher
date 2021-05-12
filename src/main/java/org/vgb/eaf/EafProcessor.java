package org.vgb.eaf;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.*;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.UUID;

/**
 * connect to IMAP server and check for mails.
 * Mails are processed in `MessageProcessor`
 */
@ApplicationScoped
public class EafProcessor {
    private static final Logger LOG = Logger.getLogger(EafProcessor.class);
    public static final String INBOX = "INBOX";

    @ConfigProperty(name = "imap.purge")
    boolean imapPurge = false;

    @ConfigProperty(name = "processed.target")
    String processedTarget;

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm");

    @Inject
    MessageProcessor messageProcessor;

    public void process(EafConfiguration configuration) throws MessagingException, URISyntaxException {
        URI u = new URI(configuration.getImapTarget());
        String protocol = u.getScheme();
        Properties properties = getServerProperties(protocol, u.getHost(), u.getPort());
        Session session = Session.getInstance(properties);
        session.setDebug(configuration.isImapDebug());
        IMAPStore store = (IMAPStore) session.getStore(protocol);
        LOG.infov("authenticate as {0}", configuration.getImapUser());
        store.connect(configuration.getImapUser(), configuration.imapPassword);


        LOG.debug("connected");




        IMAPFolder inboxFolder = (IMAPFolder) store.getFolder(INBOX);
        inboxFolder.open(Folder.READ_WRITE);


        MessageMover messageMover = null;
        if ("dir".equals(processedTarget)) {
            messageMover = this::messageMoverSaveInDir;
        } else if ("imap".equals(processedTarget)) {
            messageMover = (msg, target)  -> messageMoverImap(msg, target, store);
        } else {
            throw new RuntimeException("messageMover \"" + processedTarget + "\" (processed.target) not defined");
        }

        Message[] messages = inboxFolder.getMessages();
        LOG.infov("folder {0} contains {1} messages", inboxFolder.getName(), messages.length);

        for (Message msg : messages) {
            boolean processed = false;
            try {
                processed = messageProcessor.processMessage(msg, configuration);
            } catch (Exception e) {
                LOG.fatal(e.getMessage(), e);
            }

            msg.setFlag(Flags.Flag.SEEN, true);

            try {

                // store EML as processed
                String messageFileTarget = processed ?
                        configuration.getDirProcessedOut() :
                        configuration.getDirProcessedErrors();


                messageMover.moveMessage(msg, messageFileTarget);

            } catch (Exception e) {
                LOG.error("error postprocessing - " + e.getMessage(), e);
            }
        }

        // close folder and delete messages marked for deletion
        inboxFolder.close(imapPurge);
    }

    private void messageMoverImap(Message message, String messageFileTarget, IMAPStore store) throws MessagingException {
        LOG.infov("store message in imap folder {0}", messageFileTarget);


        IMAPFolder targetFolder = (IMAPFolder) store.getFolder(messageFileTarget);
        if (! targetFolder.exists()) {
            LOG.warn("create folder: " + messageFileTarget);
            targetFolder.create(IMAPFolder.HOLDS_MESSAGES);
        }

        IMAPFolder sourceFolder = ((IMAPFolder)message.getFolder());
        sourceFolder.moveMessages(new Message[]{message}, targetFolder);
    }

    private void messageMoverSaveInDir(Message msg, String messageFileTarget) throws MessagingException, IOException {
        messageFileTarget += "/" + df.format(msg.getSentDate()) + " " + UUID.randomUUID().toString() + ".eml";
        LOG.infov("store message in filesystem {0}", messageFileTarget);
        FileOutputStream fos = new FileOutputStream(messageFileTarget);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        msg.writeTo(bos);
        bos.close();
        fos.close();

        // set deleted - because saved in filesystem
        msg.setFlag(Flags.Flag.DELETED, true);
    }

    private Properties getServerProperties(String protocol, String host,
                                           Integer port) {
        Properties properties = new Properties();

        if (port == null) {
            if ("imaps".equals(protocol)) {
                port = 993;
            } else {
                port = 143;
            }
        }
        LOG.infov("connect via {0} to {1} on port {2}", protocol, host, port);

        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);

        // SSL setting
        properties.setProperty(
                String.format("mail.%s.auth.plain.disable", protocol),
                "true");
        properties.setProperty(
                String.format("mail.%s.ssl.enable", protocol),
                "true");
        properties.setProperty(
                String.format("mail.%s.starttls.enable", protocol),
                "true");
        properties.setProperty(
                String.format("mail.%s.socketFactory.class", protocol),
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(
                String.format("mail.%s.socketFactory.fallback", protocol),
                "false");
        properties.setProperty(
                String.format("mail.%s.socketFactory.port", protocol),
                String.valueOf(port));

        return properties;
    }
}
