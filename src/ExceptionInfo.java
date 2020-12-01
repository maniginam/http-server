import java.io.IOException;

public class ExceptionInfo extends Exception {

    public String message;
    private HttpParser parser;
    private HttpResponder responder;

    public ExceptionInfo (String request, String message) throws IOException {
        super(message);
        this.setMessage(message);
    }

    public ExceptionInfo (String request, String message, Throwable cause) throws IOException {
        super(message, cause);
        this.setMessage(message);
    }

    public String getMessage() {
        return message;
        }

    public void setMessage(String message) throws IOException {
        responder = new HttpResponder();
        parser = new HttpParser();
        parser.setStatus(404, message);
        parser.setHeaderField(message, null, "");
        this.message = parser.getStatus() + parser.getHeaderField() + message;

    }

}
