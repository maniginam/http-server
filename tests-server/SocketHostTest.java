import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SocketHostTest {
    private TestHelper helper;
    private EchoHandler handler;
    private SocketHost host;
    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private BufferedReader reader;
    private EchoHandlerFactory handlerFactory;
    private BufferedInputStream buffedInput;
    private RequestParser parser;

    @BeforeEach
    public void setup() {
        helper = new TestHelper();
        handlerFactory = new EchoHandlerFactory();
        handler = handlerFactory.getHandler();
        host = new SocketHost(3141, handlerFactory);
        parser = new RequestParser();
    }

    @AfterEach
    private void tearDown() throws Exception {
        host.stop();
        if (socket != null)
            socket.close();
    }

    @Test
    public void isConnected() throws Exception {
        host.start();
        helper.connect();
        socket = helper.getSocket();
        assertTrue(socket.isConnected());
    }

    @Test
    public void port() {
        assertEquals(3141, host.getPort());
        assertEquals(handler, host.getHandler());
    }

    @Test
    public void startStop() throws Exception {
        assertFalse(host.isRunning());

        host.start();
        assertTrue(host.isRunning());

        host.stop();
        assertFalse(host.isRunning());
    }

    @Test
    public void connectionWithOneEcho() throws Exception {
        host.start();
        helper.connect();
        buffedInput = helper.getBuffedInput();
        output = helper.getOutput();

        output.write("hello world!\r\n".getBytes());
        output.write("\r\n".getBytes());
        buffedInput.read();

        String msg = host.getHandler().getRequestHeader();

        assertEquals("hello world!\r\n\r\n", msg);
    }

    @Test
    public void connectionWithTwoEchos() throws Exception {
        host.start();
        helper.connect();
        buffedInput = helper.getBuffedInput();
        output = helper.getOutput();

        output.write("hello world!\r\n".getBytes());
        output.write("hello mars!\r\n".getBytes());
        output.write("\r\n".getBytes());
        buffedInput.read();

        String msg = host.getHandler().getRequestHeader();
        assertEquals("hello world!\r\nhello mars!\r\n\r\n", msg);
    }

    @Test
    public void twoConnections() throws Exception {
        host.start();
        helper.connect();
        helper.connect();
        buffedInput = helper.getBuffedInput();
        output = helper.getOutput();

        output.write("hello world!\r\n".getBytes());
        output.write("\r\n".getBytes());
        buffedInput.read();

        String msg = host.getHandler().getRequestHeader();

        assertEquals("hello world!\r\n\r\n", msg);
    }
    @Test
    public void echoWithByteBody() throws IOException {
        host.start();
        helper.connect();
        buffedInput = helper.getBuffedInput();
        output = helper.getOutput();

        String header = ("hello world!\r\n" +
                "Content-Length: 92990\r\n" +
                "\r\n");
        File file = new File("/Users/maniginam/server-task/http-server/BruslyDog.jpeg");
        FileInputStream input = new FileInputStream(file);
        byte[] image = input.readAllBytes();
        ByteArrayOutputStream outputImg = new ByteArrayOutputStream();

        outputImg.write(image);
        output.write((header).getBytes());
        output.write(image);
        buffedInput.read();

        int bodySize = handler.getBodySize();

        assertEquals(92990, bodySize);
        assertEquals(header, host.getHandler().getRequestHeader());
        assertArrayEquals(outputImg.toByteArray(), host.getHandler().getBody());
    }

    @Test
    public void cleanClose() throws Exception {
        host.start();
        helper.connect();
        host.stop();

        assertFalse(host.getConnectionThread().isAlive());

        List<Delegator> delegators = host.getDelegators();
        for (Delegator delegator : delegators) {
            assertFalse(delegator.getThread().isAlive());
        }
    }

//    @Test
//    public void POSTrequest() throws IOException {
//        host.start();
//        helper.connect();
//        buffedInput = helper.getBuffedInput();
//        output = helper.getOutput();
//
//        File file = new File("/Users/maniginam/server-task/http-server/BruslyDog.jpeg");
//        FileInputStream input = new FileInputStream(file);
//        byte[] image = input.readAllBytes();
//        ByteArrayOutputStream requestImage = new ByteArrayOutputStream();
//
//        String requestHeader1 = "POST /form HTTP/1.1\r\n" +
//                "Name: file\r\n" +
//                "Content-Type: multipart/form-data; boundary=----Rex&Leo\r\n" +
//                "Content: " + file + "\r\n" +
//                "Content-Length: " + image.length;
//
//        String requestHeader2 = requestHeader1 + "\r\n------Rex&Leo\r\n" +
//                "Content-Disposition: form-data; name=\"file\"; filename=\"BruslyDog.jpeg\"\r\n" +
//                "Content-Type: image/jpeg";
//
//        output.write((requestHeader2 + "\r\n\r\n").getBytes());
//        output.write(image);
//        buffedInput.read();
//
//        String requestHeaderResult = host.getHandler().getRequestHeader();
//        byte[] requestBodyResult = host.getHandler().getBody();
//        String responseBodyMsgResult = host.getHandler().getResponseBody();
//
//
//        assertEquals(requestHeader2, requestHeaderResult);
//        assertTrue(requestHeaderResult.contains("Content-Type: image/jpeg"));
//        assertTrue(response.contains("<h2>POST Form</h2>"));
//        assertTrue(response.contains("<li>file name: BruslyDog.jpeg</li>"));
//        assertTrue(response.contains("<li>file size: 92990</li>"));
//        assertTrue(response.contains("<li>content type: application/octet-stream</li>"));v
//
//    }
}
