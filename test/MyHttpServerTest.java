import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static java.net.http.HttpResponse.*;
import static org.junit.jupiter.api.Assertions.*;

public class MyHttpServerTest {
    private TestHelper helper;
    private HttpHandler handler;
    private HttpHost host;
    private HttpClient client;
    private Socket socket;
    private OutputStream output;
    private BufferedReader reader;
    private InetSocketAddress address;
    private HttpServer server;
    private WebSocket websocket;
    private HttpRequest.Builder request;
    private BodyHandlers bodyHandler;
    private Listener listener;

    @BeforeEach
    public void setup() {
        helper = new TestHelper();
        handler = new Manager();
        host = new HttpHost(8080, handler);
        address = InetSocketAddress.createUnresolved("localhost", 8080);
        WebSocket.Listener listener = new WebSocket.Listener() {};
    }

    @AfterEach
    public void tearDown() {

    }

    private void connect() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080");
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder(uri);
//        BodyHandler<String> bodyHandler = BodyHandlers.ofString();
//        HttpResponse response = client.send((HttpRequest) request, bodyHandler);


//        server = HttpServer.create(address, 25);
    }

    @Test
    public void port() {
        assertEquals(8080, host.getPort());
        assertEquals(handler, host.getHandler());
    }

    @Test
    public void requestRespond() throws IOException, InterruptedException {
        host.start();
        connect();

        assertEquals("http://localhost:8080", host.getURI());
    }

}
