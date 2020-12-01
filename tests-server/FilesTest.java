import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FilesTest {
    private HttpServer server;
    private Exception ExceptionInfo;
    private HttpHandler handler;
    private HttpParser parser;

    @BeforeEach
    public void setup() throws IOException {
        server = new HttpServer(3141, "/Users/maniginam/server-task/http-spec/testroot");
        handler = new HttpHandler(3141, "/Users/maniginam/server-task/http-spec/testroot");
        parser = new HttpParser();
    }

    @Test
    public void submitListing() throws IOException, ExceptionInfo {
        String get = "Get /listing HTTP/1.1";
        server.submitRequest(get);
        String result = server.getBodyMessage();

        assertTrue(result.contains("<ul>"));
        assertTrue(result.contains("<li><a href=\"/index.html\">index.html</a></li>"));
        assertTrue(result.contains("<li><a href=\"/hello.pdf\">hello.pdf</a></li>"));
        assertTrue(result.contains("<li><a href=\"/listing/img\">img</a></li>"));
    }

    @Test
    public void submitListingImgs() throws IOException, ExceptionInfo {
        String get = "GET /listing/img HTTP/1.1";
        server.submitRequest(get);
        String result = server.getBodyMessage();

        assertTrue(result.contains("<ul>"));
        assertTrue(result.contains("<li><a href=\"/img/autobot.jpg\">autobot.jpg</a></li>"));
        assertTrue(result.contains("<li><a href=\"/img/autobot.png\">autobot.png</a></li>"));
        assertTrue(result.contains("<li><a href=\"/img/decepticon.jpg\">decepticon.jpg</a></li>"));
        assertTrue(result.contains("<li><a href=\"/img/decepticon.png\">decepticon.png</a></li>"));
    }

    @Test
    public void serveHtmlFile() throws IOException, ExceptionInfo {
        Path path = Path.of("/Users/maniginam/server-task/http-spec/testroot/index.html");
        String msg = Files.readString(path, StandardCharsets.UTF_8);
        String get = "GET /listing/index.html HTTP/1.1";

        handler.handle(get);
        String fields = handler.getServer().getFields();
        String result = handler.getServer().getBodyMessage();


        assertEquals(msg, result);
        assertTrue(fields.contains("Content-Type"));
        assertTrue(fields.contains("text/html"));

    }

    @Test
    public void serveJPG() throws IOException, ExceptionInfo {
        String jpg = "autobot.jpg";
        String get = "GET /listing/img/autobot.jpg HTTP/1.1";

        byte[] body = server.convertFiletoBytes(jpg);

        handler.handle(get);
        String fields = handler.getServer().getFields();
        byte[] bodyResult = handler.getServer().getBodyBytes();

        assertTrue(fields.contains("Content-Type"));
        assertTrue(fields.contains("Content-Disposition"));
        assertTrue(fields.contains("image/jpeg"));
        assertArrayEquals(body, bodyResult);
    }

    @Test
    public void servePNG() throws IOException, ExceptionInfo {
        String png = "decepticon.png";
        String get = "GET /listing/img/decepticon.png HTTP/1.1";

        byte[] body = server.convertFiletoBytes(png);

        handler.handle(get);
        String fields = handler.getServer().getFields();
        byte[] bodyResult = handler.getServer().getBodyBytes();

        assertTrue(fields.contains("Content-Type"));
        assertTrue(fields.contains("Content-Disposition"));
        assertTrue(fields.contains("image/png"));
        assertArrayEquals(body, bodyResult);
    }

    @Test
    public void servePDF() throws IOException, ExceptionInfo {
        String pdf = "hello.pdf";
        String get = "GET /listing/img/hello.pdf HTTP/1.1";

        byte[] body = server.convertFiletoBytes(pdf);

        handler.handle(get);
        String fields = handler.getServer().getFields();
        byte[] bodyResult = handler.getServer().getBodyBytes();

        assertTrue(fields.contains("Content-Type"));
        assertTrue(fields.contains("Content-Disposition"));
        assertTrue(fields.contains("application/pdf"));
        assertArrayEquals(body, bodyResult);
    }
}

