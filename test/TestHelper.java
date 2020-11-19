import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class TestHelper {
    public EchoHandler handler;
    private Socket socket;
    private OutputStream output;
    private BufferedReader reader;

    public TestHelper() {
        handler = new EchoHandler();

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

class EchoHandler implements Handler {

    public boolean initWasCalled;

    public String initMessage;

    @Override
    public String handle(String message) {
        return message;
    }

    @Override
    public String init() {
        initWasCalled = true;
        return initMessage;
    }

}
