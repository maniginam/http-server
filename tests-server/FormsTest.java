import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FormsTest {

    private HttpHandler handler;
    private HttpServer server;
    private FormFactory formFactory;
    private Form form;

    @BeforeEach
    public void setup() throws IOException {
        handler = new HttpHandler(3141, "/Users/maniginam/server-task/http-spec/testroot");
        server = new HttpServer("/Users/maniginam/server-task/http-spec/testroot");
        formFactory = new FormFactory();
    }

    @Test
    public void withNoBoxes() throws IOException, ExceptionInfo {
        String[] boxes = new String[0];
        form = new Form(boxes);

        String instructions = form.getInstructions();
        assertTrue(instructions.contains("<h2>Get Form</h2>"));
        assertTrue(instructions.contains("<form method=\"get\" action=\"/form\""));
        assertTrue(instructions.contains("<input type=\"submit\" value=\"Submit\""));
    }

    @Test
    public void formWithOneInputBox() throws IOException, ExceptionInfo {
        String[] boxes = new String[1];
        boxes[0] = "Foo";
        form = new Form(boxes);
        String instructions = form.getInstructions();

        assertTrue(instructions.contains("<input type=\"text\" name=\"foo\""));
        assertTrue(instructions.contains("<input type=\"submit\" value=\"Submit\""));
    }

    @Test
    public void formWithFooEntry() throws IOException, ExceptionInfo {
        String request = "GET /form?foo=1&bar=2";
        handler.handle(request);

        String body = handler.getServer().getBodyMessage();

        assertTrue(body.contains("<h2>GET Form</h2>"));
        assertTrue(body.contains("<li>foo: 1</li>"));
        assertTrue(body.contains("<li>bar: 2</li>"));
    }

//    @Test
//    public void formWithTwoInputBoxes() throws IOException, ExceptionInfo {
//        String[] boxes = new String[2];
//        boxes[0] = "Foo";
//        boxes[1] = "Bar";
//        form = new Form(boxes);
//        String instructions = form.getInstructions();
//
//        assertTrue(instructions.contains("<input type=\"text\" name=\"foo\""));
//        assertTrue(instructions.contains("<input type=\"text\" name=\"bar\""));
//        assertTrue(instructions.contains("<input type=\"submit\" value=\"Submit\""));
//    }


}
