import java.io.IOException;

import static java.lang.Integer.parseInt;

public class HTTPHandlerOld implements Handler {
    private static int port;
    private static String path;

    public HttpCommandLineOld httpServer = new HttpCommandLineOld(port, path);
    public static boolean connected = false;

    public HTTPHandlerOld(int port, String path) {
        this.port = port;
        this.path = path;
    }

    @Override
    public String handle(String msg) {
        httpServer.submitArg(msg);
        return "<h1>" + httpServer.getMessage() + "</h1>\r\n";
    }

    @Override
    public String init() {
        return "HTTP/1.1 200 OK\r\n" +
                "\r\n" +
                "<h1>" + httpServer.getMessage() + "</h1>\r\n";
    }

    public boolean isServerConnected() {
        return connected;
    }

    public HttpCommandLineOld getServer() {
        return httpServer;
    }

    public void start(String[] args) throws IOException, InterruptedException {
        SocketHost host = new SocketHost(port, this);
        host.start();
        host.getAcceptThread().join();
        connected = host.isRunning();
        int newPort = -1;
        httpServer.manageArgs(args);
        for (String arg : args) {
            int argLength = arg.length();
            try {
                newPort = parseInt(arg);
                if (newPort != -1)
                    httpServer.updatePort(newPort);
            } catch (Exception e) {
                // no update
            }
            if (argLength > 4) {
                httpServer.updateRoot(arg);
            }
        }
        for (String arg : args) {
            if (arg == "-x" || arg == "-h") {
                handle(arg);
            }
        }

    }
}



