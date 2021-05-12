package org.vgb.eaf;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;

public @FunctionalInterface interface MessageMover {
   public void moveMessage(Message msg, String messageFileTarget) throws MessagingException, IOException ;
}
