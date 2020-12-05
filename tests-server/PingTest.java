import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PingTest {

    private HttpHandler handler;
    private HttpServer server;
    private HttpParser parser;

    @BeforeEach
    public void setup() throws IOException {
        handler = new HttpHandler(3141, "/Users/maniginam/server-task/http-spec/testroot");
        server = new HttpServer("/Users/maniginam/server-task/http-spec/testroot");
        parser = new HttpParser();
    }

    @Test
    public void blankPing() throws IOException, ExceptionInfo {

        server.submitRequest("GET /ping HTTP/1.1", null);
        String header = server.getResponseHeader();
        String body = server.getResponseBodyMessage();

        assertTrue(header.contains("HTTP/1.1 200 OK"));
        assertTrue(body.contains("<h1>Ping</h1>"));
        assertTrue(body.contains("<li>start time: "));
        assertTrue(body.contains("<li>end time: "));
        assertTrue(body.contains("<li>sleep seconds: 0</li>"));
    }

    @Test
    public void oneSecondPing() throws IOException, ExceptionInfo {

        server.submitRequest("GET /ping/1 HTTP/1.1", null);
        String header = server.getResponseHeader();
        String body = server.getResponseBodyMessage();

        assertTrue(header.contains("HTTP/1.1 200 OK"));
        assertTrue(body.contains("<h2>Ping</h2>"));
        assertTrue(body.contains("<li>start time: "));
        assertTrue(body.contains("<li>end time: "));
        assertTrue(body.contains("<li>sleep seconds: 1</li>"));
    }
}
