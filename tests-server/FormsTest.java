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
        ByteArrayOutputStream request1 = new ByteArrayOutputStream();
        ByteArrayOutputStream request2 = new ByteArrayOutputStream();
        ByteArrayOutputStream request3 = new ByteArrayOutputStream();

        String requestHeader1 = "POST /form HTTP/1.1\r\n" +
                "Name: file\r\n" +
                "Content-Type: multipart/form-data; boundary=----Rex&Leo\r\n" +
                "Content: " + file + "\r\n" +
//                "Content: /Users/maniginam/server-task/http-spec/testroot/img/autobot.jpg\r\n" +
                "Content-Length: " + image.length;

        String requestHeader2 = requestHeader1 + "\r\n------Rex&Leo\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"BruslyDog.jpeg\"\r\n" +
                "Content-Type: image/jpeg";

        request1.write((requestHeader1 + "\r\n\r\n").getBytes());
        request2.write((requestHeader2 + "\r\n\r\n").getBytes());
        request3.write((requestHeader2 + "\r\n\r\n").getBytes());
        request3.write(image);

        handler.handle(request1.toByteArray());
        handler.handle(request2.toByteArray());
        handler.handle(request3.toByteArray());

        int requestCount = handler.getServer().getNumberOfRequestParts();
        String headerResult = handler.getServer().getRequestHeader();
        byte[] requestBody = handler.getServer().getRequestBody();
        String header = handler.getServer().getHeader();
        String response = handler.getServer().getBodyMessage();


        assertEquals(2, requestCount);
        assertEquals(requestHeader2, headerResult);
//        assertArrayEquals(image, requestBody);
//        assertTrue(header.contains("Content-Type: image/jpeg"));
        assertTrue(response.contains("<h2>POST Form</h2>"));
        assertTrue(response.contains("<li>file name: BruslyDog.jpeg</li>"));
        assertTrue(response.contains("<li>file size: 92990</li>"));
        assertTrue(response.contains("<li>content type: application/octet-stream</li>"));

    }

    @Test
    public void boxMapWDisposition() {
        PostFormHandler poster = new PostFormHandler();
        String request = "Rex\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"Leo.jpeg\"\r\n" +
                "Content-Type: image/jpeg";

        String result = poster.handle(request, 3);

        assertTrue(result.contains("<li>file name: Leo.jpeg</li>"));

    }

}
