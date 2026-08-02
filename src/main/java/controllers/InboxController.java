package controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.Attachment;
import models.Inbox;
import models.Mail;
import views.ViewRenderer;

@Path("/")
public class InboxController {
    private static final int LIMIT = 10;

    @Inject
    Inbox inbox;

    @Inject
    ViewRenderer views;

    @GET
    @Path("inbox")
    @Produces(MediaType.TEXT_HTML)
    public String index(@QueryParam("next") String next, @QueryParam("prev") String prev, @Context HttpHeaders headers) throws Exception {
        List<Mail> mails = StringUtils.isNotBlank(next) ? inbox.listNext(LIMIT, next) : inbox.listPrev(LIMIT, prev);
        Optional<Mail> first = mails.stream().findFirst();
        Optional<Mail> last = mails.stream().reduce((f, second) -> second);
        return views.render("InboxController/index.ftl.html", Map.of("mails", mails, "first", first, "last", last), headers);
    }

    @GET
    @Path("inbox/{id}")
    @Produces(MediaType.TEXT_HTML)
    public String show(@PathParam("id") String id, @Context HttpHeaders headers) throws Exception {
        Mail mail = inbox.get(id);
        return views.render("InboxController/show.ftl.html", Map.of("mail", mail), headers);
    }

    @GET
    @Path("inbox/{id}/attachment/{index}/{filename}")
    public Response attachment(@PathParam("id") String id, @PathParam("index") int index) throws Exception {
        Mail mail = inbox.get(id);
        Attachment attachment = mail.getAttachment(index).get();

        byte[] data = IOUtils.toByteArray(inbox.getAttachmentStream(id, index));
        return Response.ok(data, attachment.getContentType()).build();
    }

    @GET
    @Path("inbox/{id}/raw")
    @Produces(MediaType.TEXT_PLAIN)
    public byte[] raw(@PathParam("id") String id) throws Exception {
        return inbox.getRaw(id);
    }

    ///////////////////////////////////////////////////////////////////////
    // Index / Catchall shows index page
    ///////////////////////////////////////////////////////////////////////
    @GET
    @Path("{path: .*}")
    @Produces(MediaType.TEXT_HTML)
    public String catchAll(@QueryParam("next") String next, @QueryParam("prev") String prev, @Context HttpHeaders headers) throws Exception {
        return index(next, prev, headers);
    }

}
