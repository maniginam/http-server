import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class HttpHandlerTest {

    private HttpHandler handler;
    private HttpServer server;
    private HttpParser parser;


    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        handler = new HttpHandler(3141, "/Users/maniginam/server-task/http-spec/testroot");
        server = new HttpServer("/Users/maniginam/server-task/http-spec/testroot");
        parser = new HttpParser();
    }

    @Test
    public void isHandler() throws IOException, InterruptedException {
        HttpHandler httpHandler = new HttpHandler(3141, "/Users/maniginam/server-task/http-spec/testroot");
        assertTrue(httpHandler instanceof Handler);
    }

    @Test
    public void submitsBlankEntry() throws Exception {
        byte[] result = handler.handle("GET HTTP/1.1".getBytes());

        assertArrayEquals(handler.getServer().getResponse(), result);
    }

    @Test
    public void submitsForwardSlashEntry() throws Exception {
        byte[] result = handler.handle("GET / HTTP/1.1".getBytes());

        assertArrayEquals(handler.getServer().getResponse(), result);
    }

}

