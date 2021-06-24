package org.vgb.eaf;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.mail.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MessageProcessor {
    private static final Logger LOG = Logger.getLogger(MessageProcessor.class);

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm");
    /**
     *
     * @param message
     * @return true if message was processed - can be deleted from inbox
     */
    public boolean processMessage(Message message, EafConfiguration configuration) throws MessagingException {
        LOG.infov("process message: no {0} - {1} {2} - {3}",
                message.getMessageNumber(), message.getSentDate(),
                ((message.getFrom().length > 0) ? message.getFrom()[0] : "no-from"),
                message.getSubject()
        );
        String filenamePrefix = df.format(message.getSentDate());
        filenamePrefix += " - " + filenameEscape(message.getSubject());

        int processedAttachments = 0;
        boolean errornous = false;

        try {
            Object messageContent = message.getContent();
            if (!(messageContent instanceof Multipart)) {
                LOG.warn("no multipart: " + messageContent.getClass().getName());
                errornous = true;
            } else {
                Multipart multipart = (Multipart) messageContent;
                List<File> attachments = new ArrayList<File>();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);

                    if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) ||
                            StringUtils.isBlank(bodyPart.getFileName())) {
                        LOG.info("skip attachment " + bodyPart.getDisposition() + " - " + bodyPart.getFileName());
                        continue; // dealing with attachments only
                    }

                    InputStream is = bodyPart.getInputStream();
                    // don´t use filename - use prefix derived from subject and random UUID - but keep extension
                    String extension = getExtensionByStringHandling(bodyPart.getFileName()).orElseGet(() -> "");
                    // clean up extension for cases like
                    // =?iso-8859-1?Q?Rechnung_f=FCr_Herrn_Schmidt_31.05.2021_-_05.06.2021.pdf?=
                    if (extension.endsWith("?=")) {
                        LOG.info("eliminate ?=");
                        extension.replace("?=", "");
                    }
                    extension = filenameEscape(extension);

                    String fileName = filenamePrefix + " - " + UUID.randomUUID().toString() + "." + extension;
                    File f = new File(configuration.dirProcessedAttachments + "/" + fileName);
                    LOG.infov("store attachment {0} named {1} in {2}", i, bodyPart.getFileName(), f.getAbsolutePath());
                    FileOutputStream fos = new FileOutputStream(f);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    byte[] buf = new byte[800000];
                    int bytesRead;
                    while ((bytesRead = is.read(buf)) != -1) {
                        bos.write(buf, 0, bytesRead);
                    }
                    bos.close();
                    fos.close();

                    processedAttachments++;
                }
                if (processedAttachments == 0) {
                    LOG.warn("no attachments");
                    errornous = true;
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            errornous = true;
        }

        return ! errornous;
    }

    public Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    /**
     * creates a safe filename derived from subject
     * @param subject
     * @return
     */
    protected static String filenameEscape(String subject) {
        if (subject == null) {
            subject = "kein Betreff";
        }
        return subject.replaceAll("[^a-zA-Z0-9\\-]", "_");
    }
}
