import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SocketHostTest {
    private TestHelper helper;
    private EchoHandler handler;
    private SocketHost host;
    private Socket socket;
    private OutputStream output;
    private BufferedReader reader;

    @BeforeEach
    public void setup() {
        helper = new TestHelper();
        handler = helper.getEchoHandler();
        host = new SocketHost(314, handler);
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
        assertEquals(314, host.getPort());
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

        output = helper.getOutput();
        reader = helper.getReader();
        output.write("hello world!\n".getBytes());
        assertEquals("hello world!", reader.readLine());
    }

    @Test
    public void connectionWithTwoEchos() throws Exception {
        host.start();
        helper.connect();
        output = helper.getOutput();
        reader = helper.getReader();

        output.write("hello world!\n".getBytes());
        assertEquals("hello world!", reader.readLine());

        output.write("hello mars!\n".getBytes());
        assertEquals("hello mars!", reader.readLine());
    }

    @Test
    public void twoConnections() throws Exception {
        host.start();
        helper.connect();
        helper.connect();
        output = helper.getOutput();
        reader = helper.getReader();

        output.write("hello world!\n".getBytes());
        assertEquals("hello world!", reader.readLine());
    }

    @Test
    public void initCalledOnConnect() throws Exception {
        host.start();
        helper.connect();

        Thread.sleep(100);
        assertTrue(handler.initWasCalled);
    }

    @Test
    public void initMessageSent() throws Exception {
        host.start();
        handler.initMessage = "hello world!";
        helper.connect();
        reader = helper.getReader();

        assertEquals("hello world!", reader.readLine());
    }

    @Test
    public void cleanClose() throws Exception {
        host.start();
        helper.connect();
        host.stop();

        assertFalse(host.getConnectionThread().isAlive());

        List<Delegator> delegators = host.getDelegators();
        for(Delegator delegator : delegators) {
            assertFalse(delegator.getThread().isAlive());
        }
    }
}
