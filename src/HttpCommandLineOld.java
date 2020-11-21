import static java.lang.Integer.parseInt;

public class HttpCommandLineOld implements CommandLine {

    private int port;
    private String path;
    private String message;

    public HttpCommandLineOld(int port, String path) {
        this.port = port;
        this.path = path;
        String name = "Gina's HTTP Server\r\n";
        String portLine = "Running on port: " + port + "\r\n";
        String filesLine = "Serving files from: " + path + "\r\n";
        message = name + portLine + filesLine;
    }

    public String getMessage() {
        return message;
    }

    public void getUsage() {
        String p = "  -p     Specify the port.  Default is 80. \r\n";
        String r = "  -r     Specify the root directory.  Default is the current working directory. \r\n";
        String h = "  -h     Print this help message \r\n";
        String x = "  -x     Print the startup configuration without starting the server\r\n";
        message = p + r + h + x;
    }

    public void getConfigMessage() {
        String name = "Gina's HTTP Server\r\n";
        String portLine = "Running on port: " + port + "\r\n";
        String filesLine = "Serving files from: " + path + "\r\n";
        message = name + portLine + filesLine;
    }

    public void submitEntry(String msg) {
        if (msg == "-h") {
            getUsage();
        }
    }

    @Override
    public String submitArg(String arg) {
        if (arg == "-h") {
            getUsage();
            System.out.println(getMessage());
            return getMessage();
        } else if (arg == "-x") {
            getConfigMessage();
            System.out.println(getMessage());
            return getMessage();
        } else {
            message = "Invalid option: " + arg;
            System.out.println(getMessage());
            return getMessage();
        }
    }

    public void updatePort(int newPort) {
        port = newPort;
    }

    public void updateRoot(String newPath) {
        path = newPath;
    }

    @Override
    public void manageArgs(String[] args) {
        int newPort = -1;
        for (String arg : args) {
            int argLength = arg.length();
            try {
                newPort = parseInt(arg);
                if (newPort != -1)
                    updatePort(newPort);
            } catch (Exception e) {
                // no update
            }
            if (argLength > 4) {
                updateRoot(arg);
            }
        }
        for (String arg : args) {
            if (arg == "-x" || arg == "-h") {
                submitArg(arg);
            }
        }
    }

}
