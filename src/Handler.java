import java.io.IOException;

public interface Handler {
    byte[] handle(byte[] message) throws ExceptionInfo, IOException;

    String getRoot();
}
