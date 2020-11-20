public class ServerCommandLine implements CommandLine {

    private final int port;
    private final String path;
    private String message;

    public ServerCommandLine(int port, String path) {
        this.port = port;
        this.path = path;
        String name = "Gina's HTTP Server\r\n";
        String portLine = "Running on port: " + port + "\r\n";
        String filesLine = "Serving files from: " + path + "\r\n";
        message = name + portLine + filesLine;
    }

    public String getMessage() { return message; }

    public void getUsage() {
        String p = "  -p     Specify the port.  Default is 80. \r\n";
        String r = "  -r     Specify the root directory.  Default is the current working directory. \r\n";
        String h = "  -h     Print this help message \r\n";
        String x = "  -x     Print the startup configuration without starting the server\r\n";
        message = p + r + h + x;
    }

    public void submitEntry(String msg) {
        if(msg == "-h")
            getUsage();
    }
}
