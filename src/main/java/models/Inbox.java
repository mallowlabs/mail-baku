package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.google.inject.Inject;

import ninja.utils.NinjaProperties;

public class Inbox {

    @Inject
    private Logger logger;

    @Inject
    NinjaProperties ninjaProperties;

    private File directory;

    public Inbox() throws IOException {
        File tmp = File.createTempFile("mail-baku", "txt");
        directory = new File(tmp.getParentFile(), "mail-baku");
        tmp.delete();
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public void save(InputStream is) throws IOException {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String filename = sdf.format(now) + Mail.EXT;
        File mailFile = new File(directory, filename);
        logger.info(mailFile.getAbsolutePath());
        try (OutputStream os = new FileOutputStream(mailFile)) {
            IOUtils.copy(is, os);
        }
    }

    public List<String> listIds() throws IOException {
        List<String> ids = new ArrayList<>();
        Files.newDirectoryStream(directory.toPath(), "*" + Mail.EXT).forEach(s -> ids.add(FilenameUtils.getBaseName(s.getFileName().toFile().getName())));
        Collections.sort(ids);
        Collections.reverse(ids);
        return ids;
    }

    public List<Mail> listPrev(int limit, String prev) throws IOException {
        return listIds()
            .stream()
            .filter(s -> StringUtils.isEmpty(prev) || s.compareTo(prev) < 0)
            .limit(limit)
            .map(s -> this.getSafe(s))
            .filter(m -> m != null)
            .sorted((m1, m2) -> m2.getSentDate().compareTo(m1.getSentDate()))
            .collect(Collectors.toList());
    }

    public List<Mail> listNext(int limit, String next) throws IOException {
        return listIds()
            .stream()
            .filter(s -> StringUtils.isEmpty(next) || s.compareTo(next) > 0)
            .limit(limit)
            .map(s -> this.getSafe(s))
            .filter(m -> m != null)
            .sorted((m1, m2) -> m2.getSentDate().compareTo(m1.getSentDate()))
            .collect(Collectors.toList());
    }

    public Mail get(String id) throws IOException, MessagingException {
        Mail mail = new Mail();
        try (InputStream in = new FileInputStream(new File(directory, id + Mail.EXT))) {
            Session session = Session.getDefaultInstance(new Properties(), null);
            MimeMessage message = new MimeMessage(session, in);

            mail.setId(id);
            if (message.getContent() instanceof MimeMultipart) {
                MimeMultipart multipart = (MimeMultipart) message.getContent();
                mail.setContent(multipart.getBodyPart(0).getContent().toString());

                List<Attachment> list = new ArrayList<>();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart part = multipart.getBodyPart(i);
                    if (StringUtils.equals(Part.ATTACHMENT, part.getDisposition()) || StringUtils.equals(Part.INLINE, part.getDisposition())) {
                        String filename = StringUtils.defaultIfBlank(part.getFileName(), "unknown");
                        filename = MimeUtility.decodeText(filename);

                        Attachment attachment = new Attachment();
                        attachment.setIndex(i);
                        attachment.setFilename(filename);
                        attachment.setContentType(StringUtils.split(part.getContentType(), ';')[0]);
                        list.add(attachment);
                    }
                }
                mail.setAttachments((Attachment[]) list.toArray(new Attachment[list.size()]));

            } else {
                mail.setContent(message.getContent().toString());
            }
            mail.setSubject(message.getSubject());
            mail.setToAddresses(message.getRecipients(Message.RecipientType.TO));
            mail.setCcAddresses(message.getRecipients(Message.RecipientType.CC));
            mail.setBccAddresses(message.getRecipients(Message.RecipientType.BCC));
            mail.setFromAddresses(message.getFrom());
            mail.setSentDate(message.getSentDate());
        }

        return mail;
    }

    public InputStream getAttachmentStream(String id, int index) throws IOException, MessagingException {
        try (InputStream in = new FileInputStream(new File(directory, id + Mail.EXT))) {
            Session session = Session.getDefaultInstance(new Properties(), null);
            MimeMessage message = new MimeMessage(session, in);
            if (message.getContent() instanceof MimeMultipart) {
                MimeMultipart multipart = (MimeMultipart) message.getContent();
                BodyPart part = multipart.getBodyPart(index);
                if (StringUtils.equals(Part.ATTACHMENT, part.getDisposition()) || StringUtils.equals(Part.INLINE, part.getDisposition())) {
                    return part.getInputStream();
                }
            }
        }
        return null;
    }

    public byte[] getRaw(String id) throws IOException {
        return FileUtils.readFileToByteArray(new File(directory, id + Mail.EXT));
    }

    public Mail getSafe(String id) {
        try {
            return get(id);
        } catch (IOException | MessagingException e) {
        }
        return null;
    }

}
