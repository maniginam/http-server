import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLineTest {


    private HTTPHandler httpHandler;
    private LocalCommandLine commandLine;

    @BeforeEach
    public void setup() {
        commandLine = new LocalCommandLine();

    }

    @Test
    public void usage() {
        String p = "  -p     Specify the port.  Default is 80. \r\n";
        String r = "  -r     Specify the root directory.  Default is the current working directory. \r\n";
        String h = "  -h     Print this help message \r\n";
        String x = "  -x     Print the startup configuration without starting the server\r\n";
        String message = p + r + h + x;

        commandLine.submitEntry("-h");
        assertEquals(message, commandLine.noStart());


    }

}
