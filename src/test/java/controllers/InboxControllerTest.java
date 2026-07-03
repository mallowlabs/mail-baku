package controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class InboxControllerTest {

    @Test
    void testGetIndex() {
        given()
            .when().get("/")
            .then()
            .statusCode(200)
            .body(containsString("Inbox"));
    }

    @Test
    void testGetIndexJapanese() {
        given()
            .header("Accept-Language", "ja")
            .when().get("/")
            .then()
            .statusCode(200)
            .body(containsString("受信トレイ"));
    }

    @Test
    void testReceiveMail() throws Exception {
        String subject = "Hello mail-baku " + System.currentTimeMillis();

        Properties props = new Properties();
        props.put("mail.smtp.host", "localhost");
        props.put("mail.smtp.port", "2525");
        Session session = Session.getInstance(props);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("from@example.com"));
        message.setRecipients(Message.RecipientType.TO, "to@example.com");
        message.setSubject(subject);
        message.setText("This is a test mail.");
        message.setSentDate(new java.util.Date());
        Transport.send(message);

        given()
            .when().get("/")
            .then()
            .statusCode(200)
            .body(containsString(subject));
    }

    @Test
    void testGetWebJars() {
        given()
            .when().get("/webjars/bootstrap/5.3.8/css/bootstrap.min.css")
            .then()
            .statusCode(200);
    }

    @Test
    void testGetAssets() {
        given()
            .when().get("/assets/css/custom.css")
            .then()
            .statusCode(200);
    }

    @Test
    void testGetServer() {
        given()
            .when().get("/server")
            .then()
            .statusCode(200)
            .body(containsString("SMTP Server"));
    }

}
