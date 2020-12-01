import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HttpResponder {
    public byte[] response;
    public HttpResponder() {

    }

    public byte[] respond(String header, byte[] body) throws IOException {
        byte headerBytes[] = header.getBytes();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(headerBytes);
        output.write(body);
        output.write("\r\n".getBytes());

        byte response[] = output.toByteArray();
        return response;
    }
}
