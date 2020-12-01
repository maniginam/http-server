import java.io.IOException;

public class HttpHandlerFactory implements HandlerFactory {
    private int port;
    private String root;
    private HttpHandler handler;

    public HttpHandlerFactory(int port, String root) throws IOException {
        this.port = port;
        this.root = root;
        handler = new HttpHandler(port, root);
    }

    @Override
    public HttpHandler getHandler() {
        return handler;
    }
}
