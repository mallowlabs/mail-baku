package controllers;

import org.subethamail.smtp.server.SMTPServer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import filters.IPAddressFilter;
import lifecycles.SubEthaSMTPLifecycle;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;

@Singleton
@FilterWith(IPAddressFilter.class)
public class ServerController {

    @Inject
    SubEthaSMTPLifecycle lifecycle;

    public Result index() throws Exception {
        SMTPServer server = lifecycle.getSMTPServer();
        Result result = Results.html();
        result.render("server", server);
        return result;
    }

}
