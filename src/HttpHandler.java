import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HttpHandler implements Handler {
    private String root;
    private int port;
    private HttpServer server;
    private int bodySize;
    private RequestParser parser;
    private String header;
    private byte[] requestBody;
    private String responseHeader;
    private byte[] responseBody;
    private String responseBodyMessage;

    public HttpHandler(int port, String root) throws IOException {
        this.port = port;
        this.root = root;
        server = new HttpServer(root);
        parser = new RequestParser();
    }

    @Override
    public void handleHeader(byte[] input) {
        responseHeader = null;
        responseBody = null;
        responseBodyMessage = null;
        parser.interpretHeader(input);
        header = parser.getHeader();
        bodySize = parser.getBodySize();
    }

    @Override
    public byte[] handle(String header, byte[] body) throws ExceptionInfo, IOException {
        responseHeader = null;
        responseBody = null;
        responseBodyMessage = null;
        setRequestBody(body);
        server.submitRequest(header, body);
        responseHeader = server.getResponseHeader();
        responseBodyMessage = server.getResponseBodyMessage();
        responseBody = server.getResponseBodyBytes();

        ByteArrayOutputStream response = new ByteArrayOutputStream();
        response.write(responseHeader.getBytes());
        if (responseBodyMessage != null)
            response.write(responseBodyMessage.getBytes());
        if (responseBody != null)
            response.write(responseBody);

        response.close();
        return response.toByteArray();
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
    public byte[] getRequestBody() {
        return requestBody;
    }

    @Override
    public String getResponseHeader() {
        return responseHeader;
    }

    @Override
    public byte[] getResponseBody() {
        return responseBody;
    }

    @Override
    public String getResponseBodyMessage() {
        return responseBodyMessage;
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
