import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class MultipleRequestsTest {
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
    public void twoPings() throws IOException {
        host.start();
        helper.connect();
        helper.connect();
        OutputStream output = helper.getOutput();
        BufferedInputStream buffedInput = helper.getBuffedInput();

        String request1 = "GET /ping HTTP/1.1\r\n\r\n";
        String request2 = "GET /ping/1 HTTP/1.1\r\n\r\n";

        output.write(request1.getBytes());
        buffedInput.read();
        String header1Result = host.getHandler().getResponseHeader();
        String body1Result = host.getHandler().getResponseBodyMessage();

        output.write(request2.getBytes());
        buffedInput.read();

        String header2Result = host.getHandler().getResponseHeader();
        String body2Result = host.getHandler().getResponseBodyMessage();

        assertTrue(header1Result.contains("HTTP/1.1 200 OK"));
        assertTrue(body1Result.contains("<h2>Ping</h2>"));

        assertTrue(header2Result.contains("HTTP/1.1 200 OK"));
        assertTrue(body2Result.contains("<h2>Ping</h2>"));
    }

    @Test
    public void listingThenPing() throws IOException, InterruptedException {
        host.start();
        helper.connect();
        helper.connect();
        OutputStream output1 = helper.getOutput();
        OutputStream output2 = helper.getOutput();
        BufferedInputStream buffedInput = helper.getBuffedInput();

        String request1 = "GET /listing HTTP/1.1\r\n\r\n";
        String request2 = "GET /ping HTTP/1.1\r\n\r\n";

        output1.write(request1.getBytes());
        buffedInput.read();

        String requestHeader1 = host.getHandler().getRequestHeader();
        String header1Result = host.getHandler().getResponseHeader();
        String body1Result = host.getHandler().getResponseBodyMessage();

        output2.write(request2.getBytes());
        Thread.sleep(100);

        String requestHeader2 = host.getHandler().getRequestHeader();
        String header2Result = host.getHandler().getResponseHeader();
        String body2Result = host.getHandler().getResponseBodyMessage();

        assertEquals(request1, requestHeader1);
        assertTrue(header1Result.contains("HTTP/1.1 200 OK"));
        assertTrue(body1Result.contains("<li><a href=\"/forms.html\">forms.html</a></li>"));
        assertEquals(request2, requestHeader2);
        assertTrue(header2Result.contains("HTTP/1.1 200 OK"));
        assertTrue(body2Result.contains("<h2>Ping</h2>"));
    }


    @Test
    public void onePingOneImgRequest() throws IOException, InterruptedException {
        host.start();
        helper.connect();
        helper.connect();
        OutputStream output1 = helper.getOutput();
        OutputStream output2 = helper.getOutput();
        BufferedInputStream buffedInput = helper.getBuffedInput();

        File file = new File("/Users/maniginam/server-task/http-spec/testroot/img/BruslyDog.jpeg");
        FileInputStream input = new FileInputStream(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(input.readAllBytes());
        byte[] imageArray = outputStream.toByteArray();

        String request1 = "GET /ping HTTP/1.1\r\n\r\n";
        String request2 = "GET /img/BruslyDog.jpeg HTTP/1.1\r\n\r\n";

        output1.write(request1.getBytes());
        buffedInput.read();

        String header1Result = host.getHandler().getResponseHeader();
        String bodyMsg1Result = host.getHandler().getResponseBodyMessage();

        output2.write(request2.getBytes());
        Thread.sleep(100);

        String header2Result = host.getHandler().getResponseHeader();
        String bodyMsg2Result = host.getHandler().getResponseBodyMessage();
        byte[] body2Result = host.getHandler().getResponseBody();

        assertTrue(header1Result.contains("HTTP/1.1 200 OK"));
        assertTrue(bodyMsg1Result.contains("<h2>Ping</h2>"));

        assertTrue(header2Result.contains("HTTP/1.1 200 OK"));
        assertNull(bodyMsg2Result);
        assertArrayEquals(imageArray, body2Result);
    }


}
