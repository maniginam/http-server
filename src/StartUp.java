import java.io.IOException;

public class CommandLineHandler implements Handler {

    private int port;
    private String path;
    public static CommandLine commandLine;

    public CommandLineHandler(int port, String path) {
        this.port = port;
        this.path = path;
    }

    @Override
    public String handle(String msg) {
        commandLine.manageArgs(msg);
        commandLine.commandLineMessage();
        return commandLine.getMessage();
    }

    public CommandLine getCommandLine() {
        return commandLine;
    }

    @Override
    public String init() {
        commandLine = new LocalCommandLine(port, path);
        return commandLine.getConfigMessage();
    }

    @Override
    public void start(String[] args) throws IOException, InterruptedException {

    }

    @Override
    public void requireExit(String msg) {
        String[] args = msg.split("[, ] ");
        for (String arg : args) {
            boolean exit;
            if (arg == "-h")
                exit = true;
            else if (arg == "-x")
                exit = true;
            else
                exit = false;

            if (exit)
                System.exit(0);
        }
    }

    public static String getUsageMessage() {
        String p = "  -p     Specify the port.  Default is 80.\n";
        String r = "  -r     Specify the root directory.  Default is the current working directory.\n";
        String h = "  -h     Print this help message\n";
        String x = "  -x     Print the startup configuration without starting the server\n";
        String message = p + r + h + x;
        System.out.println(message);
        return message;
    }

    public static String getConfigMessage() {
        String name = "Example Server\r\n";
        String portLine = "Running on port: " + port + ".\r\n";
        String filesLine = "Serving files from: " + path;
        String message = name + portLine + filesLine;
        System.out.println(message);
        return message;
    }

    public static void main(String[] args) throws Exception {
        int port = 80;
        String path = "/Users/maniginam/server-task/http-spec";
        LocalCommandLine commandLine = new LocalCommandLine(port, path);
        Handler localHandler = new CommandLineHandler(port, path);
        SocketHost host = new SocketHost(port, localHandler);
        for (String arg : args) {
            if (arg == "-h") {
                getUsageMessage();
                System.exit(0);
            }
            else if (arg == "-x" && args.length == 1) {
                getConfigMessage();
                System.exit(0);
            }
//            else {
//                host.start();
//                host.getConnectionThread().join();
//            }
        }

//        for (String arg : args) {
//            if (arg == "-h" || arg == "-x") {
//                host.stop();
//                System.exit(0);
//            }
//        }
//
//        host.stop();
//        Handler httpHandler = new HttpHandler(port, path);
//        SocketHost httpHost = new SocketHost(port, httpHandler);
//        httpHost.start();
//        httpHost.getAcceptThread().join();
    }
}
