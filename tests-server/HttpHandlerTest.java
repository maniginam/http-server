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
        server = new HttpServer(3141, "/Users/maniginam/server-task/http-spec/testroot");
        parser = new HttpParser();
    }

    @Test
    public void isHandler() throws IOException, InterruptedException {
        HttpHandler httpHandler = new HttpHandler(3141, "/Users/maniginam/server-task/http-spec/testroot");
        assertTrue(httpHandler instanceof Handler);
    }

    @Test
    public void createsServerOnInit() throws IOException, InterruptedException {
//        String message = "HTTP/1.1 200 OK\r\n\" + server.getConfigMessage()" + "\r\n\r\n";
//        String result = handler.init();

        assertNotNull(handler.getServer());
    }

//    @Test
//    public void submitsBlankEntry() throws Exception {
//        Path path = Path.of("/Users/maniginam/server-task/http-spec/testroot/index.html");
//        String fileContent = Files.readString(path, StandardCharsets.UTF_8);
//
//        byte[] result = handler.handle("GET HTTP/1.1");
//
//        assertEquals(parser.wrapHeader(fileContent, "").getBytes(), result);
//    }
//
//    @Test
//    public void submitsForwardSlashEntry() throws Exception {
//        Path path = Path.of("/Users/maniginam/server-task/http-spec/testroot/index.html");
//        String fileContent = Files.readString(path, StandardCharsets.UTF_8);
//
//        byte[] result = handler.handle("GET / HTTP/1.1");
//
////        assertEquals(HttpParser.wrapHeader(fileContent, ""), result);
//    }

}

