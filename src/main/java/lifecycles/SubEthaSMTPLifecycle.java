package lifecycles;

import java.io.IOException;
import java.net.InetAddress;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import mail.MailListener;
import mail.SMTPAuthHandlerFactory;
import models.Inbox;

@ApplicationScoped
public class SubEthaSMTPLifecycle {

    private static final Logger logger = Logger.getLogger(SubEthaSMTPLifecycle.class);

    @Inject
    Inbox inbox;

    @ConfigProperty(name = "mail-baku.mail.bind.address", defaultValue = "0.0.0.0")
    String bindAddress;

    @ConfigProperty(name = "mail-baku.mail.port", defaultValue = "1025")
    int port;

    private SMTPServer smtpServer = null;

    void startService(@Observes StartupEvent event) {
        logger.info("Starting mail server.");
        try {
            MailListener myListener = new MailListener(inbox);
            smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(myListener), new SMTPAuthHandlerFactory());

            smtpServer.setBindAddress(InetAddress.getByName(bindAddress));
            smtpServer.setPort(port);
            smtpServer.start();
        } catch (IOException | RuntimeException e) {
            logger.error("Failed to start mail server.", e);
        }
    }

    void stopService(@Observes ShutdownEvent event) {
        logger.info("Stopping mail server.");
        if (smtpServer != null && smtpServer.isRunning()) {
            smtpServer.stop();
        }
    }

    public SMTPServer getSMTPServer() {
        return smtpServer;
    }

}
