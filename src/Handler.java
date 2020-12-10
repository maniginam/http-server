import java.io.IOException;
import java.util.Map;

public interface Handler {
    byte[] handle(Map<String, String> header, byte[] body) throws ExceptionInfo, IOException;

    String getRoot();

    int getBodySize();

    void handleHeader(byte[] input) throws IOException;

    String getRequestHeader();

    void setRequestBody(byte[] body);

    byte[] getRequestBody();

    String getResponseHeader();

    byte[] getResponseBody();

    String getResponseBodyMessage();

    byte[] getResponse();

}
