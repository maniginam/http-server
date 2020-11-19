import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscribers;
import java.util.concurrent.Executor;

public class HttpHost {
    private final int port;
    private final HttpHandler handler;
    private boolean isConnected;
    private HttpServer httpServer;
    private URI address;
    private Executor executor;
    private MyHttpServer server;
    private HttpRequest request;
    private Exchange exchange;
    private Response response;


    public HttpHost(int port, HttpHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    public void start() throws IOException {
        address = URI.create("http://localhost:8080");
        server = new MyHttpServer(address);

    }

    public Response getURI() throws IOException {
        exchange = new Exchange();
        handler.handle(exchange);
        return response;
    }

    public int getPort() {
        return port;
    }

    public HttpHandler getHandler() {
        return handler;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
