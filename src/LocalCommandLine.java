public class CommandLine {

    public static String getUsage() {
        String p = "  -p     Specify the port.  Default is 80. \r\n";
        String r = "  -r     Specify the root directory.  Default is the current working directory. \r\n";
        String h = "  -h     Print this help message \r\n";
        String x = "  -x     Print the startup configuration without starting the server\r\n";
        String message = p + r + h + x;
        return message;
    }

    public static String noStart() {
        return getUsage();
    }

}

