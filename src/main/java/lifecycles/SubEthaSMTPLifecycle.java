package lifecycles;

import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import mail.MailListener;
import mail.SMTPAuthHandlerFactory;
import models.Inbox;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.NinjaProperties;

@Singleton
public class SubEthaSMTPLifecycle {
    @Inject
    Logger logger;

    @Inject
    Inbox inbox;

    @Inject
    NinjaProperties ninjaProperties;

    private SMTPServer smtpServer = null;

    @Start(order = 90)
    public void startService() {
        logger.info("Starting mail server.");
        try {

            MailListener myListener = new MailListener(inbox);
            smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(myListener), new SMTPAuthHandlerFactory());

            smtpServer.setBindAddress(InetAddress.getByName(ninjaProperties.getWithDefault("mail-baku.mail.bind.address", "0.0.0.0")));
            smtpServer.setPort(ninjaProperties.getIntegerWithDefault("mail-baku.mail.port", 1025));
            smtpServer.start();
        } catch (IOException e) {
            logger.error("Failed to start mail server.", e);
        }
    }

    @Dispose(order = 90)
    public void stopService() {
        logger.info("Stopping mail server.");
        smtpServer.stop();
    }

    public SMTPServer getSMTPServer() {
        return smtpServer;
    }
}
