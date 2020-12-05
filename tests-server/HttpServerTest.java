import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {

    private HttpServer server;
    private HttpParser parser;
    private HttpHandler handler;
    private RequestParser requester;

    @BeforeEach
    public void setup() throws IOException {
        handler = new HttpHandler(3141, "/Users/maniginam/server-task/http-spec/testroot");
        server = new HttpServer("/Users/maniginam/server-task/http-spec/testroot");
        parser = new HttpParser();
        requester = new RequestParser();
    }

    @Test
    public void submitBlank() throws Exception {
        Path path = Path.of("/Users/maniginam/server-task/http-spec/testroot/index.html");
        String fileContent = Files.readString(path, StandardCharsets.UTF_8);

        server.submitRequest("GET HTTP/1.1", null);
        String bodyMessage = server.getResponseBodyMessage();

        assertEquals(fileContent, bodyMessage);
    }

    @Test
    public void submitForwardSlash() throws IOException, ExceptionInfo {
        Path path = Path.of("/Users/maniginam/server-task/http-spec/testroot/index.html");
        String fileContent = Files.readString(path, StandardCharsets.UTF_8);

        server.submitRequest("GET / HTTP/1.1", null);
        String bodyMessage = server.getResponseBodyMessage();

        assertEquals(fileContent, bodyMessage);
    }

    @Test
    public void submitGarbage() {
        String header = "GET /rex HTTP/1.1";
        assertThrows(ExceptionInfo.class, () -> {
            server.submitRequest(header, null);
        });
    }

    @Test
    public void requestWithBody() throws IOException, ExceptionInfo {
        String body = "Rex is 3, and Leo is 1";
        server.submitRequest(("GET / HTTP/1.1\r\n" +
                "Content-Length: " + body.length() + "\r\n\r\n"), body.getBytes());

        String result = new String(server.getRequestBody(), StandardCharsets.UTF_8);
        assertEquals(body, result);
    }


}
