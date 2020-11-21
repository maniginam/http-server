import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPHandlerTest {

    private HTTPHandler handler;


    @BeforeEach
    public void setup() {
        HTTPHandler.commandLine = new MockCommandLine();
        handler = new HTTPHandler();
    }

    @Test
    public void isHandler() {
        HTTPHandler httpHandler = new HTTPHandler();
        assertTrue(httpHandler instanceof Handler);
    }

    @Test
    public void noStart() throws IOException, InterruptedException {
        String[] h = new String[1];
        CommandLine commandLine = new LocalCommandLine();
        commandLine.noStart();
        String message = LocalCommandLine.getMessage();
        h[0] = "-h";
        handler.checkArgs(h);

        assertNull(handler.getServer());
        assertEquals(LocalCommandLine.noStart(), message);
    }

    @Test
    public void initMessage() {
        String message = handler.init();

        assertNotNull(handler.getServer());
        assertEquals(handler.getServer().getMessage(), message);
    }

    @Test
    public void submitHEntry() {
        String result = handler.handle("-h");
        assertEquals("HTTP/1.1 200 OK\r\n" + "\r\n" + handler.getServer().getMessage(), result);
    }
}

