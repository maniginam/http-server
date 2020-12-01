import java.io.IOException;

public class ExceptionInfo extends Exception {

    public byte[] response;
    private HttpParser parser;
    private HttpResponder responder;

    public ExceptionInfo (String request, String response) throws IOException {
        super(response);
        this.setResponse(response);
    }

    public ExceptionInfo (String request, String response, Throwable cause) throws IOException {
        super(response, cause);
        this.setResponse(response);
    }

    public byte[] getReponse() {
        return response;
        }

    public void setResponse(String message) throws IOException {
        responder = new HttpResponder();
        parser = new HttpParser();
        parser.setStatus("", 404, message);
        parser.setHeaderField("", null, "");
        response = responder.respond(parser.getHeader(), message.getBytes());
//                parser.getStatus() + parser.getHeaderField() + message;

    }

}
