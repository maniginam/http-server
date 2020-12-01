import java.io.IOException;
import java.util.Date;

public class HttpHandler implements Handler {
    private String root;
    private int port;
    private HttpServer server;
    private Date date;

    public HttpHandler(int port, String root) throws IOException {
        this.port = port;
        this.root = root;
        server = new HttpServer(root);
    }

    @Override
    public byte[] handle(String msg) throws ExceptionInfo, IOException {
            server.submitRequest(msg);
            return server.getResponse();
    }


    @Override
    public String getRoot() {
        return root;
    }

    public HttpServer getServer() {
        return server;
    }
}
