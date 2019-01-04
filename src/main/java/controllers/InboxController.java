package controllers;

import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import filters.IPAddressFilter;
import models.Attachment;
import models.Inbox;
import models.Mail;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;

@Singleton
@FilterWith(IPAddressFilter.class)
public class InboxController {
    private static final int LIMIT = 10;

    @Inject
    Inbox inbox;

    public Result index(@Param("next") String next, @Param("prev") String prev) throws Exception {
        List<Mail> mails = StringUtils.isNotBlank(next) ? inbox.listNext(LIMIT, next) : inbox.listPrev(LIMIT, prev);
        Optional<Mail> first = mails.stream().findFirst();
        Optional<Mail> last = mails.stream().reduce((f, second) -> second);
        Result result = Results.html();
        result.render("mails", mails);
        result.render("first", first);
        result.render("last", last);
        return result;
    }

    public Result show(@PathParam("id") String id) throws Exception {
        Mail mail = inbox.get(id);
        Result result = Results.html();
        result.render("mail", mail);
        return result;
    }

    public Result attachment(@PathParam("id") String id, @PathParam("index") int index) throws Exception {
        Mail mail = inbox.get(id);
        Attachment attachment = mail.getAttachment(index).get();

        byte[] data = IOUtils.toByteArray(inbox.getAttachmentStream(id, index));
        return Results.contentType(attachment.getContentType()).renderRaw(data);
    }

    public Result raw(@PathParam("id") String id) throws Exception {
        byte[] data = inbox.getRaw(id);
        return Results.contentType("text/plan").renderRaw(data);
    }

}
