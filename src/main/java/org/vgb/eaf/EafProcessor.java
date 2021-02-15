package org.vgb.eaf;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

@ApplicationScoped
public class EafProcessor {
    private static final Logger LOG = Logger.getLogger(EafProcessor.class);
    public static final String INBOX = "INBOX";

    @Inject
    MessageProcessor messageProcessor;

    public void process(EafConfiguration configuration) throws MessagingException, URISyntaxException {
        URI u = new URI(configuration.getImapTarget());
        String protocol = u.getScheme();
        Properties properties = getServerProperties(protocol, u.getHost(), u.getPort());
        Session session = Session.getInstance(properties);
        session.setDebug(configuration.isImapDebug());
        Store store = session.getStore(protocol);
        LOG.infov("authenticate as {0}", configuration.getImapUser());
        store.connect(configuration.getImapUser(), configuration.imapPassword);


        LOG.info("connected");


        Folder inboxFolder = store.getFolder(INBOX);
        inboxFolder.open(Folder.READ_WRITE);
        Message[] messages = inboxFolder.getMessages();
        LOG.infov("folder {0} contains {1} messages", inboxFolder.getName(), messages.length);

        for (Message msg : messages) {
            try {
                boolean processed = messageProcessor.processMessage(msg, configuration);
                if (processed) {
                    msg.setFlag(Flags.Flag.DELETED, true);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }

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
