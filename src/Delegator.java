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
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            while (host.isRunning() && socket.isConnected()) {
                if (input.available() > 0) {
                    //FIX THIS AS THIS WAS MENTIONED COULD POSE ISSUES
                    String line = reader.readLine();
                    String response = host.getHandler().handle(line);

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
        String msg = host.getHandler().init();
        if (msg != null)
            send(msg);
        thread = new Thread(this);
        thread.start();
    }

    public void stop() throws InterruptedException {
        if (thread != null)
            thread.join();
    }

    private void send(String response) throws IOException {
        output.write((response + "\n").getBytes());
    }

    public Thread getThread() {
        return thread;
    }
}
