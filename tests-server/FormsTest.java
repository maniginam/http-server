import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FormsTest {

    private HttpHandler handler;
    private HttpServer server;
    private FormHandler form;

    @BeforeEach
    public void setup() throws IOException {
        handler = new HttpHandler(3141, "/Users/maniginam/server-task/http-spec/testroot");
        server = new HttpServer("/Users/maniginam/server-task/http-spec/testroot");
    }

    @Test
    public void formIsCalled() throws IOException, ExceptionInfo {
        handler.handle("GET /forms.html HTTP/1.1");
        String response = handler.getServer().getBodyMessage();

        assertTrue(response.contains("<h2>Get Form</h2>"));
        assertTrue(response.contains("<form method=\"get\" action=\"/form\""));
        assertTrue(response.contains("<input type=\"submit\" value=\"Submit\""));
    }

    @Test
    public void oneFormEntry() throws IOException, ExceptionInfo {
        handler.handle("GET /form?foo=1");

        String body = handler.getServer().getBodyMessage();

        assertTrue(body.contains("<h2>GET Form</h2>"));
        assertTrue(body.contains("<li>foo: 1</li>"));
        assertFalse(body.contains("<li>bar: 2</li>"));
    }

    @Test
    public void twoFormEntries() throws IOException, ExceptionInfo {
        String request = "GET /form?foo=1&bar=2";
        handler.handle(request);

        String body = handler.getServer().getBodyMessage();

        assertTrue(body.contains("<h2>GET Form</h2>"));
        assertTrue(body.contains("<li>foo: 1</li>"));
        assertTrue(body.contains("<li>bar: 2</li>"));
    }

    @Test
    public void twoFormEntriesWithLongInputs() throws IOException, ExceptionInfo {
        String request = "GET /form?foo=Rex&bar=Leo";
        handler.handle(request);

        String body = handler.getServer().getBodyMessage();

        assertTrue(body.contains("<h2>GET Form</h2>"));
        assertTrue(body.contains("<li>foo: Rex</li>"));
        assertTrue(body.contains("<li>bar: Leo</li>"));
    }


}
