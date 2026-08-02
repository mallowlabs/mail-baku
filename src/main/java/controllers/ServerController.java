package controllers;

import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import lifecycles.SubEthaSMTPLifecycle;
import views.ViewRenderer;

@Path("server")
public class ServerController {

    @Inject
    SubEthaSMTPLifecycle lifecycle;

    @Inject
    ViewRenderer views;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String index(@Context HttpHeaders headers) throws Exception {
        return views.render("ServerController/index.ftl.html", Map.of("server", lifecycle.getSMTPServer()), headers);
    }

}
