import java.io.IOException;

public interface Handler {
    byte[] handle(String header, byte[] body) throws ExceptionInfo, IOException;

    String getRoot();

    int getBodySize();

    void handleHeader(byte[] input);

    String getRequestHeader();

    void setRequestBody(byte[] body);

    byte[] getRequestBody();

    String getResponseHeader();

    byte[] getResponseBody();

    String getResponseBodyMessage();
}
