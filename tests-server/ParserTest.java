import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {

    private HttpParser parser;

    @BeforeEach
    public void setup() {
        parser = new HttpParser();
    }

    @Test
    public void status() {
        parser.setStatus(200, "OK");
        String result = parser.getStatus();

        assertEquals("HTTP/1.1 200 OK\r\n", result);
    }

    @Test
    public void headerField() {
        parser.setHeaderField("hello", null, "");
        String result = parser.getHeaderField();

        assertEquals("Server: Gina's Http Server\r\nContent-Length: 5\r\n\r\n", result);
    }

    @Test
    public void entireHeader() {
        String msg = "HTTP/1.1 200 OK\r\n" +
                "Server: Gina's Http Server\r\nContent-Length: 5\r\n\r\n";

        parser.setStatus(200, "OK");
        parser.setHeaderField("hello", null, "");
        String result = parser.getHeader();

        assertEquals(msg, result);
    }

    @Test
    public void standardHeader() {
        String msg = "";
        parser.setStatus(200, "OK");
        String result = parser.getStatus();

                assertEquals("HTTP/1.1 200 OK\r\n", result);
//        assertEquals("HTTP/1.1 200 OK\r\n" +
//                "Server: Gina's Http Server\r\n" +
//                "Content-Length: " + msg.length() + "\r\n" +
//                "\r\n", result);
    }

    @Test
    public void badRequestHeader() {
        String msg = "bad page";
        String result = parser.wrapBadRequest(msg);

        assertEquals("HTTP/1.1 404 bad page\r\n" +
                "Server: Gina's Http Server\r\n" +
                "Content-Length: " + msg.length() + "\r\n" +
                "\r\n" + msg, result);
    }

    @Test
    public void withHeaderFields() {
        String msg = "got fields";
        String fields = "First: Rex, Second: Leo";

        String result = parser.wrapHeader(msg, fields);

        assertEquals("HTTP/1.1 200 OK\r\n" +
                "Server: Gina's Http Server\r\n" +
                "Content-Length: " + msg.length() + "\r\n" +
                "First: Rex\r\n" +
                "Second: Leo\r\n" +
                "\r\n" + msg, result);

    }
}
