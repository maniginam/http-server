import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class FormsTest {

    private HttpHandler handler;
    private HttpServer server;
    private FormInputHandler form;
    private HandlerFactory factory;
    private SocketHost host;
    private TestHelper helper;

    @BeforeEach
    public void setup() throws IOException {
        helper = new TestHelper();
        factory = new HttpHandlerFactory(3141, "/Users/maniginam/server-task/http-spec/testroot");
        host = new SocketHost(3141, factory);
        handler = new HttpHandler(3141, "/Users/maniginam/server-task/http-spec/testroot");
        server = new HttpServer("/Users/maniginam/server-task/http-spec/testroot");
    }

    @Test
    public void formIsCalled() throws IOException, ExceptionInfo {
        handler.handle("GET /forms.html HTTP/1.1", null);
        String response = handler.getServer().getResponseBody();

        assertTrue(response.contains("<h2>Get Form</h2>"));
        assertTrue(response.contains("<form method=\"get\" action=\"/form\""));
        assertTrue(response.contains("<input type=\"submit\" value=\"Submit\""));
    }

    @Test
    public void oneFormEntry() throws IOException, ExceptionInfo {
        handler.handle("GET /form?foo=1", null);

        String body = handler.getServer().getResponseBody();

        assertTrue(body.contains("<h2>GET Form</h2>"));
        assertTrue(body.contains("<li>foo: 1</li>"));
        assertFalse(body.contains("<li>bar: 2</li>"));
    }

    @Test
    public void twoFormEntries() throws IOException, ExceptionInfo {
        String request = "GET /form?foo=1&bar=2";
        handler.handle(request, null);

        String body = handler.getServer().getResponseBody();

        assertTrue(body.contains("<h2>GET Form</h2>"));
        assertTrue(body.contains("<li>foo: 1</li>"));
        assertTrue(body.contains("<li>bar: 2</li>"));
    }

    @Test
    public void twoFormEntriesWithLongInputs() throws IOException, ExceptionInfo {
        String request = "GET /form?foo=Rex&bar=Leo";
        handler.handle(request, null);

        String body = handler.getServer().getResponseBody();

        assertTrue(body.contains("<h2>GET Form</h2>"));
        assertTrue(body.contains("<li>foo: Rex</li>"));
        assertTrue(body.contains("<li>bar: Leo</li>"));
    }

    @Test
    public void postIsPresent() throws IOException, ExceptionInfo {
        handler.handle("GET /forms.html HTTP/1.1", null);
        String response = handler.getServer().getResponseBody();

        assertTrue(response.contains("<h2>Post Form</h2>"));
        assertTrue(response.contains("<form method=\"post\" action=\"/form\" enctype=\"multipart/form-data\">"));
        assertTrue(response.contains("<input type=\"file\" name=\"file\"/>"));
        assertTrue(response.contains("<input type=\"submit\" value=\"Submit\""));
    }

    @Test
    public void postImage() throws IOException, ExceptionInfo {
        host.start();
        helper.connect();
        OutputStream output = helper.getOutput();
        BufferedInputStream buffedInput = helper.getBuffedInput();

        File file = new File("/Users/maniginam/server-task/http-server/BruslyDog.jpeg");
        FileInputStream input = new FileInputStream(file);
        byte[] image = input.readAllBytes();
        ByteArrayOutputStream requestImage = new ByteArrayOutputStream();

        String requestHeader1 = "POST /form HTTP/1.1\r\n" +
                "Name: file\r\n" +
                "Content-Type: multipart/form-data; boundary=----Rex&Leo\r\n" +
                "Content: " + file + "\r\n" +
                "Content-Length: " + image.length;

        String requestHeader2 = requestHeader1 + "\r\n------Rex&Leo\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"BruslyDog.jpeg\"\r\n" +
                "Content-Type: image/jpeg";

        requestImage.write((requestHeader2 + "\r\n\r\n").getBytes());
        requestImage.write(image);

        handler.handle(requestHeader1, null);
        handler.handle(requestHeader2, null);
        handler.handle(requestHeader2, requestImage.toByteArray());

        String requestHeaderResult = handler.getServer().getRequestHeader();
        String response = handler.getServer().getResponseBody();

        assertEquals(requestHeader2, requestHeaderResult);
        assertTrue(requestHeaderResult.contains("Content-Type: image/jpeg"));
        assertTrue(response.contains("<h2>POST Form</h2>"));
        assertTrue(response.contains("<li>file name: BruslyDog.jpeg</li>"));
        assertTrue(response.contains("<li>file size: 92990</li>"));
        assertTrue(response.contains("<li>content type: application/octet-stream</li>"));

    }

    @Test
    public void boxMapWDisposition() throws IOException, ExceptionInfo {
        PostFormHandler poster = new PostFormHandler();
        String request = "Rex\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"Leo.jpeg\"\r\n" +
                "Content-Type: image/jpeg";

        poster.handle(request, 3);
        String result = poster.getResponseBody();

        assertTrue(result.contains("<li>file name: Leo.jpeg</li>"));

    }

}
