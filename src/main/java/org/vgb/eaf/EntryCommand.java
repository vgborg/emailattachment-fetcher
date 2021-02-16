package org.vgb.eaf;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import javax.inject.Inject;

/**
 * Entry point for command line.
 * Just accept parameters, setup `EafConfiguration` object and
 * start `EafProcessor` (in loop if enabled).
 */
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

    @ConfigProperty(name ="imap.debug")
    boolean imapDebug;


    @CommandLine.Option(names = {"-a", "--attachments"}, description = "Where to store attachments", paramLabel = "<dir>")
    String paramenter_dirProcessedAttachments;

    @CommandLine.Option(names = {"-e", "--errors"}, description = "Where to store errornous mails", paramLabel = "<dir>")
    String paramenter_dirProcessedErrors;

    @CommandLine.Option(names = {"-o", "--out"}, description = "Where to store processed emails", paramLabel = "<dir>")
    String paramenter_dirProcessedOut;


    @CommandLine.Option(names = {"-s", "--seconds"}, description = "Repeat every x seconds", paramLabel = "<seconds>")
    Integer paramenter_seconds;


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
        System.out.println("== Email attachment fetcher ===");

        String dirProcessedAttachments =
                (paramenter_dirProcessedAttachments != null) ? paramenter_dirProcessedAttachments : config_dirProcessedAttachments;

        String dirProcessedErrors =
                (paramenter_dirProcessedErrors != null) ? paramenter_dirProcessedErrors : config_dirProcessedErrors;

        String dirProcessedOut =
                (paramenter_dirProcessedOut != null) ? paramenter_dirProcessedOut : config_dirProcessedOut;

        EafConfiguration configuration = new EafConfiguration(
                dirProcessedAttachments, dirProcessedErrors, dirProcessedOut,
                config_imapTarget, config_imapUser, config_imapPassword, imapDebug
        );

        boolean repeat = false;
        do {
            repeat = (paramenter_seconds != null) && paramenter_seconds > 0;

            try {
                eafProcessor.process(configuration);
                if (repeat) {
                    LOG.infov("sleep {0} seconds to run again", paramenter_seconds);
                    Thread.sleep(paramenter_seconds * 1000);
                } else {
                    LOG.infov("no repeat - exitting");
                }
            } catch (Throwable e) {
                LOG.error(e.getMessage(), e);
            }
        } while (repeat);
    }
}
