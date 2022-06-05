package controllers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.junit.Test;

import ninja.NinjaDocTester;

public class ApplicationControllerDocTesterTest extends NinjaDocTester {

    @Test
    public void testGetIndex() {
        Response response = makeRequest(Request.GET().url(testServerUrl().path("/")));
        assertThat(response.payload, containsString("Inbox"));
    }
}
