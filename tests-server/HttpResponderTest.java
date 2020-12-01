import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class HttpResponderTest {
    private HttpResponder responder;
    private HttpParser parser;

    @BeforeEach
    public void setup() {
        responder = new HttpResponder();
        parser = new HttpParser();
    }

    @Test
    public void standardResponse() throws IOException {
        byte[] target = (parser.getHeader() + "Rex\r\n").getBytes();
        byte[] result = responder.respond(parser.getHeader(), "Rex".getBytes());

        assertArrayEquals(target, result);
    }

}
