//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.*;
//import java.net.Socket;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class SocketHostTest {
//    private TestHelper helper;
//    private EchoHandler handler;
//    private SocketHost host;
//    private Socket socket;
//    private InputStream input;
//    private OutputStream output;
//    private BufferedReader reader;
//    private EchoHandlerFactory handlerFactory;
//    private BufferedInputStream buffedInput;
//    private RequestParser parser;
//
//    @BeforeEach
//    public void setup() {
//        helper = new TestHelper();
//        handlerFactory = new EchoHandlerFactory();
//        handler = handlerFactory.getHandler();
//        host = new SocketHost(3141, handlerFactory);
//        parser = new RequestParser();
//    }
//
//    @AfterEach
//    private void tearDown() throws Exception {
//        host.stop();
//        if (socket != null)
//            socket.close();
//    }
//
//    @Test
//    public void isConnected() throws Exception {
//        host.start();
//        helper.connect();
//        socket = helper.getSocket();
//        assertTrue(socket.isConnected());
//    }
//
//    @Test
//    public void port() {
//        assertEquals(3141, host.getPort());
//        assertEquals(handler, host.getHandler());
//    }
//
//    @Test
//    public void startStop() throws Exception {
//        assertFalse(host.isRunning());
//
//        host.start();
//        assertTrue(host.isRunning());
//
//        host.stop();
//        assertFalse(host.isRunning());
//    }
//
//    @Test
//    public void connectionWithOneEcho() throws Exception {
//        host.start();
//        helper.connect();
//        buffedInput = helper.getBuffedInput();
//        output = helper.getOutput();
//        RequestParser parser = new RequestParser();
//
//        output.write("hello world!\r\n".getBytes());
//        output.write("\r\n".getBytes());
//        buffedInput.read();
//
//        String msg = host.getHandler().getRequestHeader();
//
//        assertEquals("hello world!\r\n\r\n", msg);
//    }
//
//    @Test
//    public void connectionWithTwoEchos() throws Exception {
//        host.start();
//        helper.connect();
//        buffedInput = helper.getBuffedInput();
//        output = helper.getOutput();
//
//        output.write("hello world!\r\n".getBytes());
//        output.write("hello mars!\r\n".getBytes());
//        output.write("\r\n".getBytes());
//        buffedInput.read();
//
//        String msg = host.getHandler().getRequestHeader();
//        assertEquals("hello world!\r\nhello mars!\r\n\r\n", msg);
//    }
//
//    @Test
//    public void twoConnections() throws Exception {
//        host.start();
//        helper.connect();
//        helper.connect();
//        buffedInput = helper.getBuffedInput();
//        output = helper.getOutput();
//
//        output.write("hello world!\r\n".getBytes());
//        output.write("\r\n".getBytes());
//        buffedInput.read();
//
//        String msg = host.getHandler().getRequestHeader();
//
//        assertEquals("hello world!\r\n\r\n", msg);
//    }
//    @Test
//    public void echoWithByteBody() throws IOException {
//        host.start();
//        helper.connect();
//        buffedInput = helper.getBuffedInput();
//        output = helper.getOutput();
//
//        String header = ("hello world!\r\n" +
//                "Content-Length: 92990\r\n" +
//                "\r\n");
//        byte[] headerBytes = header.getBytes();
//        File file = new File("/Users/maniginam/server-task/http-server/BruslyDog.jpeg");
//        FileInputStream input = new FileInputStream(file);
//        byte[] image = input.readAllBytes();
//        ByteArrayOutputStream outputImg = new ByteArrayOutputStream();
//
//        byte[] fullRequest = new byte[headerBytes.length + image.length];
//        System.arraycopy(headerBytes, 0, fullRequest, 0, headerBytes.length);
//        System.arraycopy(image, 0, fullRequest, headerBytes.length, image.length);
//
//        outputImg.write(image);
//        output.write(fullRequest);
//        buffedInput.read();
//
//        int bodySize = handler.getBodySize();
//
//        assertEquals(92990, bodySize);
//        assertEquals(header, host.getHandler().getRequestHeader());
//        assertArrayEquals(outputImg.toByteArray(), host.getHandler().getRequestBody());
//    }
//
//    @Test
//    public void cleanClose() throws Exception {
//        host.start();
//        helper.connect();
//        host.stop();
//
//        assertFalse(host.getConnectionThread().isAlive());
//
//        List<Delegator> delegators = host.getDelegators();
//        for (Delegator delegator : delegators) {
//            assertFalse(delegator.getThread().isAlive());
//        }
//    }
//
//}
