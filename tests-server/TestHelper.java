import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class TestHelper {
    public EchoHandler handler;
    public EchoHandlerFactory handlerFactory;
    private Socket socket;
    private OutputStream output;
    private BufferedReader reader;

    public TestHelper() {


    }

    public EchoHandler getEchoHandler() {
        return handler;
    }

    public void connect() throws IOException {
        socket = new Socket("localhost", 314);
        output = socket.getOutputStream();
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public OutputStream getOutput() {
        return output;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public Socket getSocket() {
        return socket;
    }
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

    public boolean initWasCalled;

    public String initMessage;
    public HttpResponder responder;
    public byte[] response;

    @Override
    public byte[] handle(String message) throws IOException {
        responder = new HttpResponder();
        response = responder.respond("", message.getBytes());
        return response;
    }

    public String init() {
        initWasCalled = true;
//        String[] lines = new String[0];
//        for(String line : lines) {
//            initMessage = initMessage + line;
//        }
        return initMessage;
    }

    @Override
    public String getRoot() {
        return null;
    }

}
