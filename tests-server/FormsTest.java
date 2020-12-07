import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class FormsTest {
    private HttpHandler handler;
    private HandlerFactory factory;
    private SocketHost host;
    private TestHelper helper;
    private Socket socket;

    @BeforeEach
    public void setup() throws IOException {
        helper = new TestHelper();
        factory = new HttpHandlerFactory(3141, "/Users/maniginam/server-task/http-spec/testroot");
        host = new SocketHost(3141, factory);
        handler = new HttpHandler(3141, "/Users/maniginam/server-task/http-spec/testroot");
    }

    @AfterEach
    private void tearDown() throws Exception {
        host.stop();
        if (socket != null)
            socket.close();
    }

    @Test
    public void formIsCalled() throws IOException, ExceptionInfo {
        handler.handle("GET /forms.html HTTP/1.1", null);
        String response = handler.getServer().getResponseBodyMessage();

        assertTrue(response.contains("<h2>Get Form</h2>"));
        assertTrue(response.contains("<form method=\"get\" action=\"/form\""));
        assertTrue(response.contains("<input type=\"submit\" value=\"Submit\""));
    }

    @Test
    public void oneFormEntry() throws IOException, ExceptionInfo {
        handler.handle("GET /form?foo=1", null);

        String body = handler.getServer().getResponseBodyMessage();

        assertTrue(body.contains("<h2>GET Form</h2>"));
        assertTrue(body.contains("<li>foo: 1</li>"));
        assertFalse(body.contains("<li>bar: 2</li>"));
    }

    @Test
    public void twoFormEntries() throws IOException, ExceptionInfo {
        String request = "GET /form?foo=1&bar=2";
        handler.handle(request, null);

        String body = handler.getServer().getResponseBodyMessage();

        assertTrue(body.contains("<h2>GET Form</h2>"));
        assertTrue(body.contains("<li>foo: 1</li>"));
        assertTrue(body.contains("<li>bar: 2</li>"));
    }

    @Test
    public void twoFormEntriesWithLongInputs() throws IOException, ExceptionInfo {
        String request = "GET /form?foo=Rex&bar=Leo";
        handler.handle(request, null);

        String body = handler.getServer().getResponseBodyMessage();

        assertTrue(body.contains("<h2>GET Form</h2>"));
        assertTrue(body.contains("<li>foo: Rex</li>"));
        assertTrue(body.contains("<li>bar: Leo</li>"));
    }

    @Test
    public void postIsPresent() throws IOException, ExceptionInfo {
        handler.handle("GET /forms.html HTTP/1.1", null);
        String response = handler.getServer().getResponseBodyMessage();

        assertTrue(response.contains("<h2>Post Form</h2>"));
        assertTrue(response.contains("<form method=\"post\" action=\"/form\" enctype=\"multipart/form-data\">"));
        assertTrue(response.contains("<input type=\"file\" name=\"file\"/>"));
        assertTrue(response.contains("<input type=\"submit\" value=\"Submit\""));
    }

    @Test
    public void postImage() throws IOException, ExceptionInfo, InterruptedException {
        host.start();
        helper.connect();
        OutputStream output1 = helper.getOutput();
        OutputStream output2 = helper.getOutput();
        OutputStream output3 = helper.getOutput();
        BufferedInputStream buffedInput = helper.getBuffedInput();

        File file = new File("/Users/maniginam/server-task/http-server/BruslyDog.jpeg");
        FileInputStream input = new FileInputStream(file);
        byte[] image = input.readAllBytes();
        ByteArrayOutputStream requestImage = new ByteArrayOutputStream();
        requestImage.write(image);

        String requestHeader1 = "POST /form HTTP/1.1\r\n" +
                "Name: file\r\n" +
                "Content-Type: multipart/form-data; boundary=----Rex&Leo\r\n" +
                "Content: " + file + "\r\n" +
                "Content-Length: " + image.length + "\r\n" +
                "something blah\r\n";

        String requestHeader2 = requestHeader1 + "------Rex&Leo\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"BruslyDog.jpeg\"\r\n" +
                "Content-Type: image/jpeg\r\n";

        output1.write((requestHeader1 + "\r\n").getBytes());
        buffedInput.read();

        output2.write((requestHeader2 + "\r\n").getBytes());
        Thread.sleep(100);

        output3.write((requestHeader2 + "\r\n").getBytes());
        output3.write(image);
        output3.write("\r\n\r\n".getBytes());
        Thread.sleep(100);


        String requestHeaderResult = host.getHandler().getRequestHeader();
        byte[] requestBodyResult = host.getHandler().getRequestBody();
        String responseHeaderResult = host.getHandler().getResponseHeader();
        String responseBodyMessageResult = host.getHandler().getResponseBodyMessage();
        byte[] responseBodyResult = host.getHandler().getResponseBody();

        assertArrayEquals(requestImage.toByteArray(), requestBodyResult);
        assertTrue(requestHeaderResult.contains("Content-Type: image/jpeg"));

        assertTrue(responseHeaderResult.contains("HTTP/1.1 200 OK"));
        assertNull(responseBodyResult);
        assertTrue(responseBodyMessageResult.contains("<h2>POST Form</h2>"));
        assertTrue(responseBodyMessageResult.contains("<li>file name: BruslyDog.jpeg</li>"));
        assertTrue(responseBodyMessageResult.contains("<li>file size: 92990</li>"));
        assertTrue(responseBodyMessageResult.contains("<li>content type: application/octet-stream</li>"));
        assertTrue(responseHeaderResult.contains("HTTP/1.1 200 OK"));

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
