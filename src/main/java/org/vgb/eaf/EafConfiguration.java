package org.vgb.eaf;

import org.eclipse.microprofile.config.inject.ConfigProperty;

public class EafConfiguration {
    String dirProcessedAttachments;

    String dirProcessedErrors;

    String dirProcessedOut;


    String imapTarget;

    String imapUser;

    String imapPassword;

    boolean imapDebug;

    public EafConfiguration(String dirProcessedAttachments, String dirProcessedErrors, String dirProcessedOut,
                            String imapTarget, String imapUser, String imapPassword, boolean imapDebug
    ) {
        this.dirProcessedAttachments = dirProcessedAttachments;
        this.dirProcessedErrors = dirProcessedErrors;
        this.dirProcessedOut = dirProcessedOut;
        this.imapTarget = imapTarget;
        this.imapUser = imapUser;
        this.imapPassword = imapPassword;
        this.imapDebug = imapDebug;
    }

    public boolean isImapDebug() {
        return imapDebug;
    }

    public void setImapDebug(boolean imapDebug) {
        this.imapDebug = imapDebug;
    }

    public String getDirProcessedAttachments() {
        return dirProcessedAttachments;
    }

    public void setDirProcessedAttachments(String dirProcessedAttachments) {
        this.dirProcessedAttachments = dirProcessedAttachments;
    }

    public String getDirProcessedErrors() {
        return dirProcessedErrors;
    }

    public void setDirProcessedErrors(String dirProcessedErrors) {
        this.dirProcessedErrors = dirProcessedErrors;
    }

    public String getDirProcessedOut() {
        return dirProcessedOut;
    }

    public void setDirProcessedOut(String dirProcessedOut) {
        this.dirProcessedOut = dirProcessedOut;
    }

    public String getImapTarget() {
        return imapTarget;
    }

    public void setImapTarget(String imapTarget) {
        this.imapTarget = imapTarget;
    }

    public String getImapUser() {
        return imapUser;
    }

    public void setImapUser(String imapUser) {
        this.imapUser = imapUser;
    }

    public String getImapPassword() {
        return imapPassword;
    }

    public void setImapPassword(String imapPassword) {
        this.imapPassword = imapPassword;
    }
}
