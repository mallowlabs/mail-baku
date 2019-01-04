package mail;

import java.io.IOException;
import java.io.InputStream;

import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;

import models.Inbox;

public class MailListener implements SimpleMessageListener {
    private final Inbox inbox;

    public MailListener(Inbox inbox) {
        this.inbox = inbox;
    }

    @Override
    public boolean accept(String from, String recipient) {
        return true;
    }

    @Override
    public void deliver(String from, String recipient, InputStream data) throws TooMuchDataException, IOException {
        inbox.save(data);
    }

}
