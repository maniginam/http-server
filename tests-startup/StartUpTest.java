import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class StartUpTest
{

    private StartUp starter;


    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        starter = new StartUp();
    }

    @Test
    public void submitInvalidEntry() {
        String message = "Invalid option: Rex";
        String args[] = new String[1];
        args[0] = "Rex";
        String result = starter.getConfigMessage(args);
        assertEquals(message, result);
    }

    @Test
    public void submitH() throws Exception {
        String p = "  -p     Specify the port.  Default is 80.\n";
        String r = "  -r     Specify the root directory.  Default is the current working directory.\n";
        String h = "  -h     Print this help message\n";
        String x = "  -x     Print the startup configuration without starting the server\n";
        String message = p + r + h + x;
        String result = starter.getUsageMessage();

        assertEquals(message, result);
    }

    @Test
    public void submitXEntry() throws IOException, InterruptedException {
        String name = "Example Server\r\n";
        String portLine = "Running on port: 80.\r\n";
        String filesLine = "Serving files from: /Users/maniginam/server-task/http-spec";
        String message = name + portLine + filesLine;
        String args[] = new String[1];
        args[0] = "-x";

        String result = starter.getConfigMessage(args);

        assertEquals(message, result);
    }

    @Test
    public void submitPEntryWithX() throws Exception {
        String name = "Example Server\r\n";
        String portLine = "Running on port: 3141.\r\n";
        String filesLine = "Serving files from: /Users/maniginam/server-task/http-spec";
        String message = name + portLine + filesLine;
        String args[] = new String[3];
        args[0] = "-x";
        args[1] = "-p";
        args[2] = "3141";

        String result = starter.getConfigMessage(args);

        assertEquals(message, result);

    }

    @Test
    public void submitREntry() throws Exception {
        String name = "Example Server\r\n";
        String portLine = "Running on port: 80.\r\n";
        String filesLine = "Serving files from: /gina/keith/rex/leo";
        String message = name + portLine + filesLine;

        String args[] = new String[3];
        args[0] = "-x";
        args[1] = "-r";
        args[2] = "/gina/keith/rex/leo";

        String result = starter.getConfigMessage(args);

        assertEquals(message, result);
    }

    @Test
    public void messageOnlyFromPEntryWithNOX() throws Exception {
        String name = "Example Server\r\n";
        String portLine = "Running on port: 3141.\r\n";
        String filesLine = "Serving files from: /Users/maniginam/server-task/http-spec";
        String message = name + portLine + filesLine;
        String args[] = new String[2];
        args[0] = "-p";
        args[1] = "3141";

        String result = starter.getConfigMessage(args);

        assertEquals(message, result);

    }
}


