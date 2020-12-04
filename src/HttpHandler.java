import java.io.IOException;

public class HttpHandler implements Handler {
    private String root;
    private int port;
    private HttpServer server;
    private int bodySize;
    private RequestParser parser;
    private String header;
    private byte[] requestBody;

    public HttpHandler(int port, String root) throws IOException {
        this.port = port;
        this.root = root;
        server = new HttpServer(root);
        parser = new RequestParser();
    }

    @Override
    public void handleHeader(byte[] input) {
        parser.interpretHeader(input);
        header = parser.getHeader();
        bodySize = parser.getBodySize();
    }

    @Override
    public byte[] handle(String header, byte[] body) throws ExceptionInfo, IOException {
        setRequestBody(body);
        server.submitRequest(header, body);
            return server.getResponse();
    }

    @Override
    public String getRequestHeader() {
        return header;
    }

    @Override
    public void setRequestBody(byte[] body) {
        requestBody = body;
    }

    @Override
    public byte[] getBody() {
        return requestBody;
    }


    @Override
    public String getRoot() {
        return root;
    }

    @Override
    public int getBodySize() {
        return bodySize;
    }

    public HttpServer getServer() {
        return server;
    }
}
