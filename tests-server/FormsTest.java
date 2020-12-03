import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class FormsTest {

    private HttpHandler handler;
    private HttpServer server;
    private FormInputHandler form;

    @BeforeEach
    public void setup() throws IOException {
        handler = new HttpHandler(3141, "/Users/maniginam/server-task/http-spec/testroot");
        server = new HttpServer("/Users/maniginam/server-task/http-spec/testroot");
    }

    @Test
    public void formIsCalled() throws IOException, ExceptionInfo {
        handler.handle("GET /forms.html HTTP/1.1".getBytes());
        String response = handler.getServer().getBodyMessage();

        assertTrue(response.contains("<h2>Get Form</h2>"));
        assertTrue(response.contains("<form method=\"get\" action=\"/form\""));
        assertTrue(response.contains("<input type=\"submit\" value=\"Submit\""));
    }

    @Test
    public void oneFormEntry() throws IOException, ExceptionInfo {
        handler.handle("GET /form?foo=1".getBytes());

        String body = handler.getServer().getBodyMessage();

        assertTrue(body.contains("<h2>GET Form</h2>"));
        assertTrue(body.contains("<li>foo: 1</li>"));
        assertFalse(body.contains("<li>bar: 2</li>"));
    }

    @Test
    public void twoFormEntries() throws IOException, ExceptionInfo {
        String request = "GET /form?foo=1&bar=2";
        handler.handle(request.getBytes());

        String body = handler.getServer().getBodyMessage();

        assertTrue(body.contains("<h2>GET Form</h2>"));
        assertTrue(body.contains("<li>foo: 1</li>"));
        assertTrue(body.contains("<li>bar: 2</li>"));
    }

    @Test
    public void twoFormEntriesWithLongInputs() throws IOException, ExceptionInfo {
        String request = "GET /form?foo=Rex&bar=Leo";
        handler.handle(request.getBytes());

        String body = handler.getServer().getBodyMessage();

        assertTrue(body.contains("<h2>GET Form</h2>"));
        assertTrue(body.contains("<li>foo: Rex</li>"));
        assertTrue(body.contains("<li>bar: Leo</li>"));
    }

    @Test
    public void postIsPresent() throws IOException, ExceptionInfo {
        handler.handle("GET /forms.html HTTP/1.1".getBytes());
        String response = handler.getServer().getBodyMessage();

        assertTrue(response.contains("<h2>Post Form</h2>"));
        assertTrue(response.contains("<form method=\"post\" action=\"/form\" enctype=\"multipart/form-data\">"));
        assertTrue(response.contains("<input type=\"file\" name=\"file\"/>"));
        assertTrue(response.contains("<input type=\"submit\" value=\"Submit\""));
    }

    @Test
    public void postImage() throws IOException, ExceptionInfo {
        File file = new File("/Users/maniginam/server-task/http-server/BruslyDog.jpeg");
//        File file = new File("/Users/maniginam/server-task/http-spec/testroot/img/autobot.jpg");
        FileInputStream input = new FileInputStream(file);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.readAllBytes());
        byte[] image = inputStream.readAllBytes();
        ByteArrayOutputStream request = new ByteArrayOutputStream();

        String post = "POST /form HTTP/1.1\r\n" +
                "Name: file\r\n" +
                "Content-Type: image/jpeg\r\n" +
                "Content: " + file + "\r\n" +
//                "Content: /Users/maniginam/server-task/http-spec/testroot/img/autobot.jpg\r\n" +
                "Content-Length: " + image.length;

        request.write((post + "\r\n\r\n").getBytes());
        request.write(image);

        handler.handle(request.toByteArray());
        int requestCount = handler.getServer().getNumberOfRequestParts();
        String requestHeader = handler.getServer().getRequestHeader();
        byte[] requestBody = handler.getServer().getRequestBody();
        String header = handler.getServer().getHeader();
        String response = handler.getServer().getBodyMessage();


        assertEquals(2, requestCount);
        assertEquals(post, requestHeader);
//        assertArrayEquals(image, requestBody);
        assertTrue(header.contains("Content-Type: image/jpeg"));
        assertTrue(response.contains("<li>file name: BruslyDog.jpeg</li>"));
        assertTrue(response.contains("<li>file size: 93178</li>"));
        assertTrue(response.contains("<li>content type: application/octet-stream</li>"));

    }


}
