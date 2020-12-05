import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TestHelper {
    public EchoHandler handler;
    public EchoHandlerFactory handlerFactory;
    private Socket socket;
    private OutputStream output;
    private BufferedReader reader;
    private InputStream input;
    private BufferedInputStream buffedInput;
    private ByteArrayOutputStream outBytes;

    public TestHelper() {


    }

    public EchoHandler getEchoHandler() {
        return handler;
    }

    public void connect() throws IOException {
        socket = new Socket("localhost", 3141);
        input = socket.getInputStream();
        buffedInput = new BufferedInputStream(input);
        output = socket.getOutputStream();
        outBytes = new ByteArrayOutputStream();
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public InputStream getInput() {
        return input;
    }
    public OutputStream getOutput() {
        return output;
    }
    public Socket getSocket() {
        return socket;
    }
    public BufferedInputStream getBuffedInput() { return buffedInput; }
    public ByteArrayOutputStream getOutBytes() { return outBytes; }

}

class EchoHandlerFactory implements HandlerFactory {

    private final EchoHandler handler;

    public EchoHandlerFactory() {
        handler = new EchoHandler();
    }

    @Override
    public EchoHandler getHandler() {
        return handler;
    }
}

class EchoHandler implements Handler {
    public byte[] response;
    private int bodySize = -1;
    private RequestParser parser;
    private String header;
    private byte[] body;
    private String responseHeader;
    private byte[] responseBody;
    private String responseBodyMessage;

    @Override
    public void handleHeader(byte[] input) {
        parser = new RequestParser();
        parser.interpretHeader(input);
        bodySize = parser.getBodySize();
        header = parser.getHeader();
    }

    @Override
    public String getRequestHeader() {
        return header;
    }

    @Override
    public byte[] getRequestBody() {
        return body;
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
    public byte[] handle(String header, byte[] body) throws IOException {
        setRequestBody(body);
        response = header.getBytes();
        return response;
    }

    public void setRequestBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String getRoot() {
        return null;
    }

    @Override
    public int getBodySize() {
        return bodySize;
    }

}
