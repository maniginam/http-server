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
        byte[] response;
        String contentLengthString;
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            while (host.isRunning() && socket.isConnected()) {
                if (input.available() > 0) {
                    //FIX THIS AS THIS WAS MENTIONED COULD POSE ISSUES
                    String line = reader.readLine();
                    try {
                        response = host.getHandler().handle(line);
                    } catch (ExceptionInfo e) {
                        response = e.getMessage().getBytes();
                    }
                    send(response);
                    output.flush();

                } else {
                    Thread.sleep(1);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        host.getDelegators().remove(this);
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
