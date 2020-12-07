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
        String header;
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
                int requests = 0;
                if (input.available() > 0) {
                    outputHeader = new ByteArrayOutputStream();
                    outputBody = new ByteArrayOutputStream();
                    while (bodySize == -1) {
                        int b = buffedInput.read();
                        outputHeader.write(b);
                        host.getHandler().handleHeader(outputHeader.toByteArray());
                        bodySize = host.getHandler().getBodySize();
                        header = host.getHandler().getRequestHeader();
                        if (bodySize > 0) {
                            outputBody.write(buffedInput.readNBytes(bodySize));
                            body = outputBody.toByteArray();
                        } else {
                        body = null;
                    }
                }
                header = host.getHandler().getRequestHeader();
                    System.out.println("header = " + header);

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
