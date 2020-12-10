import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

public class SocketHost {
    private final Handler handler;
    private final int port;
    private final HandlerFactory factory;
    private ServerSocket server;
    private boolean running;
    private Thread connectionThread;
    private List<Delegator> delegators;


    public SocketHost(int port, HandlerFactory factory) {
        this.port = port;
        this.factory = factory;
        handler = factory.getHandler();
        running = false;
        delegators = new LinkedList<Delegator>();
    }

    public int getPort() {
        return port;
    }

    public Handler getHandler() {
        return handler;
    }

    public void start() throws IOException {
        server = new ServerSocket(port);
        Runnable accepter = new Runnable() {
            @Override
            public void run() {
                try {
                    acceptConnections();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        running = true;
        connectionThread = new Thread(accepter);
        connectionThread.start();
    }

    private void acceptConnections() throws Exception {
        while (running) {
            try {
                Socket socket = server.accept();
                Delegator delegator = new Delegator(this, socket);
                delegators.add(delegator);
                delegator.start();
            } catch (SocketException e) {
                // closed socket service while waiting for connection
            }
        }
    }

    public void stop() throws Exception {
        if (running) {
            server.close();
            running = false;
            for (Delegator delegator : delegators) {
                delegator.stop();
            }
            connectionThread.join();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public List<Delegator> getDelegators() {
        return delegators;
    }

    public Thread getConnectionThread() {
        return connectionThread;
    }
}
