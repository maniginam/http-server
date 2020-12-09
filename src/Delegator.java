import java.io.*;
import java.net.Socket;

public class Delegator implements Runnable {
    private final SocketHost host;
    private final Socket socket;
    private Thread thread;
    private final OutputStream output;

    public Delegator(SocketHost host, Socket socket) throws IOException {
        this.host = host;
        this.socket = socket;
        output = socket.getOutputStream();
    }

    @Override
    public void run() {
        String header = "";
        byte[] body;
        byte[] response;
        ByteArrayOutputStream outputHeader;
        ByteArrayOutputStream outputHeader2;
        ByteArrayOutputStream outputBody;

        try {
            InputStream input = socket.getInputStream();
            BufferedInputStream buffedInput = new BufferedInputStream(input);

            while (host.isRunning() && socket.isConnected()) {
                int bodySize = -1;
                body = null;
                if (input.available() > 0) {
                    outputHeader = new ByteArrayOutputStream();
                    while (bodySize == -1) {
                        int b = buffedInput.read();
                        outputHeader.write(b);
                        host.getHandler().handleHeader(outputHeader.toByteArray());
                        header = host.getHandler().getRequestHeader();
                        bodySize = host.getHandler().getBodySize();
                        if (bodySize > 0) {
                            outputBody = new ByteArrayOutputStream();
                            outputBody.write(buffedInput.readNBytes(bodySize));
                            body = outputBody.toByteArray();
                        } else { body = null; }
                }

                try {
                    response = host.getHandler().handle(header, body);
                } catch (ExceptionInfo e) {
                    response = e.getMessage().getBytes();
                }

                if (response != null) {
                    send(response);
                    output.flush();
                }

            } else{
                Thread.sleep(1);
            }
        }
    } catch(IOException |
    InterruptedException e)

    {
        e.printStackTrace();
    }
        host.getDelegators().

    remove(this);

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
