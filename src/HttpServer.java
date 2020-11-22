import static java.lang.Integer.parseInt;

public class HttpCommandLine implements CommandLine {
    private String header;
    private String portLine;
    private int port;
    private String rootLine;
    private String root;
    private String configMessage;
    public String message;
    public boolean usage;
    public boolean config;
    public String invalidArg;

    public HttpCommandLine() {
        port = 80;
        root = "/Users/maniginam/server-task/http-server";
        portLine = "Running on port: ";
        rootLine = "Serving files from: ";
        setHeader("Gina's HTTP Server");
        usage = false;
        config = false;
        invalidArg = null;
        message = null;
    }

    public void commandLineMessage() {
        if (usage) {
            getUsageMessage();
        } else if (config) {
            getConfigMessage();
        } else {
            getInvalidArgMessage();
        }
    }

    private void getInvalidArgMessage() {
        message = "<h3>Invalid option: " + invalidArg + "</h3>";
    }

    public void manageArgs(String msg) {
        int newPort = -1;
        String[] args = msg.split("[, ] ");
        String[] newArgs = new String[5];
        for (String arg : args) {
            if (arg == "-h")
                newArgs[0] = arg;
            else if (arg == "-x")
                newArgs[1] = arg;
            else
                newArgs[2] = arg;

            int argLength = arg.length();
            try {
                newPort = parseInt(arg);
                config = true;
                if (newPort != -1)
                    updatePort(newPort);
            } catch (Exception e) {
                if (arg.startsWith("/")) {
                    updateRoot(arg);
                    config = true;
                }
            }
        }
        for (String arg : newArgs) {
            if (arg == "-h")
                usage = true;
            else if (arg == "-x")
                config = true;
            else {
                if (arg != null)
                    invalidArg = arg;
            }
        }
//        int size = args.length;
//        for (int i = 0; i < size; i++) {
//            System.out.println("i = " + i);
//            System.out.println("args[i] = " + args[i]);
//            if (args[i] == "-h") {
//                usage = true;
//                getUsage();
//            } else if (args[i] == "-x") {
//                config = true;
//                getConfigMessage();
//            } else if (args[i] == "-p") {
//                i++;
//                System.out.println("got p");
//                port = parseInt(args[i]);
//                System.out.println("portLine = " + portLine);
//                portLine = getPortLine();
//                getConfigMessage();
//                i++;
//            } else if (args[i] == "-r") {
//                i++;
//                root = args[i];
//                rootLine = getRootLine();
//                getConfigMessage();
//            } else {
//                invalidArg = args[i];
//                getInvalidArgMessage();
//            }
//        }
    }

    @Override
    public boolean getUsage() {
        return usage;
    }

    @Override
    public boolean getConfig() {
        return config;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void getUsageMessage() {
        String p = "<h3>  -p     Specify the port.  Default is 80. \r\n";
        String r = "  -r     Specify the root directory.  Default is the current working directory. \r\n";
        String h = "  -h     Print this help message \r\n";
        String x = "  -x     Print the startup configuration without starting the server</h3>\r\n";
        message = p + r + h + x;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getConfigMessage() {
        message = getHeader() + getPortLine() + getRootLine();
        return message;
    }

    @Override
    public void submitEntry(String msg) {

    }

    @Override
    public void submitArg(String arg) {

    }

    public void updateRoot(String newRoot) {
        root = newRoot;
    }

    public void updatePort(int newPort) {
        port = newPort;
    }

    public String getHeader() {
        return "<h1>" + header + "<br>";
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPortLine() {
        return "<small><small><small>" + portLine + port + "<br>";
    }

    public String getRootLine() {
        return rootLine + root + "</small></small></small></h1>\r\n";
    }
}
