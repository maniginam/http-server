import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class HttpHandlerTest {

    private HttpHandler handler;
    private HttpCommandLine httpCommandLine;


    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        handler = new HttpHandler(3141, "Leo");
        httpCommandLine = new HttpCommandLine();
    }

    @Test
    public void isHandler() throws IOException, InterruptedException {
        HttpHandler httpHandler = new HttpHandler(3141, "Rex");
        assertTrue(httpHandler instanceof Handler);
    }

    @Test
    public void initMessage() throws IOException, InterruptedException {
        String message = httpCommandLine.getHeader() + httpCommandLine.getPortLine() + httpCommandLine.getRootLine();

        assertNotNull(handler.getCommandLine());
        assertEquals(message, handler.init());
    }

    @Test
    public void submitInvalidEntry() throws IOException, InterruptedException {
        String result = handler.handle("-gina");
        assertEquals(handler.getCommandLine().getMessage(), result);
    }

    @Test
    public void submitHEntry() throws IOException, InterruptedException {
        String result = handler.handle("-h");
        assertTrue(handler.getCommandLine().usage);
        assertEquals(handler.getCommandLine().getMessage(), result);
    }

    @Test
    public void submitXEntry() throws IOException, InterruptedException {
        String result = handler.handle("-x");
        assertTrue(handler.getCommandLine().config);
        assertEquals(handler.getCommandLine().getMessage(), result);
    }

    @Test
    public void submitPEntry() throws IOException, InterruptedException {
        String result = handler.handle("-x, -p, 3141");
        assertEquals("<small><small><small>Running on port: 3141<br>", handler.getCommandLine().getPortLine());
        assertEquals(handler.getCommandLine().getMessage(), result);
    }

    @Test
    public void submitREntry() throws IOException, InterruptedException {
        String result = handler.handle("-x, -r, /gina/keith/rex/leo");
        assertEquals("Serving files from: /gina/keith/rex/leo</small></small></small></h1>\r\n", handler.getCommandLine().getRootLine());
        assertEquals(handler.getCommandLine().getMessage(), result);
    }

}

