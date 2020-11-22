import static java.lang.Integer.parseInt;

public class MockCommandLine implements CommandLine{
    public int port;
    public String path;
    public String message;
    public boolean usage;
    public boolean config;
    private String invalidArg;

    public MockCommandLine(int port, String path) {
        this.port = port;
        this.path = path;
        message = null;
        usage = false;
        config = false;
    }

    @Override
    public void getUsageMessage() {
        String p = "  -p     Specify the port.  Default is 80.\n";
        String r = "  -r     Specify the root directory.  Default is the current working directory.\n";
        String h = "  -h     Print this help message\n";
        String x = "  -x     Print the startup configuration without starting the server\n";
        message = p + r + h + x;
        System.out.println(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void updatePort(int newPort) {
        port = newPort;
    }

    public void updateRoot(String newPath) {
        path = newPath;
    }

    @Override
    public void submitEntry(String msg) {

    }

    @Override
    public void submitArg(String arg) {

    }

    public String getHMessage() {
        String p = "  -p     Specify the port.  Default is 80.\n";
        String r = "  -r     Specify the root directory.  Default is the current working directory.\n";
        String h = "  -h     Print this help message\n";
        String x = "  -x     Print the startup configuration without starting the server\n";
        message = p + r + h + x;
        return message;
    }

    public void getXMessage() {
        String name = "Example Server\r\n";
        String portLine = "Running on port: " + port + ".\r\n";
        String filesLine = "Serving files from: " + path;
        message = getConfigMessage();
        System.out.println(message);
    }

    private void getInvalidArgMessage() {
        message = "Invalid option: " + invalidArg;
    }

    @Override
    public void commandLineMessage() {
        if (usage) {
            getUsageMessage();
        } else if (config) {
            getXMessage();
        } else {
            getInvalidArgMessage();
        }
    }

    @Override
    public String getConfigMessage() {
        String name = "Example Server\r\n";
        String portLine = "Running on port: " + port + ".\r\n";
        String filesLine = "Serving files from: " + path;
        message = name + portLine + filesLine;
        System.out.println(message);
        return message;
    }

    @Override
    public void manageArgs(String msg) {
        String[] args = msg.split("[, ] ");
        for (String arg : args) {
            if (msg == "-h") {
                usage = true;
                getUsageMessage();
            }
            else if (msg == "-x" && args.length == 1) {
                config = true;
                getXMessage();
            }
            else {
                int newPort = -1;
                try {
                    newPort = parseInt(arg);
                    if (newPort != -1)
                        updatePort(newPort);
                } catch (Exception e) {
                    if (arg.startsWith("/")) {
                        updateRoot(arg);
                        invalidArg = arg;
                    }
                }
            }
        }
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
}
