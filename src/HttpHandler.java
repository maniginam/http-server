import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class HttpHandler implements Handler {
    private String root;
    private int port;
    private HttpServer server;
    private int bodySize = -1;
    private RequestParser parser;
    private String header;
    private byte[] requestBody;
    private String responseHeader;
    private byte[] responseBody;
    private String responseBodyMessage;
    private byte[] finalResponse;

    public HttpHandler(int port, String root) throws IOException {
        this.port = port;
        this.root = root;
        server = new HttpServer(root);
        parser = new RequestParser();
    }

    @Override
    public void handleHeader(byte[] input) throws IOException {
        responseHeader = null;
        responseBody = null;
        responseBodyMessage = null;
        parser.interpretHeader(input);
        header = parser.getHeader();
//        ****************TEST IS GETITNG A -1 BODYSIZE HERE
        bodySize = parser.getContentLength();
    }

    @Override
    public byte[] handle(Map<String, String> requestHeader, byte[] requestBody) throws ExceptionInfo, IOException {
        bodySize = -1;
        setRequestBody(requestBody);
        server.submitRequest(requestHeader, requestBody);
        responseHeader = server.getResponseHeader();
        responseBodyMessage = server.getResponseBodyMessage();
        responseBody = server.getResponseBodyBytes();

        ByteArrayOutputStream response = new ByteArrayOutputStream();
        if (responseHeader != null)
            response.write(responseHeader.getBytes());
        if (responseBodyMessage != null)
            response.write(responseBodyMessage.getBytes());
        if (responseBody != null)
            response.write(responseBody);

        finalResponse = response.toByteArray();
        response.close();
        return finalResponse;
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

    public byte[] getResponse() {
        return finalResponse;
    }
}
