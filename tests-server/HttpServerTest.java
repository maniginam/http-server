import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {

    private HttpServer server;

    @BeforeEach
    public void setup() {
        server = new HttpServer(3141, "/Users/maniginam/server-task/http-spec");
    }

    @Test
    public void headerMessage() {
        String portLine = "Running on port: 3141";
        String rootLine = "Serving files from: /Users/maniginam/server-task/http-spec";
        String name = "Gina's HTTP Server";
        String message = "<h1>" + name + "<br><small><small><small>" + portLine + "<br>" + rootLine + "</small></small></small></h1>\r\n";

        String result = server.getConfigMessage();

        assertEquals(message, result);
    }
}
