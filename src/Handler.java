import java.io.IOException;

public interface Handler {
    byte[] handle(String message) throws ExceptionInfo, IOException;

    String getRoot();
}
