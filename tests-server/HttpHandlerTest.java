import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class HttpHandlerTest {

    private HttpHandler handler;
    private HttpServer server;


    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        handler = new HttpHandler(3141, "Leo");
        server = new HttpServer(3141, "Leo");
    }

    @Test
    public void isHandler() throws IOException, InterruptedException {
        HttpHandler httpHandler = new HttpHandler(3141, "Rex");
        assertTrue(httpHandler instanceof Handler);
    }

    @Test
    public void initMessage() throws IOException, InterruptedException {
        String message = server.getConfigMessage();

        assertNotNull(handler.getServer());
        assertEquals(message, handler.init());
    }


}

