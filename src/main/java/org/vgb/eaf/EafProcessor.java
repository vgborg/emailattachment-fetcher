package org.vgb.eaf;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EafProcessor {
    private static final Logger LOG = Logger.getLogger(EafProcessor.class);

    public void process(EafConfiguration configuration) {
        LOG.infov("connect to {0} as {1}", configuration.getImapTarget(), configuration.getImapUser());
    }
}
