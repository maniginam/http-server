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

    @BeforeEach
    public void setup() throws IOException {
        handler = new HttpHandler(3141, "/Users/maniginam/server-task/http-spec/testroot");
        server = new HttpServer("/Users/maniginam/server-task/http-spec/testroot");
        parser = new HttpParser();
    }

    @Test
    public void submitBlank() throws Exception {
        Path path = Path.of("/Users/maniginam/server-task/http-spec/testroot/index.html");
        String fileContent = Files.readString(path, StandardCharsets.UTF_8);

        server.submitRequest("GET HTTP/1.1");
        String bodyMessage = server.getBodyMessage();

        assertEquals(fileContent, bodyMessage);
    }

    @Test
    public void submitForwardSlash() throws IOException, ExceptionInfo {
        Path path = Path.of("/Users/maniginam/server-task/http-spec/testroot/index.html");
        String fileContent = Files.readString(path, StandardCharsets.UTF_8);

        server.submitRequest("GET / HTTP/1.1");
        String bodyMessage = server.getBodyMessage();

        assertEquals(fileContent, bodyMessage);
    }

    @Test
    public void submitGarbage() {
        String msg = "GET /rex HTTP/1.1";
        assertThrows(ExceptionInfo.class, () -> {
            server.submitRequest(msg);
        });

    }


}
