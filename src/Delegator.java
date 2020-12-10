import java.io.*;
import java.net.Socket;
import java.util.Map;

public class Delegator implements Runnable {
    private final SocketHost host;
    private final Socket socket;
    private Thread thread;
    private final OutputStream output;
    private RequestParser parser;

    public Delegator(SocketHost host, Socket socket) throws IOException {
        this.host = host;
        this.socket = socket;
        output = socket.getOutputStream();
    }

    @Override
    public void run() {
        byte[] response;
        ByteArrayOutputStream outputHeader;

        try {
            InputStream input = socket.getInputStream();
            BufferedInputStream buffedInput = new BufferedInputStream(input);

            while (host.isRunning() && socket.isConnected()) {
                if (buffedInput.available() > 0) {
                    parser = new RequestParser();
                    outputHeader = new ByteArrayOutputStream();
                    int contentLength = extractContentLength(outputHeader, buffedInput);
                    if (contentLength > 0) {
                        extractBody(buffedInput, contentLength);
                    }
                    response = respond();

                    if (response != null) {
                        send(response);
                        output.flush();
                    }

                } else {
                    Thread.sleep(1);
                }
            }
        } catch (IOException |
                InterruptedException e) {
            e.printStackTrace();
        }
        host.getDelegators().

                remove(this);

    }

    private byte[] respond() throws IOException {
        byte[] response;
        try {
            Map<String, String> header = parser.getHeaderMap();
            byte[] body = parser.getBody();
            response = host.getHandler().handle(header, body);
        } catch (ExceptionInfo e) {
            response = e.getMessage().getBytes();
        }
        return response;
    }

    private void extractBody(BufferedInputStream buffedInput, int contentLength) throws IOException {
        ByteArrayOutputStream outputBody;
        byte[] body;
        outputBody = new ByteArrayOutputStream();
        outputBody.write(buffedInput.readNBytes(contentLength));
        body = outputBody.toByteArray();
        parser.interpretBody(body);
        outputBody.flush();
    }

    private int extractContentLength(ByteArrayOutputStream outputHeader, BufferedInputStream buffedInput) throws IOException {
        int contentLength = -1;
        while (contentLength == -1) {
            int b = buffedInput.read();
            outputHeader.write(b);
            parser.interpretHeader(outputHeader.toByteArray());
            contentLength = parser.getContentLength();
        }
        return contentLength;
    }

    public void start() throws IOException {
        thread = new Thread(this);
        thread.start();
    }

    public void stop() throws InterruptedException {
        if (thread != null)
            thread.join();
    }

    private void send(byte[] response) throws IOException {
        output.write(response);
    }

    public Thread getThread() {
        return thread;
    }
}
