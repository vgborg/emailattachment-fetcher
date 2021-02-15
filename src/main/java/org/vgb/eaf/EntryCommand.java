package org.vgb.eaf;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import javax.inject.Inject;


@QuarkusMain
@CommandLine.Command(mixinStandardHelpOptions = true)
public class EntryCommand implements Runnable, QuarkusApplication {

    @ConfigProperty(name = "dir.processed.attachments")
    String config_dirProcessedAttachments;

    @ConfigProperty(name = "dir.processed.errors")
    String config_dirProcessedErrors;

    @ConfigProperty(name = "dir.processed.out")
    String config_dirProcessedOut;



    @ConfigProperty(name = "imap.target")
    String config_imapTarget;

    @ConfigProperty(name = "imap.user")
    String config_imapUser;

    @ConfigProperty(name = "imap.password")
    String config_imapPassword;



    @CommandLine.Option(names = {"-a", "--attachments"}, description = "Where to store attachments")
    String paramenter_dirProcessedAttachments;

    @CommandLine.Option(names = {"-e", "--errors"}, description = "Where to store errornous mails")
    String paramenter_dirProcessedErrors;

    @CommandLine.Option(names = {"-o", "--out"}, description = "Where to store processed emails")
    String paramenter_dirProcessedOut;


    private static final Logger LOG = Logger.getLogger(EntryCommand.class);


    @Inject
    CommandLine.IFactory factory;

    @Inject
    EafProcessor eafProcessor;

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory).execute(args);
    }

    @Override
    public void run() {
        String dirProcessedAttachments =
                (paramenter_dirProcessedAttachments != null) ? paramenter_dirProcessedAttachments : config_dirProcessedAttachments;

        String dirProcessedErrors =
                (paramenter_dirProcessedErrors != null) ? paramenter_dirProcessedErrors : config_dirProcessedErrors;

        String dirProcessedOut =
                (paramenter_dirProcessedOut != null) ? paramenter_dirProcessedOut : config_dirProcessedOut;


        EafConfiguration configuration = new EafConfiguration(
                dirProcessedAttachments, dirProcessedErrors, dirProcessedOut,
                config_imapTarget, config_imapUser, config_imapPassword
        );

        eafProcessor.process(configuration);
    }
}
