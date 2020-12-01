import static java.lang.Integer.parseInt;

public class StartUp {

    private String invalidArg;

    public String getInvalidArgMsg() {
        String message = "Invalid option: " + invalidArg;
        System.out.println(message);
        return message;
    }

    public void setInvalidArg(String invalidArg) {
        this.invalidArg = invalidArg;
    }

    public static String getUsageMessage() {
        String p = "  -p     Specify the port.  Default is 80.\n";
        String r = "  -r     Specify the root directory.  Default is the current working directory.\n";
        String h = "  -h     Print this help message\n";
        String x = "  -x     Print the startup configuration without starting the server\n";
        String message = p + r + h + x;
        return message;
    }

    public static String getConfigMessage(String[] args) {
        int port = getPort(args);
        String path = getPath(args);
        String invalidArg = null;
        String message;
        for (String arg : args) {
            if (!(arg.startsWith("-h") || arg.startsWith("-x") || arg.startsWith("-p") || arg.startsWith("-r") || arg != Integer.toString(port) || arg != path)) {
                invalidArg = arg;
            }
        }

        if (invalidArg != null)
            message = "Invalid option: " + invalidArg;
        else {
            String name = "Example Server\r\n";
            String portLine = "Running on port: " + port + ".\r\n";
            String filesLine = "Serving files from: " + path;
            message = name + portLine + filesLine;
        }
        return message;
    }

    public static int getPort(String[] args) {
        int port = 80;
        for (String arg : args) {
            try {
                port = parseInt(arg);
            } catch (NumberFormatException e) {
                //no port update
            }
        }
        return port;
    }

    public static String getPath(String[] args) {
        String path = "/Users/maniginam/server-task/http-spec";
        for (String arg : args) {
            if (arg.startsWith("/")) {
                path = arg;
            } else if (arg.length() > 4) {
                path = path + "/" + arg;
            }
        }
        return path;
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            String message = getConfigMessage(args);
            int port = getPort(args);
            String path = getPath(args);
            for (String arg : args) {
                if (arg.startsWith("-h")) {
                    System.out.println(getUsageMessage());
                    System.exit(0);
                } else if (arg.startsWith("-x")) {
                    System.out.println(message);
                    System.exit(0);
                } else {
                    System.out.println(message);
                    HttpHandlerFactory handlerFactory = new HttpHandlerFactory(port, path);
                    SocketHost host = new SocketHost(port, handlerFactory);
                    host.start();
                    host.getConnectionThread().join();
                }
            }
        } else {
            System.out.println(getConfigMessage(args));
            HttpHandlerFactory handlerFactory = new HttpHandlerFactory(80, "/Users/maniginam/server-task/http-spec");
            SocketHost host = new SocketHost(80, handlerFactory);
            host.start();
            host.getConnectionThread().join();
        }
    }
}