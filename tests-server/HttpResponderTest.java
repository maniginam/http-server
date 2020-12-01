import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpResponderTest {
    private HttpResponder responder;
    private HttpResponse response;
    private HttpParser parser;
    private String root = "/Users/maniginam/server-task/http-spec/testroot";

    @BeforeEach
    public void setup() throws IOException {
        responder = new HttpResponder();
//        server = new HttpServer(3141, "/Users/maniginam/server-task/http-spec/testroot");
        parser = new HttpParser();
    }

    public void standardResponse() throws IOException {
        String request = "GET / HTTP/1.1";
        byte[] result = responder.respond(parser.getHeader(), "Rex".getBytes());

        Path path = Path.of("/Users/maniginam/server-task/http-spec/testroot/index.html");
        String fileContent = Files.readString(path, StandardCharsets.UTF_8);

        assertEquals(fileContent, request);
    }

}
